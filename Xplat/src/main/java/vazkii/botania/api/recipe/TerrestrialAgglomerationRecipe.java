/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

import org.jetbrains.annotations.NotNull;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public interface TerrestrialAgglomerationRecipe extends Recipe<RecipeInput> {
	ResourceLocation TERRA_PLATE_ID = botaniaRL("terra_plate");
	ResourceLocation TYPE_ID = TERRA_PLATE_ID;

	int getMana();

	@Override
	default RecipeType<?> getType() {
		return BuiltInRegistries.RECIPE_TYPE.get(TYPE_ID);
	}

	@Override
	default boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	@NotNull
	@Override
	default ItemStack getToastSymbol() {
		return BuiltInRegistries.ITEM.getOptional(TERRA_PLATE_ID).map(ItemStack::new).orElse(ItemStack.EMPTY);
	}

	@Override
	default boolean isSpecial() {
		return true;
	}
}
