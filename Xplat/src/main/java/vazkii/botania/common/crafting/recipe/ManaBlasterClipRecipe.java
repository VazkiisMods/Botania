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

import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.ManaBlasterItem;

public class ManaBlasterClipRecipe extends CustomRecipe {
	public static final RecipeSerializer<ManaBlasterClipRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(ManaBlasterClipRecipe::new);

	public ManaBlasterClipRecipe(CraftingBookCategory craftingBookCategory) {
		super(craftingBookCategory);
	}

	@Override
	public boolean matches(@NotNull CraftingContainer inv, @NotNull Level world) {
		boolean foundGun = false;
		boolean foundClip = false;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof ManaBlasterItem
						&& !ManaBlasterItem.hasClip(stack) && !foundGun) {
					foundGun = true;
				} else if (stack.is(BotaniaItems.clip) && !foundClip) {
					foundClip = true;
				} else {
					return false; // Found an invalid item, breaking the recipe
				}
			}
		}

		return foundGun && foundClip;
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess registries) {
		ItemStack gun = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty() && stack.getItem() instanceof ManaBlasterItem) {
				gun = stack;
			}
		}

		if (gun.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack lens = ManaBlasterItem.getLens(gun);
		ItemStack gunCopy = gun.copy();
		ManaBlasterItem.setLens(gunCopy, ItemStack.EMPTY);
		ManaBlasterItem.setClip(gunCopy, true);
		ManaBlasterItem.setLensAtPos(gunCopy, lens, 0);
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
