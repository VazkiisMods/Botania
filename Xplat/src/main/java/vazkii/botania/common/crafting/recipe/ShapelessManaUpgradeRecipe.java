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

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.mixin.ShapelessRecipeAccessor;

import java.util.function.Function;

public class ShapelessManaUpgradeRecipe extends ShapelessRecipe {
	public static final WrappingRecipeSerializer<ShapelessManaUpgradeRecipe> SERIALIZER = new Serializer();

	private ShapelessManaUpgradeRecipe(ShapelessRecipe recipe) {
		super(recipe.getGroup(), recipe.category(), ((ShapelessRecipeAccessor) recipe).botania_getResult(), recipe.getIngredients());
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingInput inv, @NotNull HolderLookup.Provider registries) {
		return ManaUpgradeRecipe.output(super.assemble(inv, registries), inv);
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	private static class Serializer implements WrappingRecipeSerializer<ShapelessManaUpgradeRecipe> {
		public static final MapCodec<ShapelessManaUpgradeRecipe> CODEC = SHAPELESS_RECIPE.codec()
				.xmap(ShapelessManaUpgradeRecipe::new, Function.identity());
		public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessManaUpgradeRecipe> STREAM_CODEC = SHAPELESS_RECIPE.streamCodec()
				.map(ShapelessManaUpgradeRecipe::new, Function.identity());

		@Override
		public ShapelessManaUpgradeRecipe wrap(Recipe<?> recipe) {
			if (!(recipe instanceof ShapelessRecipe shapelessRecipe)) {
				throw new IllegalArgumentException("Unsupported recipe type to wrap: " + recipe.getType());
			}
			return new ShapelessManaUpgradeRecipe(shapelessRecipe);
		}

		@Override
		public MapCodec<ShapelessManaUpgradeRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, ShapelessManaUpgradeRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
