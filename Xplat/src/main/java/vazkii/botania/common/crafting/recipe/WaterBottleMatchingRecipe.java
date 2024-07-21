/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting.recipe;

import com.mojang.serialization.Codec;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.*;
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
	public boolean matches(@NotNull CraftingContainer craftingContainer, @NotNull Level level) {
		if (!super.matches(craftingContainer, level)) {
			return false;
		}
		for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
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
		public static final Codec<WaterBottleMatchingRecipe> CODEC = SHAPED_RECIPE.codec()
				.xmap(WaterBottleMatchingRecipe::new, Function.identity());

		@Override
		public WaterBottleMatchingRecipe wrap(Recipe<?> recipe) {
			if (!(recipe instanceof ShapedRecipe shapedRecipe)) {
				throw new IllegalArgumentException("Unsupported recipe type to wrap: " + recipe.getType());
			}
			return new WaterBottleMatchingRecipe(shapedRecipe);
		}

		@Override
		public Codec<WaterBottleMatchingRecipe> codec() {
			return CODEC;
		}

		@Override
		public WaterBottleMatchingRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
			return new WaterBottleMatchingRecipe(SHAPED_RECIPE.fromNetwork(buffer));
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull WaterBottleMatchingRecipe recipe) {
			SHAPED_RECIPE.toNetwork(buffer, recipe);
		}
	}
}
