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
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.mixin.ShapedRecipeAccessor;

import java.util.function.Function;

public class ArmorUpgradeRecipe extends ShapedRecipe {
	public static final WrappingRecipeSerializer<ArmorUpgradeRecipe> SERIALIZER = new Serializer();

	private ArmorUpgradeRecipe(ShapedRecipe recipe) {
		super(recipe.getGroup(), recipe.category(), ((ShapedRecipeAccessor) recipe).botania_getPattern(),
				((ShapedRecipeAccessor) recipe).botania_getResult(), recipe.showNotification());
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingInput inv, @NotNull HolderLookup.Provider registries) {
		ItemStack out = super.assemble(inv, registries);
		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.hasTag() && stack.getItem() instanceof ArmorItem) {
				out.setTag(stack.getTag());
				break;
			}
		}
		return out;
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	private static class Serializer implements WrappingRecipeSerializer<ArmorUpgradeRecipe> {
		public static final MapCodec<ArmorUpgradeRecipe> CODEC = SHAPED_RECIPE.codec()
				.xmap(ArmorUpgradeRecipe::new, Function.identity());
		public static final StreamCodec<RegistryFriendlyByteBuf, ArmorUpgradeRecipe> STREAM_CODEC = SHAPED_RECIPE.streamCodec()
				.map(ArmorUpgradeRecipe::new, Function.identity());


		@Override
		public ArmorUpgradeRecipe wrap(Recipe<?> recipe) {
			if (!(recipe instanceof ShapedRecipe shapedRecipe)) {
				throw new IllegalArgumentException("Unsupported recipe type to wrap: " + recipe.getType());
			}
			return new ArmorUpgradeRecipe(shapedRecipe);
		}

		@Override
		public MapCodec<ArmorUpgradeRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, ArmorUpgradeRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
