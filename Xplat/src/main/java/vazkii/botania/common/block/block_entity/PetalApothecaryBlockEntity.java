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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.block.PetalApothecary;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.recipe.CustomApothecaryColor;
import vazkii.botania.api.recipe.PetalApothecaryRecipe;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.gui.HUDHandler;
import vazkii.botania.common.block.PetalApothecaryBlock;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.EntityHelper;
import vazkii.botania.common.helper.InventoryHelper;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PetalApothecaryBlockEntity extends SimpleInventoryBlockEntity implements PetalApothecary {

	private static final int SET_KEEP_TICKS_EVENT = 0;
	private static final int CRAFT_EFFECT_EVENT = 1;

	private List<ItemStack> lastRecipe = null;
	private Ingredient lastReagent = Ingredient.EMPTY;
	private int recipeKeepTicks = 0;

	public PetalApothecaryBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.ALTAR, pos, state);
	}

	public boolean collideEntityItem(ItemEntity item) {
		ItemStack stack = item.getItem();
		if (level.isClientSide || stack.isEmpty() || !item.isAlive()) {
			return false;
		}

		if (getFluid() == State.EMPTY) {
			// XXX: special handling for now since fish buckets don't have fluid cap, may need to be changed later
			if (stack.getItem() instanceof MobBucketItem bucketItem && XplatAbstractions.INSTANCE.getBucketFluid(bucketItem) == Fluids.WATER) {
				setFluid(State.WATER);
				bucketItem.checkExtraContent(null, level, stack, getBlockPos().above()); // Spawns the fish
				item.setItem(new ItemStack(Items.BUCKET));
				return true;
			}

			if (XplatAbstractions.INSTANCE.extractFluidFromItemEntity(item, Fluids.WATER)) {
				setFluid(State.WATER);
				return true;
			}

			if (XplatAbstractions.INSTANCE.extractFluidFromItemEntity(item, Fluids.LAVA)) {
				setFluid(State.LAVA);
				return true;
			}

			return false;
		}

		if (getFluid() == State.LAVA) {
			item.setRemainingFireTicks(100 * 20);
			return true;
		}

		Optional<RecipeHolder<PetalApothecaryRecipe>> maybeRecipe = level.getRecipeManager().getRecipeFor(BotaniaRecipeTypes.PETAL_TYPE, getItemHandler(), level);
		if (maybeRecipe.isPresent()) {
			var recipe = maybeRecipe.get();
			if (recipe.value().getReagent().test(item.getItem())) {
				saveLastRecipe(recipe.value().getReagent());
				ItemStack output = recipe.value().assemble(getItemHandler(), getLevel().registryAccess());

				for (int i = 0; i < inventorySize(); i++) {
					getItemHandler().setItem(i, ItemStack.EMPTY);
				}

				EntityHelper.shrinkItem(item);

				ItemEntity outputItem = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.5, worldPosition.getZ() + 0.5, output);
				XplatAbstractions.INSTANCE.itemFlagsComponent(outputItem).apothecarySpawned = true;
				level.addFreshEntity(outputItem);

				setFluid(State.EMPTY, false);

				level.gameEvent(null, GameEvent.BLOCK_ACTIVATE, getBlockPos());
				level.blockEvent(getBlockPos(), getBlockState().getBlock(), CRAFT_EFFECT_EVENT, 0);
				return true;
			}
		}

		if (!XplatAbstractions.INSTANCE.isFluidContainer(item)
				&& !XplatAbstractions.INSTANCE.itemFlagsComponent(item).apothecarySpawned) {
			if (!getItemHandler().getItem(inventorySize() - 1).isEmpty()) {
				return false;
			}

			if (lastReagent.test(item.getItem())) {
				return false;
			}

			for (int i = 0; i < inventorySize(); i++) {
				if (getItemHandler().getItem(i).isEmpty()) {
					getItemHandler().setItem(i, stack.split(1));
					EntityHelper.syncItem(item);
					level.playSound(null, worldPosition, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.1F, 10F);
					level.gameEvent(null, GameEvent.BLOCK_CHANGE, getBlockPos());
					clearLastRecipe();
					return true;
				}
			}
		}

		return false;
	}

	@Nullable
	private CustomApothecaryColor getFlowerComponent(ItemStack stack) {
		CustomApothecaryColor c = null;
		if (stack.getItem() instanceof CustomApothecaryColor color) {
			c = color;
		}
		return c;
	}

	public void saveLastRecipe(Ingredient reagent) {
		lastRecipe = new ArrayList<>();
		for (int i = 0; i < inventorySize(); i++) {
			ItemStack stack = getItemHandler().getItem(i);
			if (stack.isEmpty()) {
				break;
			}
			lastRecipe.add(stack.copy());
		}
		lastReagent = reagent;
		recipeKeepTicks = 400;
		level.blockEvent(getBlockPos(), getBlockState().getBlock(), SET_KEEP_TICKS_EVENT, 400);
	}

	public void clearLastRecipe() {
		lastRecipe = null;
		lastReagent = Ingredient.EMPTY;
	}

	public InteractionResult trySetLastRecipe(Player player) {
		// lastRecipe is not synced. If we're calling this method we already checked that
		// the apothecary has water and no items, so just optimistically assume
		// success on the client.
		if (player.level().isClientSide()) {
			return InteractionResult.sidedSuccess(true);
		}
		boolean success = InventoryHelper.tryToSetLastRecipe(player, getItemHandler(), lastRecipe, SoundEvents.GENERIC_SPLASH);
		if (success) {
			level.gameEvent(null, GameEvent.BLOCK_CHANGE, getBlockPos());
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
		return success
				? InteractionResult.sidedSuccess(false)
				: InteractionResult.PASS;
	}

	public boolean isEmpty() {
		for (int i = 0; i < inventorySize(); i++) {
			if (!getItemHandler().getItem(i).isEmpty()) {
				return false;
			}
		}

		return true;
	}

	private void tickRecipeKeep() {
		if (recipeKeepTicks > 0) {
			--recipeKeepTicks;
		} else {
			clearLastRecipe();
		}
	}

	public static void serverTick(Level level, BlockPos worldPosition, BlockState state, PetalApothecaryBlockEntity self) {
		List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(
				worldPosition.above()
		));

		boolean didChange = false;
		for (ItemEntity item : items) {
			didChange = self.collideEntityItem(item) || didChange;
		}

		if (didChange) {
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(self);
		}

		self.tickRecipeKeep();
	}

	public static void clientTick(Level level, BlockPos worldPosition, BlockState state, PetalApothecaryBlockEntity self) {
		for (int i = 0; i < self.inventorySize(); i++) {
			ItemStack stackAt = self.getItemHandler().getItem(i);
			if (stackAt.isEmpty()) {
				break;
			}

			if (Math.random() >= 0.97) {
				CustomApothecaryColor comp = self.getFlowerComponent(stackAt);

				int color = comp == null ? 0x888888 : comp.getParticleColor(stackAt);
				float red = (color >> 16 & 0xFF) / 255F;
				float green = (color >> 8 & 0xFF) / 255F;
				float blue = (color & 0xFF) / 255F;
				if (Math.random() >= 0.75F) {
					level.playSound(null, worldPosition, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.1F, 10F);
				}
				SparkleParticleData data = SparkleParticleData.sparkle((float) Math.random(), red, green, blue, 10);
				level.addParticle(data, worldPosition.getX() + 0.5 + Math.random() * 0.4 - 0.2, worldPosition.getY() + 1.2, worldPosition.getZ() + 0.5 + Math.random() * 0.4 - 0.2, 0, 0, 0);
			}
		}

		if (self.getFluid() == State.LAVA) {
			level.addParticle(ParticleTypes.SMOKE, worldPosition.getX() + 0.5 + Math.random() * 0.4 - 0.2, worldPosition.getY() + 1, worldPosition.getZ() + 0.5 + Math.random() * 0.4 - 0.2, 0, 0.05, 0);
			if (Math.random() > 0.9) {
				level.addParticle(ParticleTypes.LAVA, worldPosition.getX() + 0.5 + Math.random() * 0.4 - 0.2, worldPosition.getY() + 1, worldPosition.getZ() + 0.5 + Math.random() * 0.4 - 0.2, 0, 0.01, 0);
			}
		}

		self.tickRecipeKeep();
	}

	@Override
	public boolean triggerEvent(int id, int param) {
		switch (id) {
			case SET_KEEP_TICKS_EVENT:
				recipeKeepTicks = param;
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
					level.playLocalSound(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), BotaniaSounds.altarCraft, SoundSource.BLOCKS, 1F, 1F, false);
				}
				return true;
			}
			default:
				return super.triggerEvent(id, param);
		}
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
	public void setFluid(State fluid) {
		setFluid(fluid, true);
	}

	public void setFluid(State fluid, boolean withVibration) {
		if (withVibration) {
			level.gameEvent(null, fluid == State.EMPTY ? GameEvent.FLUID_PICKUP : GameEvent.FLUID_PLACE, getBlockPos());
		}
		level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(PetalApothecaryBlock.FLUID, fluid));
	}

	@Override
	public State getFluid() {
		return getBlockState().getValue(PetalApothecaryBlock.FLUID);
	}

	public boolean canAddLastRecipe() {
		return this.isEmpty() && this.getFluid() == State.WATER;
	}

	public static class Hud {
		public static void render(PetalApothecaryBlockEntity altar, GuiGraphics gui, Minecraft mc) {
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

				Optional<RecipeHolder<PetalApothecaryRecipe>> maybeRecipe = altar.level.getRecipeManager()
						.getRecipeFor(BotaniaRecipeTypes.PETAL_TYPE, altar.getItemHandler(), altar.level);
				maybeRecipe.ifPresent(recipe -> {
					RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
					RenderHelper.drawTexturedModalRect(gui, HUDHandler.manaBar, xc + radius + 9, yc - 8, 0, 8, 22, 15);

					ItemStack stack = recipe.value().assemble(altar.getItemHandler(), altar.getLevel().registryAccess());
					gui.renderFakeItem(stack, xc + radius + 32, yc - 8);

					var reagents = recipe.value().getReagent().getItems();
					ItemStack reagent;
					if (reagents.length == 0) {
						reagent = new ItemStack(Items.BARRIER);
					} else {
						int idx = (int) ((altar.level.getGameTime() / 20) % reagents.length);
						reagent = reagents[idx];
					}
					gui.renderFakeItem(reagent, xc + radius + 16, yc + 6);
					gui.drawString(mc.font, "+", xc + radius + 14, yc + 10, 0xFFFFFF, false);
				});

				for (int i = 0; i < amt; i++) {
					double xPos = xc + Math.cos(angle * Math.PI / 180D) * radius - 8;
					double yPos = yc + Math.sin(angle * Math.PI / 180D) * radius - 8;
					PoseStack pose = RenderSystem.getModelViewStack();
					pose.pushPose();
					pose.translate(xPos, yPos, 0);
					RenderSystem.applyModelViewMatrix();
					gui.renderFakeItem(altar.getItemHandler().getItem(i), 0, 0);
					pose.popPose();
					RenderSystem.applyModelViewMatrix();

					angle += anglePer;
				}
			}
			if (altar.recipeKeepTicks > 0 && altar.canAddLastRecipe()) {
				String s = I18n.get("botaniamisc.altarRefill0");
				gui.drawString(mc.font, s, xc - mc.font.width(s) / 2, yc + 10, 0xFFFFFF, false);
				s = I18n.get("botaniamisc.altarRefill1");
				gui.drawString(mc.font, s, xc - mc.font.width(s) / 2, yc + 20, 0xFFFFFF, false);
			}
		}
	}

}
