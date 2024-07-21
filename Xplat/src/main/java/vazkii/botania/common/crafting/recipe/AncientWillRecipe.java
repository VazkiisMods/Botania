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

import vazkii.botania.api.item.AncientWillContainer;
import vazkii.botania.common.item.AncientWillItem;

public class AncientWillRecipe extends CustomRecipe {
	public static final RecipeSerializer<AncientWillRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(AncientWillRecipe::new);

	public AncientWillRecipe(CraftingBookCategory craftingBookCategory) {
		super(craftingBookCategory);
	}

	@Override
	public boolean matches(@NotNull CraftingContainer inv, @NotNull Level world) {
		boolean foundWill = false;
		boolean foundItem = false;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof AncientWillItem && !foundWill) {
					foundWill = true;
				} else if (!foundItem) {
					if (stack.getItem() instanceof AncientWillContainer) {
						foundItem = true;
					} else {
						return false;
					}
				}
			}
		}

		return foundWill && foundItem;
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess registries) {
		ItemStack item = ItemStack.EMPTY;
		AncientWillContainer.AncientWillType will = null;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof AncientWillContainer && item.isEmpty()) {
					item = stack;
				} else {
					will = ((AncientWillItem) stack.getItem()).type; // we already verified this is a will in matches()
				}
			}
		}

		AncientWillContainer container = (AncientWillContainer) item.getItem();
		if (container.hasAncientWill(item, will)) {
			return ItemStack.EMPTY;
		}

		ItemStack copy = item.copy();
		container.addAncientWill(copy, will);
		return copy;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width > 1 || height > 1;
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
