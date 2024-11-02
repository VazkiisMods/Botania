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

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

import org.openzen.zencode.java.ZenCodeType;

import vazkii.botania.api.recipe.ITerraPlateRecipe;
import vazkii.botania.common.crafting.ModRecipeTypes;
import vazkii.botania.common.crafting.RecipeTerraPlate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * @docParam this <recipetype:botania:terra_plate>
 */
@Document("mods/Botania/recipe/manager/TerraPlateRecipeManager")
@ZenRegister
@IRecipeHandler.For(ITerraPlateRecipe.class)
@ZenCodeType.Name("mods.botania.recipe.manager.TerraPlateRecipeManager")
public class TerraPlateRecipeManager implements IRecipeManager<ITerraPlateRecipe>, IRecipeHandler<ITerraPlateRecipe> {

	/**
	 * Adds a terra plate recipe
	 *
	 * @param name   Name of the recipe to add
	 * @param output Output item
	 * @param mana   Recipe mana cost
	 * @param inputs Input items
	 * @docParam name "terra_plate_test"
	 * @docParam output <item:minecraft:diamond>
	 * @docParam mana 20000
	 * @docParam inputs <item:minecraft:dirt>, <item:minecraft:cobblestone>, <item:minecraft:gravel>
	 */
	@ZenCodeType.Method
	public void addRecipe(String name, IItemStack output, int mana, IIngredient... inputs) {
		name = fixRecipeName(name);
		ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath("crafttweaker", name);
		NonNullList<Ingredient> inputList =
				NonNullList.of(Ingredient.EMPTY, Arrays.stream(inputs).map(IIngredient::asVanillaIngredient).toArray(
						Ingredient[]::new));
		CraftTweakerAPI.apply(new ActionAddRecipe<>(this,
				new RecipeTerraPlate(resourceLocation,
						mana,
						inputList,
						output.getInternal())));
	}

	@Override
	public RecipeType<ITerraPlateRecipe> getRecipeType() {
		return ModRecipeTypes.TERRA_PLATE_TYPE;
	}

	@Override
	public String dumpToCommandString(@SuppressWarnings("rawtypes") IRecipeManager manager, ITerraPlateRecipe recipe) {
		StringJoiner s = new StringJoiner(", ", manager.getCommandString() + ".addRecipe(", ");");

		s.add(StringUtil.quoteAndEscape(recipe.getId()));
		s.add(Services.PLATFORM.createMCItemStack(recipe.getResultItem()).getCommandString());
		s.add(String.valueOf(recipe.getMana()));
		recipe.getIngredients().stream()
				.map(IIngredient::fromIngredient)
				.map(IIngredient::getCommandString)
				.forEach(s::add);
		return s.toString();
	}

	@Override
	public Optional<Function<ResourceLocation, ITerraPlateRecipe>> replaceIngredients(@SuppressWarnings("rawtypes") IRecipeManager manager, ITerraPlateRecipe recipe, List<IReplacementRule> rules) {
		return ReplacementHandlerHelper.replaceNonNullIngredientList(recipe.getIngredients(),
				Ingredient.class, recipe, rules,
				ingr -> id -> new RecipeTerraPlate(id, recipe.getMana(), ingr, recipe.getResultItem()));
	}
}
