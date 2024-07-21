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

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

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
	public ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess registries) {
		return ManaUpgradeRecipe.output(super.assemble(inv, registries), inv);
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	private static class Serializer implements WrappingRecipeSerializer<ShapelessManaUpgradeRecipe> {
		public static final Codec<ShapelessManaUpgradeRecipe> CODEC = SHAPELESS_RECIPE.codec()
				.xmap(ShapelessManaUpgradeRecipe::new, Function.identity());

		@Override
		public ShapelessManaUpgradeRecipe wrap(Recipe<?> recipe) {
			if (!(recipe instanceof ShapelessRecipe shapelessRecipe)) {
				throw new IllegalArgumentException("Unsupported recipe type to wrap: " + recipe.getType());
			}
			return new ShapelessManaUpgradeRecipe(shapelessRecipe);
		}

		@Override
		public Codec<ShapelessManaUpgradeRecipe> codec() {
			return CODEC;
		}

		@NotNull
		@Override
		public ShapelessManaUpgradeRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
			return new ShapelessManaUpgradeRecipe(SHAPELESS_RECIPE.fromNetwork(buffer));
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull ShapelessManaUpgradeRecipe recipe) {
			SHAPELESS_RECIPE.toNetwork(buffer, recipe);
		}
	}
}
