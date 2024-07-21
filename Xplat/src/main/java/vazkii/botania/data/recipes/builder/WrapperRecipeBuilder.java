/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 */
package vazkii.botania.data.recipes.builder;

import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.crafting.Recipe;

import vazkii.botania.common.crafting.recipe.WrappingRecipeSerializer;

public class WrapperRecipeBuilder<T extends Recipe<?>> {
	private final WrappingRecipeSerializer<T> serializer;
	private final RecipeBuilder recipeBuilder;

	public WrapperRecipeBuilder(WrappingRecipeSerializer<T> serializer, RecipeBuilder recipeBuilder) {
		this.recipeBuilder = recipeBuilder;
		this.serializer = serializer;
	}

	public static <T extends Recipe<?>> WrapperRecipeBuilder<T> wrap(WrappingRecipeSerializer<T> serializer,
			RecipeBuilder recipeBuilder) {
		return new WrapperRecipeBuilder<>(serializer, recipeBuilder);
	}

	public void save(RecipeOutput recipeOutput) {
		CapturingRecipeOutput capturingRecipeOutput = new CapturingRecipeOutput(recipeOutput);
		var output = capturingRecipeOutput.captureSave(recipeBuilder::save);

		T wrappedRecipe = serializer.wrap(output.getMiddle());
		recipeOutput.accept(output.getLeft(), wrappedRecipe, output.getRight());
	}
}
