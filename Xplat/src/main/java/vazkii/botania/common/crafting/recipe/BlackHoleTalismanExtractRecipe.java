/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.BlackHoleTalismanItem;
import vazkii.botania.common.item.BotaniaItems;

public class BlackHoleTalismanExtractRecipe extends CustomRecipe {
	public static final RecipeSerializer<BlackHoleTalismanExtractRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(BlackHoleTalismanExtractRecipe::new);

	public BlackHoleTalismanExtractRecipe(CraftingBookCategory craftingBookCategory) {
		super(craftingBookCategory);
	}

	@Override
	public boolean matches(@NotNull CraftingContainer inv, @NotNull Level world) {
		boolean foundTalisman = false;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.is(BotaniaItems.blackHoleTalisman) && !foundTalisman) {

					// Avoid returning true for empty talismans
					int count = BlackHoleTalismanItem.getBlockCount(stack);
					if (count <= 0) {
						return false;
					}

					foundTalisman = true;
				} else {
					return false;
				}
			}
		}

		return foundTalisman;
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess registries) {
		ItemStack talisman = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				talisman = stack;
			}
		}

		int count = BlackHoleTalismanItem.getBlockCount(talisman);
		if (count > 0) {
			Block block = BlackHoleTalismanItem.getBlock(talisman);
			if (block != null) {
				return new ItemStack(block, Math.min(64, count));
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height > 0;
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@NotNull
	@Override
	public NonNullList<ItemStack> getRemainingItems(@NotNull CraftingContainer inv) {
		return RecipeUtils.getRemainingItemsSub(inv, s -> {
			if (s.is(BotaniaItems.blackHoleTalisman)) {
				int count = BlackHoleTalismanItem.getBlockCount(s);
				if (count == 0) {
					return ItemStack.EMPTY;
				}

				int extract = Math.min(64, count);
				ItemStack copy = s.copyWithCount(1);
				BlackHoleTalismanItem.remove(copy, extract);
				ItemNBTHelper.setBoolean(copy, BlackHoleTalismanItem.TAG_ACTIVE, false);

				return copy;
			}
			return null;
		});
	}
}
