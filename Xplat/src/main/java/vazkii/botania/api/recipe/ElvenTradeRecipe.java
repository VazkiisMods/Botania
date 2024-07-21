/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.BotaniaAPI;

import java.util.List;
import java.util.Optional;

public interface ElvenTradeRecipe extends Recipe<Container> {
	ResourceLocation TYPE_ID = new ResourceLocation(BotaniaAPI.MODID, "elven_trade");
	ResourceLocation TYPE_ID_LEXICON = new ResourceLocation(BotaniaAPI.MODID, "elven_trade_lexicon");

	/**
	 * Attempts to match the recipe
	 *
	 * @param stacks Entire contents of the portal's buffer
	 * @return {@link Optional#empty()} if recipe doesn't match, Optional with a set of items used by recipe
	 *         otherwise
	 */
	Optional<List<ItemStack>> match(List<ItemStack> stacks);

	/**
	 * If the recipe does not contain the item, it will be destroyed upon entering the portal.
	 */
	boolean containsItem(ItemStack stack);

	/**
	 * @return Preview of the inputs
	 */
	@NotNull
	@Override
	NonNullList<Ingredient> getIngredients();

	/**
	 * @return Preview of the outputs
	 */
	List<ItemStack> getOutputs();

	/**
	 * Actually evaluate the recipe
	 */
	List<ItemStack> getOutputs(List<ItemStack> inputs);

	@NotNull
	@Override
	default RecipeType<?> getType() {
		return BuiltInRegistries.RECIPE_TYPE.get(TYPE_ID);
	}

	// Ignored IRecipe boilerplate

	@Override
	default boolean matches(@NotNull Container inv, @NotNull Level world) {
		return false;
	}

	@NotNull
	@Override
	default ItemStack assemble(@NotNull Container inv, @NotNull RegistryAccess registries) {
		return ItemStack.EMPTY;
	}

	@Override
	default boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	@Override
	default ItemStack getResultItem(@NotNull RegistryAccess registries) {
		return ItemStack.EMPTY;
	}

	@Override
	default boolean isSpecial() {
		return true;
	}

	/**
	 * Checks if this recipe is a "return" recipe, meaning that it returns the item that was thrown into it.
	 *
	 * @return {@code true} if recipe is a return recipe, {@code false} otherwise.
	 */
	default boolean isReturnRecipe() {
		return this.getOutputs().size() == 1
				&& this.getIngredients().size() == 1
				&& this.containsItem(this.getOutputs().get(0));
	}
}
