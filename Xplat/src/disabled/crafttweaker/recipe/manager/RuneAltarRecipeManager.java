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
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.handler.IReplacementRule;
import com.blamejared.crafttweaker.api.recipe.handler.helper.ReplacementHandlerHelper;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker.api.util.StringUtil;
import com.blamejared.crafttweaker.platform.Services;
import com.blamejared.crafttweaker_annotations.annotations.Document;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

import org.openzen.zencode.java.ZenCodeType;

import vazkii.botania.api.recipe.IRuneAltarRecipe;
import vazkii.botania.common.crafting.ModRecipeTypes;
import vazkii.botania.common.crafting.RecipeRuneAltar;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * @docParam this <recipetype:botania:runic_altar>
 */
@Document("mods/Botania/recipe/manager/RuneAltarRecipeManager")
@ZenRegister
@IRecipeHandler.For(IRuneAltarRecipe.class)
@ZenCodeType.Name("mods.botania.recipe.manager.RuneAltarRecipeManager")
public class RuneAltarRecipeManager implements IRecipeManager<IRuneAltarRecipe>, IRecipeHandler<IRuneAltarRecipe> {

	/**
	 * Adds the specified runic altar recipe.
	 *
	 * @param name   Name of the recipe to add
	 * @param output Output item
	 * @param mana   Recipe mana cost
	 * @param inputs Input items
	 * @docParam name "rune_altar_test"
	 * @docParam output <item:minecraft:diamond>
	 * @docParam mana 5000
	 * @docParam inputs <item:botania:rune_air>, <item:botania:orange_petal>, <item:botania:red_petal>
	 */
	@ZenCodeType.Method
	public void addRecipe(String name, IItemStack output, int mana, IIngredient... inputs) {
		name = fixRecipeName(name);
		ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath("crafttweaker", name);
		CraftTweakerAPI.apply(new ActionAddRecipe<>(this,
				new RecipeRuneAltar(resourceLocation,
						output.getInternal(),
						mana,
						Arrays.stream(inputs).map(IIngredient::asVanillaIngredient).toArray(Ingredient[]::new))));
	}

	@Override
	public RecipeType<IRuneAltarRecipe> getRecipeType() {
		return ModRecipeTypes.RUNE_TYPE;
	}

	@Override
	public String dumpToCommandString(@SuppressWarnings("rawtypes") IRecipeManager manager, IRuneAltarRecipe recipe) {
		StringJoiner s = new StringJoiner(", ", manager.getCommandString() + ".addRecipe(", ");");

		s.add(StringUtil.quoteAndEscape(recipe.getId()));
		s.add(Services.PLATFORM.createMCItemStack(recipe.getResultItem()).getCommandString());
		s.add(String.valueOf(recipe.getManaUsage()));
		recipe.getIngredients().stream()
				.map(IIngredient::fromIngredient)
				.map(IIngredient::getCommandString)
				.forEach(s::add);
		return s.toString();
	}

	@Override
	public Optional<Function<ResourceLocation, IRuneAltarRecipe>> replaceIngredients(@SuppressWarnings("rawtypes") IRecipeManager manager, IRuneAltarRecipe recipe, List<IReplacementRule> rules) {
		return ReplacementHandlerHelper.replaceNonNullIngredientList(recipe.getIngredients(),
				Ingredient.class, recipe, rules,
				ingr -> id -> new RecipeRuneAltar(id, recipe.getResultItem(), recipe.getManaUsage(), ingr.toArray(new Ingredient[0])));
	}
}
