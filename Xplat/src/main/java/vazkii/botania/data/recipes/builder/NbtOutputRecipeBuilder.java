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
import net.minecraft.nbt.CompoundTag;

import vazkii.botania.common.crafting.recipe.NbtOutputRecipe;

import java.util.function.Consumer;

public class NbtOutputRecipeBuilder {
	private final RecipeBuilder recipeBuilder;
	private final Consumer<CompoundTag> nbtDefinition;

	public NbtOutputRecipeBuilder(RecipeBuilder recipeBuilder, Consumer<CompoundTag> nbtDefinition) {
		this.recipeBuilder = recipeBuilder;
		this.nbtDefinition = nbtDefinition;
	}

	public static NbtOutputRecipeBuilder setNbt(RecipeBuilder recipeBuilder, Consumer<CompoundTag> nbtDefinition) {
		return new NbtOutputRecipeBuilder(recipeBuilder, nbtDefinition);
	}

	public void save(RecipeOutput recipeOutput, String recipeId) {
		CapturingRecipeOutput capturingRecipeOutput = new CapturingRecipeOutput(recipeOutput);
		var output = capturingRecipeOutput.captureSave(ro -> recipeBuilder.save(ro, recipeId));

		CompoundTag tag = new CompoundTag();
		nbtDefinition.accept(tag);
		NbtOutputRecipe<?> wrappedRecipe = new NbtOutputRecipe<>(output.getMiddle(), tag);
		recipeOutput.accept(output.getLeft(), wrappedRecipe, output.getRight());
	}
}
