/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import vazkii.botania.api.block.Wandable;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.api.recipe.RunicAltarRecipe;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.client.gui.HUDHandler;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.EntityHelper;
import vazkii.botania.common.helper.InventoryHelper;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.WandOfTheForestItem;
import vazkii.botania.common.item.material.RuneItem;
import vazkii.botania.common.proxy.Proxy;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RunicAltarBlockEntity extends SimpleInventoryBlockEntity implements ManaReceiver, Wandable {
	private static final String TAG_MANA = "mana";
	private static final String TAG_MANA_TO_GET = "manaToGet";
	private static final int SET_KEEP_TICKS_EVENT = 0;
	private static final int SET_COOLDOWN_EVENT = 1;
	private static final int CRAFT_EFFECT_EVENT = 2;

	private RunicAltarRecipe currentRecipe;

	public int manaToGet = 0;
	private int mana = 0;
	private int cooldown = 0;
	public int signal = 0;

	private List<ItemStack> lastRecipe = null;
	private int recipeKeepTicks = 0;

	public RunicAltarBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.RUNE_ALTAR, pos, state);
	}

	public boolean addItem(@Nullable Player player, ItemStack stack, @Nullable InteractionHand hand) {
		if (cooldown > 0 || stack.getItem() instanceof WandOfTheForestItem || stack.is(BotaniaItems.lexicon)) {
			return false;
		}

		if (stack.is(BotaniaBlocks.livingrock.asItem())) {
			if (!level.isClientSide) {
				ItemStack toSpawn = player != null && player.getAbilities().instabuild ? stack.copy().split(1) : stack.split(1);
				ItemEntity item = new ItemEntity(level, getBlockPos().getX() + 0.5, getBlockPos().getY() + 1, getBlockPos().getZ() + 0.5, toSpawn);
				item.setPickUpDelay(40);
				item.setDeltaMovement(Vec3.ZERO);
				level.addFreshEntity(item);
			}

			return true;
		}

		if (manaToGet != 0) {
			return false;
		}

		boolean did = false;

		for (int i = 0; i < inventorySize(); i++) {
			if (getItemHandler().getItem(i).isEmpty()) {
				did = true;
				ItemStack stackToAdd = stack.copyWithCount(1);
				getItemHandler().setItem(i, stackToAdd);

				if (player == null || !player.getAbilities().instabuild) {
					stack.shrink(1);
				}

				break;
			}
		}

		if (did) {
			level.gameEvent(null, GameEvent.BLOCK_CHANGE, getBlockPos());
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}

		return true;
	}

	@Override
	public boolean triggerEvent(int id, int param) {
		switch (id) {
			case SET_KEEP_TICKS_EVENT:
				recipeKeepTicks = param;
				return true;
			case SET_COOLDOWN_EVENT:
				cooldown = param;
				return true;
			case CRAFT_EFFECT_EVENT: {
				if (level.isClientSide) {
					for (int i = 0; i < 25; i++) {
						float red = (float) Math.random();
						float green = (float) Math.random();
						float blue = (float) Math.random();
						SparkleParticleData data = SparkleParticleData.sparkle((float) Math.random(), red, green, blue, 10);
						level.addParticle(data, worldPosition.getX() + 0.5 + Math.random() * 0.4 - 0.2, worldPosition.getY() + 1, worldPosition.getZ() + 0.5 + Math.random() * 0.4 - 0.2, 0, 0, 0);
					}
					level.playLocalSound(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), BotaniaSounds.runeAltarCraft, SoundSource.BLOCKS, 1F, 1F, false);
				}
				return true;
			}
			default:
				return super.triggerEvent(id, param);
		}
	}

	private void tickCooldown() {
		if (cooldown > 0) {
			cooldown--;
		}

		if (recipeKeepTicks > 0) {
			--recipeKeepTicks;
		} else {
			lastRecipe = null;
		}
	}

	public static void serverTick(Level level, BlockPos worldPosition, BlockState state, RunicAltarBlockEntity self) {
		if (self.manaToGet == 0) {
			List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition, worldPosition.offset(1, 1, 1)));
			for (ItemEntity item : items) {
				if (item.isAlive() && !item.getItem().isEmpty() && !item.getItem().is(BotaniaBlocks.livingrock.asItem())
						&& !XplatAbstractions.INSTANCE.itemFlagsComponent(item).runicAltarSpawned) {
					ItemStack stack = item.getItem();
					if (self.addItem(null, stack, null)) {
						EntityHelper.syncItem(item);
					}
				}
			}
		}

		int newSignal = 0;
		if (self.manaToGet > 0) {
			newSignal++;
			if (self.mana >= self.manaToGet) {
				newSignal++;
			}
		}

		if (newSignal != self.signal) {
			self.signal = newSignal;
			level.updateNeighbourForOutputSignal(worldPosition, state.getBlock());
		}

		self.updateRecipe();
		self.tickCooldown();
	}

	public static void clientTick(Level level, BlockPos worldPosition, BlockState state, RunicAltarBlockEntity self) {
		if (self.manaToGet > 0 && self.mana >= self.manaToGet && level.random.nextInt(20) == 0) {
			Vec3 vec = Vec3.atCenterOf(self.getBlockPos());
			Vec3 endVec = vec.add(0, 2.5, 0);
			Proxy.INSTANCE.lightningFX(level, vec, endVec, 2F, 0x00948B, 0x00E4D7);
		}

		if (self.cooldown > 0) {
			WispParticleData data = WispParticleData.wisp(0.2F, 0.2F, 0.2F, 0.2F, 1);
			level.addParticle(data, worldPosition.getX() + Math.random(), worldPosition.getY() + 0.8, worldPosition.getZ() + Math.random(), 0, - -0.025F, 0);
		}
		self.tickCooldown();
	}

	private void updateRecipe() {
		int manaToGet = this.manaToGet;

		if (currentRecipe != null) {
			this.manaToGet = currentRecipe.getManaUsage();
		} else {
			this.manaToGet = level.getRecipeManager().getRecipeFor(BotaniaRecipeTypes.RUNE_TYPE, getItemHandler(), level)
					.map(RunicAltarRecipe::getManaUsage)
					.orElse(0);
		}

		if (manaToGet != this.manaToGet) {
			level.playSound(null, worldPosition, BotaniaSounds.runeAltarStart, SoundSource.BLOCKS, 1F, 1F);
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}

	private void saveLastRecipe() {
		lastRecipe = new ArrayList<>();
		for (int i = 0; i < inventorySize(); i++) {
			ItemStack stack = getItemHandler().getItem(i);
			if (stack.isEmpty()) {
				break;
			}
			lastRecipe.add(stack.copy());
		}
		recipeKeepTicks = 400;
		level.blockEvent(getBlockPos(), BotaniaBlocks.runeAltar, SET_KEEP_TICKS_EVENT, 400);
	}

	public InteractionResult trySetLastRecipe(Player player) {
		// lastRecipe is not synced. If we're calling this method we already checked that
		// the altar has no items, so just optimistically assume success on the client.
		if (player.level().isClientSide()) {
			return InteractionResult.sidedSuccess(true);
		}
		boolean success = InventoryHelper.tryToSetLastRecipe(player, getItemHandler(), lastRecipe, null);
		if (success) {
			level.gameEvent(null, GameEvent.BLOCK_CHANGE, getBlockPos());
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
		return success
				? InteractionResult.sidedSuccess(false)
				: InteractionResult.PASS;
	}

	@Override
	public boolean onUsedByWand(@Nullable Player player, ItemStack wand, Direction side) {
		if (level.isClientSide) {
			return true;
		}

		RunicAltarRecipe recipe = null;

		if (currentRecipe != null) {
			recipe = currentRecipe;
		} else {
			Optional<RunicAltarRecipe> maybeRecipe = level.getRecipeManager().getRecipeFor(BotaniaRecipeTypes.RUNE_TYPE, getItemHandler(), level);
			if (maybeRecipe.isPresent()) {
				recipe = maybeRecipe.get();
			}
		}

		if (recipe != null && manaToGet > 0 && mana >= manaToGet) {
			List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition, worldPosition.offset(1, 1, 1)));
			ItemEntity livingrock = null;
			for (ItemEntity item : items) {
				if (item.isAlive() && !item.getItem().isEmpty() && item.getItem().is(BotaniaBlocks.livingrock.asItem())) {
					livingrock = item;
					break;
				}
			}

			if (livingrock != null) {
				int mana = recipe.getManaUsage();
				receiveMana(-mana);
				ItemStack output = recipe.assemble(getItemHandler(), getLevel().registryAccess());
				ItemEntity outputItem = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.5, worldPosition.getZ() + 0.5, output);
				XplatAbstractions.INSTANCE.itemFlagsComponent(outputItem).runicAltarSpawned = true;
				level.addFreshEntity(outputItem);
				currentRecipe = null;
				level.gameEvent(null, GameEvent.BLOCK_ACTIVATE, getBlockPos());
				level.blockEvent(getBlockPos(), BotaniaBlocks.runeAltar, SET_COOLDOWN_EVENT, 60);
				level.blockEvent(getBlockPos(), BotaniaBlocks.runeAltar, CRAFT_EFFECT_EVENT, 0);

				saveLastRecipe();
				for (int i = 0; i < inventorySize(); i++) {
					ItemStack stack = getItemHandler().getItem(i);
					if (!stack.isEmpty()) {
						if (stack.getItem() instanceof RuneItem && (player == null || !player.getAbilities().instabuild)) {
							ItemEntity outputRune = new ItemEntity(level, getBlockPos().getX() + 0.5, getBlockPos().getY() + 1.5, getBlockPos().getZ() + 0.5, stack.copy());
							XplatAbstractions.INSTANCE.itemFlagsComponent(outputRune).runicAltarSpawned = true;
							level.addFreshEntity(outputRune);
						}

						getItemHandler().setItem(i, ItemStack.EMPTY);
					}
				}

				EntityHelper.shrinkItem(livingrock);
			}
		}

		return true;
	}

	public boolean isEmpty() {
		for (int i = 0; i < inventorySize(); i++) {
			if (!getItemHandler().getItem(i).isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void writePacketNBT(CompoundTag tag) {
		super.writePacketNBT(tag);

		tag.putInt(TAG_MANA, mana);
		tag.putInt(TAG_MANA_TO_GET, manaToGet);
	}

	@Override
	public void readPacketNBT(CompoundTag tag) {
		super.readPacketNBT(tag);

		mana = tag.getInt(TAG_MANA);
		manaToGet = tag.getInt(TAG_MANA_TO_GET);
	}

	@Override
	protected SimpleContainer createItemHandler() {
		return new SimpleContainer(16) {
			@Override
			public int getMaxStackSize() {
				return 1;
			}
		};
	}

	@Override
	public Level getManaReceiverLevel() {
		return getLevel();
	}

	@Override
	public BlockPos getManaReceiverPos() {
		return getBlockPos();
	}

	@Override
	public int getCurrentMana() {
		return mana;
	}

	@Override
	public boolean isFull() {
		return mana >= manaToGet;
	}

	@Override
	public void receiveMana(int mana) {
		this.mana = Math.min(this.mana + mana, manaToGet);
	}

	@Override
	public boolean canReceiveManaFromBursts() {
		return !isFull();
	}

	public boolean canAddLastRecipe() {
		return this.isEmpty();
	}

	public static class Hud {
		public static void render(RunicAltarBlockEntity altar, GuiGraphics gui, Minecraft mc) {
			PoseStack ms = gui.pose();
			int xc = mc.getWindow().getGuiScaledWidth() / 2;
			int yc = mc.getWindow().getGuiScaledHeight() / 2;

			float angle = -90;
			int radius = 24;
			int amt = 0;
			for (int i = 0; i < altar.inventorySize(); i++) {
				if (altar.getItemHandler().getItem(i).isEmpty()) {
					break;
				}
				amt++;
			}

			if (amt > 0) {
				float anglePer = 360F / amt;
				altar.level.getRecipeManager().getRecipeFor(BotaniaRecipeTypes.RUNE_TYPE, altar.getItemHandler(), altar.level).ifPresent(recipe -> {
					RenderSystem.enableBlend();
					RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

					float progress = (float) altar.mana / (float) altar.manaToGet;

					RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
					RenderHelper.drawTexturedModalRect(gui, HUDHandler.manaBar, xc + radius + 9, yc - 8, progress == 1F ? 0 : 22, 8, 22, 15);

					if (progress == 1F) {
						gui.renderFakeItem(new ItemStack(BotaniaBlocks.livingrock), xc + radius + 16, yc + 8);
						ms.pushPose();
						ms.translate(0, 0, 100);
						// If the player is holding a WandOfTheForestItem or has one in their inventory, render that instead of a generic twigWand
						ItemStack playerWand = PlayerHelper.getFirstHeldItemClass(mc.player, WandOfTheForestItem.class);
						if (playerWand.isEmpty()) {
							playerWand = PlayerHelper.getItemClassFromInventory(mc.player, WandOfTheForestItem.class);
						}
						ItemStack wandToRender = playerWand.isEmpty() ? new ItemStack(BotaniaItems.twigWand) : playerWand;
						gui.renderFakeItem(wandToRender, xc + radius + 24, yc + 8);
						ms.popPose();
					}

					ItemStack output = recipe.assemble(altar.getItemHandler(), altar.getLevel().registryAccess());
					RenderHelper.renderProgressPie(gui, xc + radius + 32, yc - 8, progress, output);

					if (progress == 1F) {
						gui.drawString(mc.font, "+", xc + radius + 14, yc + 12, 0xFFFFFF, false);
					}
				});

				for (int i = 0; i < amt; i++) {
					double xPos = xc + Math.cos(angle * Math.PI / 180D) * radius - 8;
					double yPos = yc + Math.sin(angle * Math.PI / 180D) * radius - 8;
					ms.pushPose();
					ms.translate(xPos, yPos, 0);
					gui.renderFakeItem(altar.getItemHandler().getItem(i), 0, 0);
					ms.popPose();

					angle += anglePer;
				}
			}
			if (altar.recipeKeepTicks > 0 && altar.canAddLastRecipe()) {
				String s = I18n.get("botaniamisc.altarRefill0");
				if (s != null && !s.isEmpty()) {
					gui.drawString(mc.font, s, xc - mc.font.width(s) / 2, yc + 10, 0xFFFFFF);
				}
				s = I18n.get("botaniamisc.altarRefill1");
				if (s != null && !s.isEmpty()) {
					gui.drawString(mc.font, s, xc - mc.font.width(s) / 2, yc + 20, 0xFFFFFF);
				}
			}
		}
	}

	public int getTargetMana() {
		return manaToGet;
	}

}
