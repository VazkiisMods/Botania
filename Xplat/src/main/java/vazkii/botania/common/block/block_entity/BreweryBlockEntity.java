/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.block.WandHUD;
import vazkii.botania.api.brew.BrewContainer;
import vazkii.botania.api.brew.BrewItem;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.api.recipe.BotanicalBreweryRecipe;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.brew.BotaniaBrews;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.EntityHelper;

import java.util.List;
import java.util.Optional;

public class BreweryBlockEntity extends SimpleInventoryBlockEntity implements ManaReceiver {
	private static final String TAG_MANA = "mana";
	private static final int CRAFT_EFFECT_EVENT = 0;

	public BotanicalBreweryRecipe recipe;
	private int mana = 0;
	private int manaLastTick = 0;
	public int signal = 0;

	public BreweryBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.BREWERY, pos, state);
	}

	public boolean addItem(@Nullable Player player, ItemStack stack, @Nullable InteractionHand hand) {
		if (recipe != null || stack.isEmpty() || stack.getItem() instanceof BrewItem brew && brew.getBrew(stack) != null && brew.getBrew(stack) != BotaniaBrews.fallbackBrew || getItemHandler().getItem(0).isEmpty() != stack.getItem() instanceof BrewContainer) {
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
					if (stack.isEmpty() && player != null) {
						player.setItemInHand(hand, ItemStack.EMPTY);
					}
				}

				break;
			}
		}

		if (did) {
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
			findRecipe();
		}

		return true;
	}

	private void findRecipe() {
		/*TODO we need the RecipeInput for this
		Optional<RecipeHolder<BotanicalBreweryRecipe>> maybeRecipe = level.getRecipeManager().getRecipeFor(BotaniaRecipeTypes.BREW_TYPE, getItemHandler(), level);
		maybeRecipe.ifPresent(recipeBrew -> {
			this.recipe = recipeBrew.value();
			level.setBlockAndUpdate(worldPosition, BotaniaBlocks.brewery.defaultBlockState().setValue(BlockStateProperties.POWERED, true));
		});

		 */
	}

	public static void commonTick(Level level, BlockPos worldPosition, BlockState state, BreweryBlockEntity self) {
		if (self.mana > 0 && self.recipe == null) {
			self.findRecipe();

			if (self.recipe == null) {
				self.mana = 0;
			}
		}

		// Update every tick.
		self.receiveMana(0);

		if (!level.isClientSide && self.recipe == null) {
			List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 1, worldPosition.getZ() + 1));
			for (ItemEntity item : items) {
				if (item.isAlive() && !item.getItem().isEmpty()) {
					ItemStack stack = item.getItem();
					if (self.addItem(null, stack, null)) {
						EntityHelper.syncItem(item);
					}
				}
			}
		}

		if (self.recipe != null) {
			if (/*!self.recipe.matches(self.getItemHandler(), level)*/ false) { //This also needs the recipe Input
				self.recipe = null;
				level.setBlockAndUpdate(worldPosition, BotaniaBlocks.brewery.defaultBlockState());
			}

			if (self.recipe != null) {
				if (self.mana != self.manaLastTick) {
					int color = self.recipe.getBrew().getColor(self.getItemHandler().getItem(0));
					float r = (color >> 16 & 0xFF) / 255F;
					float g = (color >> 8 & 0xFF) / 255F;
					float b = (color & 0xFF) / 255F;
					for (int i = 0; i < 5; i++) {
						WispParticleData data1 = WispParticleData.wisp(0.1F + (float) Math.random() * 0.05F, r, g, b);
						level.addParticle(data1, worldPosition.getX() + 0.7 - Math.random() * 0.4, worldPosition.getY() + 0.9 - Math.random() * 0.2, worldPosition.getZ() + 0.7 - Math.random() * 0.4, 0.03F - (float) Math.random() * 0.06F, 0.03F + (float) Math.random() * 0.015F, 0.03F - (float) Math.random() * 0.06F);
						for (int j = 0; j < 2; j++) {
							WispParticleData data = WispParticleData.wisp(0.1F + (float) Math.random() * 0.2F, 0.2F, 0.2F, 0.2F);
							level.addParticle(data, worldPosition.getX() + 0.7 - Math.random() * 0.4, worldPosition.getY() + 0.9 - Math.random() * 0.2, worldPosition.getZ() + 0.7 - Math.random() * 0.4, 0.03F - (float) Math.random() * 0.06F, 0.03F + (float) Math.random() * 0.015F, 0.03F - (float) Math.random() * 0.06F);
						}
					}
				}

				if (self.mana >= self.getManaCost() && !level.isClientSide) {
					int mana = self.getManaCost();
					self.receiveMana(-mana);

					ItemStack output = self.recipe.getOutput(self.getItemHandler().getItem(0));
					ItemEntity outputItem = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.5, worldPosition.getZ() + 0.5, output);
					level.addFreshEntity(outputItem);
					level.blockEvent(worldPosition, BotaniaBlocks.brewery, CRAFT_EFFECT_EVENT, self.recipe.getBrew().getColor(output));

					for (int i = 0; i < self.inventorySize(); i++) {
						self.getItemHandler().setItem(i, ItemStack.EMPTY);
					}
				}
			}
		}

		int newSignal = 0;
		if (self.recipe != null) {
			newSignal++;
		}

		if (newSignal != self.signal) {
			self.signal = newSignal;
			level.updateNeighbourForOutputSignal(worldPosition, state.getBlock());
		}

		self.manaLastTick = self.mana;
	}

	@Override
	public boolean triggerEvent(int event, int param) {
		if (event == CRAFT_EFFECT_EVENT) {
			if (level.isClientSide) {
				for (int i = 0; i < 25; i++) {
					float r = (param >> 16 & 0xFF) / 255F;
					float g = (param >> 8 & 0xFF) / 255F;
					float b = (param & 0xFF) / 255F;
					SparkleParticleData data1 = SparkleParticleData.sparkle((float) Math.random() * 2F + 0.5F, r, g, b, 10);
					level.addParticle(data1, worldPosition.getX() + 0.5 + Math.random() * 0.4 - 0.2, worldPosition.getY() + 1, worldPosition.getZ() + 0.5 + Math.random() * 0.4 - 0.2, 0, 0, 0);
					for (int j = 0; j < 2; j++) {
						WispParticleData data = WispParticleData.wisp(0.1F + (float) Math.random() * 0.2F, 0.2F, 0.2F, 0.2F);
						level.addParticle(data, worldPosition.getX() + 0.7 - Math.random() * 0.4, worldPosition.getY() + 0.9 - Math.random() * 0.2, worldPosition.getZ() + 0.7 - Math.random() * 0.4, 0.05F - (float) Math.random() * 0.1F, 0.05F + (float) Math.random() * 0.03F, 0.05F - (float) Math.random() * 0.1F);
					}
				}
				level.playLocalSound(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), BotaniaSounds.potionCreate, SoundSource.BLOCKS, 1F, 1.5F + (float) Math.random() * 0.25F, false);
			}
			return true;
		} else {
			return super.triggerEvent(event, param);
		}
	}

	public int getManaCost() {
		ItemStack stack = getItemHandler().getItem(0);
		if (recipe == null || stack.isEmpty() || !(stack.getItem() instanceof BrewContainer container)) {
			return 0;
		}
		return container.getManaCost(recipe.getBrew(), stack);
	}

	@Override
	public void writePacketNBT(CompoundTag tag, HolderLookup.Provider registries) {
		super.writePacketNBT(tag, registries);

		tag.putInt(TAG_MANA, mana);
	}

	@Override
	public void readPacketNBT(CompoundTag tag, HolderLookup.Provider registries) {
		super.readPacketNBT(tag, registries);

		mana = tag.getInt(TAG_MANA);
	}

	@Override
	protected SimpleContainer createItemHandler() {
		return new SimpleContainer(7) {
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
		return mana >= getManaCost();
	}

	@Override
	public void receiveMana(int mana) {
		this.mana = Math.min(this.mana + mana, getManaCost());
	}

	@Override
	public boolean canReceiveManaFromBursts() {
		return !isFull();
	}

	public static class WandHud implements WandHUD {
		private final BreweryBlockEntity brewery;

		public WandHud(BreweryBlockEntity brewery) {
			this.brewery = brewery;
		}

		@Override
		public void renderHUD(GuiGraphics gui, Minecraft mc) {
			int manaToGet = brewery.getManaCost();
			if (manaToGet > 0) {
				if (brewery.recipe == null) {
					return;
				}

				int x = mc.getWindow().getGuiScaledWidth() / 2 + 8;
				int y = mc.getWindow().getGuiScaledHeight() / 2 - 12;

				RenderHelper.renderHUDBox(gui, x, y, x + 24, y + 24);
				RenderHelper.renderProgressPie(gui, x + 4, y + 4, (float) brewery.mana / (float) manaToGet,
						brewery.recipe.getOutput(brewery.getItemHandler().getItem(0)));
			}
		}
	}

}
