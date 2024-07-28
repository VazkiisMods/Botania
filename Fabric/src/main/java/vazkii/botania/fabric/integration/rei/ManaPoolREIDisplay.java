/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.integration.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;

import net.minecraft.world.item.crafting.RecipeHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.recipe.ManaInfusionRecipe;
import vazkii.botania.api.recipe.StateIngredient;

public class ManaPoolREIDisplay extends BotaniaRecipeDisplay<ManaInfusionRecipe> {
	@Nullable
	private final StateIngredient catalyst;

	public ManaPoolREIDisplay(RecipeHolder<? extends ManaInfusionRecipe> recipe) {
		super(recipe);
		this.catalyst = recipe.value().getRecipeCatalyst();
	}

	@Nullable
	public StateIngredient getCatalyst() {
		return this.catalyst;
	}

	@Override
	public int getManaCost() {
		return recipe.value().getManaToConsume();
	}

	@Override
	public @NotNull CategoryIdentifier<?> getCategoryIdentifier() {
		return BotaniaREICategoryIdentifiers.MANA_INFUSION;
	}
}
