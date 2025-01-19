/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.patchouli.component;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import vazkii.botania.common.crafting.BotaniaRecipeTypes;
import vazkii.patchouli.api.IVariable;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Patchouli custom component that draws a rotating circle of items from the defined recipe.
 */
public class RotatingRecipeComponent extends RotatingItemListComponentBase {
	@SerializedName("recipe_name")
	public String recipeName;

	@SerializedName("recipe_type")
	public String recipeType;

	@Override
	protected List<Ingredient> makeIngredients() {
		Level world = Minecraft.getInstance().level;
		RecipeType<?> type;
		if ("runic_altar".equals(recipeType)) {
			type = BotaniaRecipeTypes.RUNE_TYPE;
		} else if ("petal_apothecary".equals(recipeType)) {
			type = BotaniaRecipeTypes.PETAL_TYPE;
		} else {
			throw new IllegalArgumentException("Type must be 'runic_altar' or 'petal_apothecary'!");
		}
		Optional<? extends RecipeHolder<?>> recipe = BotaniaRecipeTypes.getRecipe(world, ResourceLocation.parse(recipeName), type);
		if (recipe.isEmpty()) {
			return ImmutableList.of();
		}
		return recipe.get().value().getIngredients();
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
		recipeName = lookup.apply(IVariable.wrap(recipeName)).asString();
		recipeType = lookup.apply(IVariable.wrap(recipeType)).asString();
	}
}
