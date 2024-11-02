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

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.ManaBlasterItem;

public class ManaBlasterRemoveLensRecipe extends CustomRecipe {
	public static final RecipeSerializer<ManaBlasterRemoveLensRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(ManaBlasterRemoveLensRecipe::new);

	public ManaBlasterRemoveLensRecipe(CraftingBookCategory craftingBookCategory) {
		super(craftingBookCategory);
	}

	@Override
	public boolean matches(@NotNull CraftingContainer inv, @NotNull Level world) {
		boolean foundGun = false;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof ManaBlasterItem
						&& !ManaBlasterItem.getLens(stack).isEmpty() && !foundGun) {
					foundGun = true;
				} else {
					return false; // Found an invalid item, breaking the recipe
				}
			}
		}

		return foundGun;
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess registries) {
		ItemStack gun = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof ManaBlasterItem) {
					gun = stack;
				}
			}
		}

		ItemStack gunCopy = gun.copyWithCount(1);
		ManaBlasterItem.setLens(gunCopy, ItemStack.EMPTY);

		return gunCopy;
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
			if (s.is(BotaniaItems.manaGun)) {
				ItemStack stack = ManaBlasterItem.getLens(s);
				stack.setCount(1);
				return stack;
			}
			return null;
		});
	}
}
