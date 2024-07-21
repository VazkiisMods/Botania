/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.data.recipes.builder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Function;

public class BotaniaSpecialRecipeBuilder {
	private final Function<CraftingBookCategory, Recipe<?>> factory;
	private final CraftingBookCategory category;

	public BotaniaSpecialRecipeBuilder(Function<CraftingBookCategory, Recipe<?>> factory, CraftingBookCategory category) {
		this.factory = factory;
		this.category = category;
	}

	public static BotaniaSpecialRecipeBuilder special(Function<CraftingBookCategory, Recipe<?>> factory, CraftingBookCategory category) {
		return new BotaniaSpecialRecipeBuilder(factory, category);
	}

	public void save(RecipeOutput recipeOutput) {
		Recipe<?> recipe = this.factory.apply(category);
		RecipeSerializer<?> serializer = recipe.getSerializer();
		ResourceLocation serializerKey = BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer);
		ResourceLocation recipeId = new ResourceLocation(serializerKey.getNamespace(), "dynamic/" + serializerKey.getPath());
		recipeOutput.accept(recipeId, recipe, null);
	}
}
