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

import vazkii.botania.api.recipe.OrechidRecipe;

public class MarimorphosisREIDisplay extends OrechidBaseREIDisplay {
	public MarimorphosisREIDisplay(RecipeHolder<? extends OrechidRecipe> recipe) {
		super(recipe);
	}

	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return BotaniaREICategoryIdentifiers.MARIMORPHOSIS;
	}
}
