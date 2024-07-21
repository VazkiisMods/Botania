/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting.recipe;

import it.unimi.dsi.fastutil.ints.IntSet;

import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RecipeUtils {
	/**
	 * Check if every ingredient in {@code inputs} is satisfied by {@code inv}.
	 * Optionally, the slots from the inventory used to fulfill the inputs are placed into {@code usedSlots}.
	 */
	public static boolean matches(List<Ingredient> inputs, Container inv, @Nullable IntSet usedSlots) {
		List<Ingredient> ingredientsMissing = new ArrayList<>(inputs);

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack input = inv.getItem(i);
			if (input.isEmpty()) {
				break;
			}

			int stackIndex = -1;

			for (int j = 0; j < ingredientsMissing.size(); j++) {
				Ingredient ingr = ingredientsMissing.get(j);
				if (ingr.test(input)) {
					stackIndex = j;
					if (usedSlots != null) {
						usedSlots.add(i);
					}
					break;
				}
			}

			if (stackIndex != -1) {
				ingredientsMissing.remove(stackIndex);
			} else {
				return false;
			}
		}

		return ingredientsMissing.isEmpty();
	}

	/**
	 * Like the vanilla method on recipe interface, but specialHandler is called first, and if it returns
	 * nonnull, that result is used instead of vanilla's
	 */
	public static NonNullList<ItemStack> getRemainingItemsSub(Container inv, Function<ItemStack, ItemStack> specialHandler) {
		NonNullList<ItemStack> ret = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

		for (int i = 0; i < ret.size(); ++i) {
			ItemStack item = inv.getItem(i);
			ItemStack special = specialHandler.apply(item);
			if (special != null) {
				ret.set(i, special);
			} else if (item.getItem().hasCraftingRemainingItem()) {
				ret.set(i, new ItemStack(item.getItem().getCraftingRemainingItem()));
			}
		}

		return ret;
	}

	public static @NotNull Recipe<?> recipeFromNetwork(@NotNull FriendlyByteBuf buffer) {
		ResourceLocation serializerId = buffer.readResourceLocation();
		return BuiltInRegistries.RECIPE_SERIALIZER.getOptional(serializerId)
				.orElseThrow(() -> new IllegalArgumentException("Unknown recipe serializer: " + serializerId))
				.fromNetwork(buffer);
	}

	public static void recipeToNetwork(@NotNull FriendlyByteBuf buffer, Recipe<?> recipe) {
		@SuppressWarnings("unchecked")
		RecipeSerializer<Recipe<?>> recipeSerializer = (RecipeSerializer<Recipe<?>>) recipe.getSerializer();
		buffer.writeResourceLocation(BuiltInRegistries.RECIPE_SERIALIZER.getResourceKey(recipeSerializer)
				.orElseThrow(() -> new IllegalArgumentException("Unregistered recipe serializer: " + recipeSerializer))
				.location());
		recipeSerializer.toNetwork(buffer, recipe);
	}
}
