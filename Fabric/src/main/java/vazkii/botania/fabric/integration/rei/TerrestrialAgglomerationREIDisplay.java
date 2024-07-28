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

import vazkii.botania.api.recipe.TerrestrialAgglomerationRecipe;

public class TerrestrialAgglomerationREIDisplay extends BotaniaRecipeDisplay<TerrestrialAgglomerationRecipe> {
	public TerrestrialAgglomerationREIDisplay(RecipeHolder<? extends TerrestrialAgglomerationRecipe> recipe) {
		super(recipe);
	}

	@Override
	public int getManaCost() {
		return recipe.value().getMana();
	}

	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return BotaniaREICategoryIdentifiers.TERRA_PLATE;
	}
}
