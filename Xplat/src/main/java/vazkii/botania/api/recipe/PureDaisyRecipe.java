/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.recipe;

import net.minecraft.commands.CacheableFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.BotaniaAPI;

import java.util.Optional;

public interface PureDaisyRecipe extends Recipe<Container> {
	ResourceLocation TYPE_ID = new ResourceLocation(BotaniaAPI.MODID, "pure_daisy");

	/**
	 * This gets called every tick, please be careful with your checks.
	 */
	boolean matches(Level world, BlockPos pos, BlockState state);

	/**
	 * Returns the input block state definition.
	 *
	 */
	StateIngredient getInput();

	/**
	 * Returns the output block state definition. If it matches multiple block states,
	 * one of those it picked at random with equal weights when the conversion takes place.
	 */
	StateIngredient getOutput();

	/**
	 * Returns whether any relevant block state properties of the matched block will be copied over to the
	 * converted block as the conversion takes place. (Used to e.g. keep the rotation of converted logs.)
	 */
	boolean isCopyInputProperties();

	/**
	 * Returns the optional mcfunction to execute when the conversion takes place.
	 * (Might not be available on client-side mirrors of the recipe definition.)
	 */
	Optional<CacheableFunction> getSuccessFunction();

	/**
	 * Returns the number of times a source block must be ticked by the flower before it converts.
	 * Note that the Pure Daisy ticks its surrounding blocks in a round-robin way, one at a time.
	 */
	int getTime();

	@Override
	default RecipeType<?> getType() {
		return BuiltInRegistries.RECIPE_TYPE.get(TYPE_ID);
	}

	/**
	 * @deprecated Not meant to be used for item crafting in a container.
	 */
	@Override
	@Deprecated
	default boolean matches(Container container, Level level) {
		return false;
	}

	/**
	 * @deprecated Not meant to be used for item crafting in a container.
	 */
	@Override
	@Deprecated
	default ItemStack assemble(Container container, @NotNull RegistryAccess registryAccess) {
		return ItemStack.EMPTY;
	}

	/**
	 * @deprecated Not meant to be used for item crafting in a container.
	 */
	@Override
	@Deprecated
	default boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	/**
	 * @deprecated Not meant to be used for item crafting in a container.
	 */
	@Override
	@Deprecated
	default ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	default boolean isSpecial() {
		return true;
	}
}
