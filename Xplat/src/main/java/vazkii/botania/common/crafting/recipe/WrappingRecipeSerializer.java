/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 */

package vazkii.botania.common.crafting.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public interface WrappingRecipeSerializer<T extends Recipe<?>> extends RecipeSerializer<T> {
	T wrap(Recipe<?> recipe);
}
