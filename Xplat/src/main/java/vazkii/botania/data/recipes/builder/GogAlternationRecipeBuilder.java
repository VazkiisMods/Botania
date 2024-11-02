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
import net.minecraft.resources.ResourceLocation;

import vazkii.botania.common.crafting.recipe.GogAlternationRecipe;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GogAlternationRecipeBuilder {
	private final RecipeBuilder baseRecipeBuilder;
	private final RecipeBuilder gogRecipeBuilder;

	public GogAlternationRecipeBuilder(RecipeBuilder baseRecipeBuilder, RecipeBuilder gogRecipeBuilder) {
		if (baseRecipeBuilder.getResult() != gogRecipeBuilder.getResult()) {
			throw new IllegalArgumentException("Both recipes must have the same result item, found: %s/%s"
					.formatted(baseRecipeBuilder.getResult(), gogRecipeBuilder.getResult()));
		}
		this.baseRecipeBuilder = baseRecipeBuilder;
		this.gogRecipeBuilder = gogRecipeBuilder;
	}

	public static GogAlternationRecipeBuilder alternatives(RecipeBuilder baseRecipeBuilder, RecipeBuilder gogRecipeBuilder) {
		return new GogAlternationRecipeBuilder(baseRecipeBuilder, gogRecipeBuilder);
	}

	public static void saveAlternatives(RecipeOutput recipeOutput, Consumer<RecipeOutput> baseRecipeOutputConsumer,
			Consumer<RecipeOutput> gogRecipeOutputConsumer) {
		CapturingRecipeOutput capturingRecipeOutput = new CapturingRecipeOutput(recipeOutput);
		var baseOutput = capturingRecipeOutput.captureSave(baseRecipeOutputConsumer);
		var gogOutput = capturingRecipeOutput.captureSave(gogRecipeOutputConsumer);

		if (!Objects.equals(baseOutput.getLeft(), gogOutput.getLeft())) {
			throw new IllegalArgumentException("Both recipes mut have matching IDs, got: %s/%s"
					.formatted(baseOutput.getLeft(), gogOutput.getLeft()));
		}

		if (!Objects.equals(baseOutput.getRight(), gogOutput.getRight())) {
			// TODO: How to handle differences in the unlock advancements?
		}

		GogAlternationRecipe<?> alternationRecipe = new GogAlternationRecipe<>(baseOutput.getMiddle(), gogOutput.getMiddle());
		recipeOutput.accept(baseOutput.getLeft(), alternationRecipe, baseOutput.getRight());
	}

	public void save(RecipeOutput recipeOutput, String recipeId) {
		save(recipeOutput, (builder, output) -> builder.save(output, recipeId));
	}

	public void save(RecipeOutput recipeOutput, ResourceLocation recipeId) {
		save(recipeOutput, (builder, output) -> builder.save(output, recipeId));
	}

	public void save(RecipeOutput recipeOutput) {
		save(recipeOutput, RecipeBuilder::save);
	}

	private void save(RecipeOutput recipeOutput, BiConsumer<RecipeBuilder, RecipeOutput> partialSaveAction) {
		saveAlternatives(recipeOutput, output -> partialSaveAction.accept(baseRecipeBuilder, output),
				output -> partialSaveAction.accept(gogRecipeBuilder, output));
	}
}
