/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.item.CosmeticAttachable;
import vazkii.botania.api.item.CosmeticBauble;
import vazkii.botania.common.item.equipment.bauble.BaubleItem;

public class CosmeticRemoveRecipe extends CustomRecipe {
	public static final RecipeSerializer<CosmeticRemoveRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(CosmeticRemoveRecipe::new);

	public CosmeticRemoveRecipe(CraftingBookCategory craftingBookCategory) {
		super(craftingBookCategory);
	}

	@Override
	public boolean matches(@NotNull CraftingInput inv, @NotNull Level world) {
		boolean foundAttachable = false;

		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof CosmeticAttachable attachable && !(stack.getItem() instanceof CosmeticBauble) && !attachable.getCosmeticItem(stack).isEmpty()) {
					foundAttachable = true;
				} else {
					return false;
				}
			}
		}

		return foundAttachable;
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingInput inv, @NotNull HolderLookup.Provider registries) {
		ItemStack attachableItem = ItemStack.EMPTY;

		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				attachableItem = stack;
			}
		}

		CosmeticAttachable attachable = (CosmeticAttachable) attachableItem.getItem();
		if (attachable.getCosmeticItem(attachableItem).isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack copy = attachableItem.copyWithCount(1);
		attachable.setCosmeticItem(copy, ItemStack.EMPTY);
		return copy;
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
	public NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput inv) {
		return RecipeUtils.getRemainingItemsSub(inv, s -> {
			if (s.getItem() instanceof BaubleItem bauble) {
				ItemStack stack = bauble.getCosmeticItem(s);
				stack.setCount(1);
				return stack;
			}
			return null;
		});
	}
}
