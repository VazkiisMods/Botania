/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting.recipe;

import com.google.common.base.Suppliers;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;

import vazkii.botania.api.mana.BasicLensItem;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.lens.LensItem;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class LensDyeingRecipe extends CustomRecipe {
	public static final RecipeSerializer<LensDyeingRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(LensDyeingRecipe::new);

	private final Supplier<List<Ingredient>> dyes = Suppliers.memoize(() -> Arrays.asList(
			Ingredient.of(Items.WHITE_DYE), Ingredient.of(Items.ORANGE_DYE),
			Ingredient.of(Items.MAGENTA_DYE), Ingredient.of(Items.LIGHT_BLUE_DYE),
			Ingredient.of(Items.YELLOW_DYE), Ingredient.of(Items.LIME_DYE),
			Ingredient.of(Items.PINK_DYE), Ingredient.of(Items.GRAY_DYE),
			Ingredient.of(Items.LIGHT_GRAY_DYE), Ingredient.of(Items.CYAN_DYE),
			Ingredient.of(Items.PURPLE_DYE), Ingredient.of(Items.BLUE_DYE),
			Ingredient.of(Items.BROWN_DYE), Ingredient.of(Items.GREEN_DYE),
			Ingredient.of(Items.RED_DYE), Ingredient.of(Items.BLACK_DYE),
			Ingredient.of(BotaniaItems.manaPearl)
	));

	public LensDyeingRecipe(CraftingBookCategory craftingBookCategory) {
		super(craftingBookCategory);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public boolean matches(CraftingInput inv, Level world) {
		boolean foundLens = false;
		boolean foundDye = false;

		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof BasicLensItem && !foundLens) {
					foundLens = true;
				} else if (!foundDye) {
					// TODO: rainbow tinting items should be a tag
					if (stack.getItem() instanceof DyeItem || stack.is(BotaniaItems.manaPearl)) {
						foundDye = true;
					} else {
						return false;
					}
				} else {
					return false;//This means we have an additional item in the recipe after the lens and dye
				}
			}
		}

		return foundLens && foundDye;
	}

	@Override
	public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registries) {
		ItemStack lens = ItemStack.EMPTY;
		DyeColor color = null;

		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof BasicLensItem && lens.isEmpty()) {
					lens = stack;
				} else if (stack.getItem() instanceof DyeItem dyeItem) {
					// we can assume that otherwise it's rainbow color, as we matched the ingredients already
					color = dyeItem.getDyeColor();
				}
			}
		}

		if (lens.getItem() instanceof BasicLensItem) {
			ItemStack lensCopy = lens.copyWithCount(1);
			if (color != null) {
				LensItem.setLensColor(lensCopy, color);
			} else {
				LensItem.setLensRainbow(lensCopy);
			}

			return lensCopy;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}
}
