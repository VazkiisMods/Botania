/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.BotaniaAPI;

public interface ManaInfusionRecipe extends Recipe<Container> {
	ResourceLocation TYPE_ID = new ResourceLocation(BotaniaAPI.MODID, "mana_infusion");

	/**
	 * Attempts to match the recipe.
	 *
	 * @param stack The whole stack that is in the Mana Pool (when actually crafting)
	 *              or in the player's hand (for the HUD).
	 * @return Whether this recipe matches the given stack.
	 */
	boolean matches(ItemStack stack);

	/**
	 * Get the recipe output, used for display (in JEI or the HUD).
	 * If {@link #getRecipeOutput} isn't overridden, this is also the actual result of the crafting recipe.
	 *
	 * @return The output stack of the recipe.
	 */
	@NotNull
	@Override
	ItemStack getResultItem(@NotNull RegistryAccess registries);

	/**
	 * Get the actual recipe output, not just for display. Defaults to a copy of {@link #getResultItem}.
	 *
	 * @param input The whole stack that is in the Mana Pool, not a copy.
	 * @return The output stack of the recipe for the specific input.
	 */
	@NotNull
	default ItemStack getRecipeOutput(@NotNull RegistryAccess registries, @NotNull ItemStack input) {
		return getResultItem(registries).copy();
	}

	/**
	 * Get the catalyst that must be under the Mana Pool for this recipe, or {@code null} if it can be anything.
	 *
	 * @return The catalyst ingredient.
	 */
	@NotNull
	StateIngredient getRecipeCatalyst();

	/**
	 * @return How much mana this recipe consumes from the pool.
	 */
	int getManaToConsume();

	@NotNull
	@Override
	default RecipeType<?> getType() {
		return BuiltInRegistries.RECIPE_TYPE.get(TYPE_ID);
	}

	// Ignored IRecipe stuff

	@NotNull
	@Override
	default ItemStack assemble(@NotNull Container inv, @NotNull RegistryAccess registries) {
		return ItemStack.EMPTY;
	}

	@Override
	default boolean matches(@NotNull Container inv, @NotNull Level world) {
		return false;
	}

	@Override
	default boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	@Override
	default boolean isSpecial() {
		return true;
	}
}
