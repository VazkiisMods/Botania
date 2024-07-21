/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.ResoluteIvyItem;

public class ResoluteIvyRecipe extends CustomRecipe {
	public static final RecipeSerializer<ResoluteIvyRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(ResoluteIvyRecipe::new);

	public ResoluteIvyRecipe(CraftingBookCategory craftingBookCategory) {
		super(craftingBookCategory);
	}

	@Override
	public boolean matches(@NotNull CraftingContainer inv, @NotNull Level world) {
		boolean foundIvy = false;
		boolean foundItem = false;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.is(BotaniaItems.keepIvy) && !foundIvy) {
					foundIvy = true;
				} else if (!foundItem
						&& !(stack.hasTag() && ItemNBTHelper.getBoolean(stack, ResoluteIvyItem.TAG_KEEP, false))
						&& !stack.getItem().hasCraftingRemainingItem()) {
					foundItem = true;
				} else {
					return false;
				}
			}
		}

		return foundIvy && foundItem;
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess registries) {
		ItemStack item = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty() && !stack.is(BotaniaItems.keepIvy)) {
				item = stack;
			}
		}

		ItemStack copy = item.copyWithCount(1);
		ItemNBTHelper.setBoolean(copy, ResoluteIvyItem.TAG_KEEP, true);
		return copy;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
