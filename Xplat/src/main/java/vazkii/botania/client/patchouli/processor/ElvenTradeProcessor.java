/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.patchouli.processor;

import com.google.common.collect.ImmutableList;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import vazkii.botania.api.recipe.ElvenTradeRecipe;
import vazkii.botania.client.patchouli.PatchouliUtils;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.List;
import java.util.stream.Collectors;

public class ElvenTradeProcessor implements IComponentProcessor {
	private List<ElvenTradeRecipe> recipes;
	private int longestIngredientSize, mostInputs, mostOutputs;

	@Override
	public void setup(Level level, IVariableProvider variables) {
		ImmutableList.Builder<ElvenTradeRecipe> builder = ImmutableList.builder();
		for (IVariable s : variables.get("recipes", level.registryAccess()).asListOrSingleton(level.registryAccess())) {
			ElvenTradeRecipe recipe = PatchouliUtils.getRecipe(level, BotaniaRecipeTypes.ELVEN_TRADE_TYPE, ResourceLocation.parse(s.asString()));
			if (recipe != null) {
				builder.add(recipe);
			}
		}
		recipes = builder.build();
		for (ElvenTradeRecipe recipe : recipes) {
			List<Ingredient> inputs = recipe.getIngredients();
			for (Ingredient ingredient : inputs) {
				int length = ingredient.getItems().length;
				if (length > longestIngredientSize) {
					longestIngredientSize = length;
				}
			}
			if (inputs.size() > mostInputs) {
				mostInputs = inputs.size();
			}
			if (recipe.getOutputs().size() > mostOutputs) {
				mostOutputs = recipe.getOutputs().size();
			}
		}
	}

	@Override
	public IVariable process(Level level, String key) {
		if (recipes.isEmpty()) {
			return null;
		}
		if (key.equals("heading")) {
			return IVariable.from(recipes.get(0).getOutputs().get(0).getHoverName(), level.registryAccess());
		} else if (key.startsWith("input")) {
			int index = Integer.parseInt(key.substring(5)) - 1;
			if (index < mostInputs) {
				return interweaveIngredients(index, level);
			} else {
				return null;
			}
		}
		if (key.startsWith("output")) {
			int index = Integer.parseInt(key.substring(6)) - 1;
			if (index < mostOutputs) {
				return IVariable.wrapList(recipes.stream().map(ElvenTradeRecipe::getOutputs)
						.map(l -> index < l.size() ? l.get(index) : ItemStack.EMPTY)
						.map(o -> IVariable.from(o, level.registryAccess()))
						.collect(Collectors.toList()), level.registryAccess());
			}
		}
		return null;
	}

	private IVariable interweaveIngredients(int inputIndex, Level level) {
		List<Ingredient> recipes = this.recipes.stream().map(ElvenTradeRecipe::getIngredients).map(ingredients -> {
			if (inputIndex < ingredients.size()) {
				return ingredients.get(inputIndex);
			} else {
				return Ingredient.EMPTY;
			}
		}).collect(Collectors.toList());
		return PatchouliUtils.interweaveIngredients(recipes, longestIngredientSize, level);
	}

}
