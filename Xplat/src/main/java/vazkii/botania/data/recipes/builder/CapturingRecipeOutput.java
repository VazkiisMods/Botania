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

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CapturingRecipeOutput {
	private final MutableObject<ResourceLocation> partialRecipeId = new MutableObject<>();
	private final MutableObject<Recipe<?>> partialRecipe = new MutableObject<>();
	private final MutableObject<AdvancementHolder> partialAdvancementHolder = new MutableObject<>();
	private final RecipeOutput partialOutput;

	public CapturingRecipeOutput(RecipeOutput recipeOutput) {
		partialOutput = new RecipeOutput() {
			@Override
			public void accept(ResourceLocation recipeId, Recipe<?> recipe, @Nullable AdvancementHolder advancementHolder) {
				partialRecipeId.setValue(recipeId);
				partialRecipe.setValue(recipe);
				partialAdvancementHolder.setValue(advancementHolder);
			}

			@Override
			public Advancement.Builder advancement() {
				return recipeOutput.advancement();
			}
		};
	}

	public Triple<ResourceLocation, Recipe<?>, AdvancementHolder> captureSave(Consumer<RecipeOutput> recipeOutputConsumer) {
		recipeOutputConsumer.accept(partialOutput);

		ResourceLocation recipeId = partialRecipeId.getValue();
		Recipe<?> recipe = partialRecipe.getValue();
		AdvancementHolder advancementHolder = partialAdvancementHolder.getValue();

		partialRecipeId.setValue(null);
		partialRecipe.setValue(null);
		partialAdvancementHolder.setValue(null);

		return Triple.of(recipeId, recipe, advancementHolder);
	}
}
