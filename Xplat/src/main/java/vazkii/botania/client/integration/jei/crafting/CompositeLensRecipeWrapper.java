/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.integration.jei.crafting;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.crafting.recipe.CompositeLensRecipe;
import vazkii.botania.common.item.lens.LensItem;
import vazkii.botania.common.lib.BotaniaTags;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class CompositeLensRecipeWrapper implements ICraftingCategoryExtension<CompositeLensRecipe> {
	private final List<Item> allLenses;

	public CompositeLensRecipeWrapper() {
		allLenses = StreamSupport.stream(BuiltInRegistries.ITEM.getTagOrEmpty(BotaniaTags.Items.LENS).spliterator(), false)
				.map(ItemStack::new)
				.filter(s -> !((LensItem) s.getItem()).isControlLens(s))
				.filter(s -> ((LensItem) s.getItem()).isCombinable(s))
				.map(ItemStack::getItem)
				.toList();
	}

	@Override
	public void setRecipe(RecipeHolder<CompositeLensRecipe> recipe, @NotNull IRecipeLayoutBuilder builder, @NotNull ICraftingGridHelper helper, @NotNull IFocusGroup focusGroup) {
		var possibleFirstLenses = focusGroup.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.INPUT)
				.filter(f -> allLenses.contains(f.getTypedValue().getIngredient().getItem()))
				.map(f -> f.getTypedValue().getIngredient().getItem())
				.toList();
		if (possibleFirstLenses.isEmpty()) {
			possibleFirstLenses = allLenses;
		}

		List<ItemStack> firstInput = new ArrayList<>();
		List<ItemStack> secondInput = new ArrayList<>();
		List<ItemStack> outputs = new ArrayList<>();

		for (var firstLens : possibleFirstLenses) {
			var firstLensStack = new ItemStack(firstLens);
			for (var secondLens : allLenses) {
				if (secondLens == firstLens) {
					continue;
				}

				ItemStack secondLensStack = new ItemStack(secondLens);
				if (((LensItem) firstLens).canCombineLenses(firstLensStack, secondLensStack)) {
					firstInput.add(firstLensStack);
					secondInput.add(secondLensStack);
					outputs.add(((LensItem) firstLens).setCompositeLens(firstLensStack.copy(), secondLensStack));
				}
			}
		}

		helper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK,
				List.of(firstInput, List.of(new ItemStack(Items.SLIME_BALL)), secondInput), 0, 0);
		helper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, outputs);
	}
}
