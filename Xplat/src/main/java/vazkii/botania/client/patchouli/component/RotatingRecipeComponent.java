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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import vazkii.botania.common.crafting.BotaniaRecipeTypes;
import vazkii.patchouli.api.IVariable;

import java.util.List;
import java.util.Map;
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
		Map<ResourceLocation, ? extends Recipe<?>> map;
		if ("runic_altar".equals(recipeType)) {
			map = BotaniaRecipeTypes.getRecipes(world, BotaniaRecipeTypes.RUNE_TYPE);
		} else if ("petal_apothecary".equals(recipeType)) {
			map = BotaniaRecipeTypes.getRecipes(world, BotaniaRecipeTypes.PETAL_TYPE);
		} else {
			throw new IllegalArgumentException("Type must be 'runic_altar' or 'petal_apothecary'!");
		}
		Recipe<?> recipe = map.get(ResourceLocation.parse(recipeName));
		if (recipe == null) {
			return ImmutableList.of();
		}
		return recipe.getIngredients();
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
		recipeName = lookup.apply(IVariable.wrap(recipeName)).asString();
		recipeType = lookup.apply(IVariable.wrap(recipeType)).asString();
	}
}
