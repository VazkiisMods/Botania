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

import vazkii.botania.api.mana.BasicLensItem;
import vazkii.botania.common.item.ManaBlasterItem;

public class ManaBlasterLensRecipe extends CustomRecipe {
	public static final RecipeSerializer<ManaBlasterLensRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(ManaBlasterLensRecipe::new);

	public ManaBlasterLensRecipe(CraftingBookCategory craftingBookCategory) {
		super(craftingBookCategory);
	}

	@Override
	public boolean matches(@NotNull CraftingContainer inv, @NotNull Level world) {
		int foundLens = 0;
		int foundGun = 0;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof ManaBlasterItem && ManaBlasterItem.getLens(stack).isEmpty()) {
					foundGun++;
				} else if (ManaBlasterItem.isValidLens(stack)) {
					foundLens++;
				} else {
					return false; // Found an invalid item, breaking the recipe
				}
			}
		}

		return foundLens == 1 && foundGun == 1;
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess registries) {
		ItemStack lens = ItemStack.EMPTY;
		ItemStack gun = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof ManaBlasterItem) {
					gun = stack;
				} else if (stack.getItem() instanceof BasicLensItem) {
					lens = stack.copyWithCount(1);
				}
			}
		}

		if (lens.isEmpty() || gun.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack gunCopy = gun.copy();
		ManaBlasterItem.setLens(gunCopy, lens);

		return gunCopy;
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
