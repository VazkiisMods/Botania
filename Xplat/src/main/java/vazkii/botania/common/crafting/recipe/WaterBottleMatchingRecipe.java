/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting.recipe;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.mixin.ShapedRecipeAccessor;

import java.util.function.Function;

public class WaterBottleMatchingRecipe extends ShapedRecipe {
	public static final WrappingRecipeSerializer<WaterBottleMatchingRecipe> SERIALIZER = new Serializer();

	private static ShapedRecipePattern transformPattern(ShapedRecipePattern pattern) {
		final var testPotion = new ItemStack(Items.POTION);
		final NonNullList<Ingredient> ingredients = NonNullList.of(Ingredient.EMPTY,
				pattern.ingredients().stream().map(i -> i.test(testPotion) ? Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION),
						Potions.WATER)) : i).toArray(Ingredient[]::new));
		return new ShapedRecipePattern(pattern.width(), pattern.height(), ingredients, pattern.data());
	}

	public WaterBottleMatchingRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result) {
		super(group, category, transformPattern(pattern), result);
	}

	private WaterBottleMatchingRecipe(ShapedRecipe recipe) {
		super(recipe.getGroup(), recipe.category(), transformPattern(((ShapedRecipeAccessor) recipe).botania_getPattern()),
				((ShapedRecipeAccessor) recipe).botania_getResult(), recipe.showNotification());
	}

	@Override
	public boolean matches(@NotNull CraftingInput craftingContainer, @NotNull Level level) {
		if (!super.matches(craftingContainer, level)) {
			return false;
		}
		for (int i = 0; i < craftingContainer.size(); i++) {
			var item = craftingContainer.getItem(i);
			if (item.is(Items.POTION) && !(PotionUtils.getPotion(item) == Potions.WATER)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	private static class Serializer implements WrappingRecipeSerializer<WaterBottleMatchingRecipe> {
		public static final MapCodec<WaterBottleMatchingRecipe> CODEC = SHAPED_RECIPE.codec()
				.xmap(WaterBottleMatchingRecipe::new, Function.identity());
		public static final StreamCodec<RegistryFriendlyByteBuf, WaterBottleMatchingRecipe> STREAM_CODEC = SHAPED_RECIPE.streamCodec()
				.map(WaterBottleMatchingRecipe::new, Function.identity());


		@Override
		public WaterBottleMatchingRecipe wrap(Recipe<?> recipe) {
			if (!(recipe instanceof ShapedRecipe shapedRecipe)) {
				throw new IllegalArgumentException("Unsupported recipe type to wrap: " + recipe.getType());
			}
			return new WaterBottleMatchingRecipe(shapedRecipe);
		}

		@Override
		public MapCodec<WaterBottleMatchingRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, WaterBottleMatchingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
