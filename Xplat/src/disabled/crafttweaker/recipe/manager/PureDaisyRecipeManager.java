/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.integration.crafttweaker.recipe.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.action.recipe.ActionRemoveRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.block.CTBlockIngredient;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.handler.IReplacementRule;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker.api.util.StringUtil;
import com.blamejared.crafttweaker.natives.block.ExpandBlockState;
import com.blamejared.crafttweaker_annotations.annotations.Document;

import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

import org.openzen.zencode.java.ZenCodeType;

import vazkii.botania.api.recipe.IPureDaisyRecipe;
import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.common.crafting.ModRecipeTypes;
import vazkii.botania.common.crafting.RecipePureDaisy;
import vazkii.botania.common.integration.crafttweaker.CTPlugin;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * @docParam this <recipetype:botania:pure_daisy>
 */
@Document("mods/Botania/recipe/manager/PureDaisyRecipeManager")
@ZenRegister
@IRecipeHandler.For(IPureDaisyRecipe.class)
@ZenCodeType.Name("mods.botania.recipe.manager.PureDaisyRecipeManager")
public class PureDaisyRecipeManager implements IRecipeManager<IPureDaisyRecipe>, IRecipeHandler<IPureDaisyRecipe> {

	/**
	 * Adds a Pure Daisy conversion recipe.
	 *
	 * @param name   Name of the recipe to add
	 * @param output Output block state
	 * @param input  Input ingredient
	 * @param time   Optional conversion time (note that the real time is multiplied by 8)
	 * @docParam name "pure_daisy_test"
	 * @docParam output <blockstate:minecraft:diamond_block>
	 * @docParam input <blockstate:minecraft:gold_block>
	 * @docParam time 50
	 */
	@ZenCodeType.Method
	public void addRecipe(String name, BlockState output, CTBlockIngredient input, @ZenCodeType.OptionalInt(RecipePureDaisy.DEFAULT_TIME) int time) {
		name = fixRecipeName(name);
		ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath("crafttweaker", name);
		CraftTweakerAPI.apply(new ActionAddRecipe<>(this,
				new RecipePureDaisy(resourceLocation, CTPlugin.blockIngredientToStateIngredient(input), output, time, CommandFunction.CacheableFunction.NONE)));
	}

	/**
	 * Removes recipes who's output state matches the given ingredient
	 *
	 * @param ingredient ingredient that should be matched against
	 * @docParam ingredient <blockstate:botania:livingrock>
	 */
	@ZenCodeType.Method
	public void remove(CTBlockIngredient ingredient) {
		StateIngredient stateIngredient = CTPlugin.blockIngredientToStateIngredient(ingredient);
		CraftTweakerAPI.apply(new ActionRemoveRecipe<>(this, recipe -> stateIngredient.test(recipe.getOutputState())));
	}

	@Override
	public void remove(IIngredient output) {
		throw new UnsupportedOperationException(
				"The Pure Daisy does not output IItemStacks, use remove(BlockState)!");
	}

	@Override
	public RecipeType<IPureDaisyRecipe> getRecipeType() {
		return ModRecipeTypes.PURE_DAISY_TYPE;
	}

	@Override
	public String dumpToCommandString(@SuppressWarnings("rawtypes") IRecipeManager manager, IPureDaisyRecipe recipe) {
		StringJoiner s = new StringJoiner(", ", manager.getCommandString() + ".addRecipe(", ");");

		s.add(StringUtil.quoteAndEscape(recipe.getId()));
		s.add(ExpandBlockState.getCommandString(recipe.getOutputState()));
		s.add(CTPlugin.ingredientToCommandString(recipe.getInput()));
		if (recipe.getTime() != RecipePureDaisy.DEFAULT_TIME) {
			s.add(String.valueOf(recipe.getTime()));
		}
		return s.toString();
	}

	@Override
	public Optional<Function<ResourceLocation, IPureDaisyRecipe>> replaceIngredients(@SuppressWarnings("rawtypes") IRecipeManager manager, IPureDaisyRecipe recipe, List<IReplacementRule> rules) {
		return Optional.empty(); // Not supported by CT at the moment
	}
}
