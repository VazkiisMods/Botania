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
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.block.WandHUD;
import vazkii.botania.api.block.Wandable;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.state.BotaniaStateProperties;
import vazkii.botania.api.state.enums.CraftyCratePattern;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.*;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class CraftyCrateBlockEntity extends OpenCrateBlockEntity implements Wandable {
	private static final String TAG_CRAFTING_RESULT = "craft_result";

	private static int recipeEpoch = 0;

	private int signal = 0;
	private ItemStack craftResult = ItemStack.EMPTY;

	private final Queue<ResourceLocation> lastRecipes = new ArrayDeque<>();
	private boolean dirty;
	private boolean matchFailed;
	private int lastRecipeEpoch = recipeEpoch;

	public static void registerListener() {
		XplatAbstractions.INSTANCE.registerReloadListener(PackType.SERVER_DATA, botaniaRL("craft_crate_epoch_counter"),
				(ResourceManagerReloadListener) mgr -> recipeEpoch++);
	}

	public CraftyCrateBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.CRAFT_CRATE, pos, state);
	}

	@Override
	protected SimpleContainer createItemHandler() {
		return new SimpleContainer(9) {
			@Override
			public int getMaxStackSize() {
				return 1;
			}

			@Override
			public boolean canPlaceItem(int slot, ItemStack stack) {
				return !isLocked(slot);
			}
		};
	}

	public CraftyCratePattern getPattern() {
		BlockState state = getBlockState();
		if (!state.is(BotaniaBlocks.craftCrate)) {
			return CraftyCratePattern.NONE;
		}
		return state.getValue(BotaniaStateProperties.CRATE_PATTERN);
	}

	private boolean isLocked(int slot) {
		return !getPattern().openSlots.get(slot);
	}

	@Override
	public void readPacketNBT(CompoundTag tag, HolderLookup.Provider registries) {
		super.readPacketNBT(tag, registries);
		craftResult = ItemStack.EMPTY /*todo ItemStack.of(tag.getCompound(TAG_CRAFTING_RESULT))*/;
	}

	@Override
	public void writePacketNBT(CompoundTag tag, HolderLookup.Provider registries) {
		super.writePacketNBT(tag, registries);
		tag.put(TAG_CRAFTING_RESULT, /*craftResult.save(new CompoundTag())*/ new CompoundTag());
	}

	public static void serverTick(Level level, BlockPos worldPosition, BlockState state, CraftyCrateBlockEntity self) {
		if (recipeEpoch != self.lastRecipeEpoch) {
			self.lastRecipeEpoch = recipeEpoch;
			self.matchFailed = false;
		}

		if (!self.matchFailed && self.canEject() && self.isFull() && self.craft(true)) {
			self.ejectAll();
		}

		int newSignal = 0;
		for (; newSignal < 9; newSignal++) // dis for loop be derpy
		{
			if (!self.isLocked(newSignal) && self.getItemHandler().getItem(newSignal).isEmpty()) {
				break;
			}
		}

		if (newSignal != self.signal) {
			self.signal = newSignal;
			level.updateNeighbourForOutputSignal(worldPosition, state.getBlock());
		}

		if (self.dirty) {
			self.dirty = false;
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(self);
		}
	}

	private boolean craft(boolean fullCheck) {
		level.getProfiler().push("craft");
		if (fullCheck && !isFull()) {
			return false;
		}

		CraftingContainer craft = new TransientCraftingContainer(new AbstractContainerMenu(MenuType.CRAFTING, -1) {
			@NotNull
			@Override
			public ItemStack quickMoveStack(@NotNull Player player, int i) {
				return ItemStack.EMPTY;
			}

			@Override
			public boolean stillValid(@NotNull Player player) {
				return false;
			}
		}, 3, 3);
		for (int i = 0; i < craft.getContainerSize(); i++) {
			ItemStack stack = getItemHandler().getItem(i);

			if (stack.isEmpty() || isLocked(i) || stack.is(BotaniaItems.placeholder)) {
				continue;
			}

			craft.setItem(i, stack);
		}

		Optional<RecipeHolder<CraftingRecipe>> matchingRecipe = getMatchingRecipe(craft);
		matchingRecipe.ifPresent(recipe -> {
			CraftingInput input = CraftingInput.of(craft.getWidth(), craft.getHeight(), craft.getItems());
			craftResult = recipe.value().assemble(input, this.getLevel().registryAccess());

			// Given some mods can return air by a bad implementation of their recipe handler,
			// check for air before continuting on.
			if (craftResult.isEmpty()) {
				// We have air, do not continue.
				matchFailed = true;
				return;
			}

			Container handler = getItemHandler();
			List<ItemStack> remainders = recipe.value().getRemainingItems(input);

			for (int i = 0; i < craft.getContainerSize(); i++) {
				ItemStack s = remainders.get(i);
				ItemStack inSlot = handler.getItem(i);
				if ((inSlot.isEmpty() && s.isEmpty())
						|| (!inSlot.isEmpty() && inSlot.is(BotaniaItems.placeholder))) {
					continue;
				}
				handler.setItem(i, s);
			}
		});
		if (matchingRecipe.isEmpty()) {
			matchFailed = true;
		}

		level.getProfiler().pop();
		// Return only if present and not air.
		return matchingRecipe.isPresent() && !craftResult.isEmpty();
	}

	private Optional<RecipeHolder<CraftingRecipe>> getMatchingRecipe(CraftingContainer craft) {
		CraftingInput input = CraftingInput.of(craft.getWidth(), craft.getHeight(), craft.getItems());
		for (ResourceLocation currentRecipe : lastRecipes) {
			var holder = BotaniaRecipeTypes.getRecipe(level, currentRecipe, RecipeType.CRAFTING);
			if (holder.isPresent() && holder.get().value().matches(input, level)) {
				return holder;
			}
		}
		Optional<RecipeHolder<CraftingRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level);
		if (recipe.isPresent()) {
			if (lastRecipes.size() >= 8) {
				lastRecipes.remove();
			}
			lastRecipes.add(recipe.get().id());
			return recipe;
		}
		return Optional.empty();
	}

	boolean isFull() {
		for (int i = 0; i < getItemHandler().getContainerSize(); i++) {
			if (!isLocked(i) && getItemHandler().getItem(i).isEmpty()) {
				return false;
			}
		}

		return true;
	}

	private void ejectAll() {
		for (int i = 0; i < inventorySize(); ++i) {
			ItemStack stack = getItemHandler().getItem(i);
			if (!stack.isEmpty()) {
				eject(stack, false);
				getItemHandler().setItem(i, ItemStack.EMPTY);
			}
		}
		if (!craftResult.isEmpty()) {
			eject(craftResult, false);
			craftResult = ItemStack.EMPTY;
		}
	}

	@Override
	public boolean onUsedByWand(@Nullable Player player, ItemStack stack, Direction side) {
		if (!getLevel().isClientSide && canEject()) {
			craft(false);
			ejectAll();
		}
		return true;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level != null && !level.isClientSide) {
			this.dirty = true;
			this.matchFailed = false;
		}
	}

	public int getSignal() {
		return signal;
	}

	public static class WandHud implements WandHUD {
		private final CraftyCrateBlockEntity crate;

		public WandHud(CraftyCrateBlockEntity crate) {
			this.crate = crate;
		}

		@Override
		public void renderHUD(GuiGraphics gui, Minecraft mc) {
			int width = 52;
			int height = 52;
			int xc = mc.getWindow().getGuiScaledWidth() / 2 + 12;
			int yc = mc.getWindow().getGuiScaledHeight() / 2 - height / 2;

			RenderHelper.renderHUDBox(gui, xc - 4, yc - 4, xc + width + 4, yc + height + 4);

			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					int index = i * 3 + j;
					int xp = xc + j * 18;
					int yp = yc + i * 18;

					boolean enabled = true;
					if (crate.getPattern() != CraftyCratePattern.NONE) {
						enabled = crate.getPattern().openSlots.get(index);
					}

					gui.fill(xp, yp, xp + 16, yp + 16, enabled ? 0x22FFFFFF : 0x22FF0000);

					ItemStack item = crate.getItemHandler().getItem(index);
					gui.renderItem(item, xp, yp);
				}
			}
		}
	}
}
