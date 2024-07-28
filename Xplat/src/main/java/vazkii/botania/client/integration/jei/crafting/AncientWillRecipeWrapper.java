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

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.item.AncientWillContainer;
import vazkii.botania.common.crafting.recipe.AncientWillRecipe;
import vazkii.botania.common.item.AncientWillItem;
import vazkii.botania.common.item.BotaniaItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AncientWillRecipeWrapper implements ICraftingCategoryExtension<AncientWillRecipe> {
	@Override
	public void setRecipe(RecipeHolder<AncientWillRecipe> recipe, @NotNull IRecipeLayoutBuilder builder, @NotNull ICraftingGridHelper helper, @NotNull IFocusGroup focusGroup) {
		var foci = focusGroup.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.INPUT)
				.filter(f -> f.getTypedValue().getIngredient().getItem() instanceof AncientWillItem)
				.map(f -> f.getTypedValue().getIngredient())
				.toList();

		var willStacks = !foci.isEmpty() ? foci : List.of(
				new ItemStack(BotaniaItems.ancientWillAhrim),
				new ItemStack(BotaniaItems.ancientWillDharok),
				new ItemStack(BotaniaItems.ancientWillGuthan),
				new ItemStack(BotaniaItems.ancientWillTorag),
				new ItemStack(BotaniaItems.ancientWillVerac),
				new ItemStack(BotaniaItems.ancientWillKaril)
		);

		var outputStacks = new ArrayList<ItemStack>();
		for (var will : !foci.isEmpty() ? foci : willStacks) {
			var stack = new ItemStack(BotaniaItems.terrasteelHelm);
			((AncientWillContainer) stack.getItem()).addAncientWill(stack, ((AncientWillItem) will.getItem()).type);
			outputStacks.add(stack);
		}

		helper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK,
				List.of(Collections.singletonList(new ItemStack(BotaniaItems.terrasteelHelm)), willStacks), 0, 0);
		helper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, outputStacks);
	}
}
