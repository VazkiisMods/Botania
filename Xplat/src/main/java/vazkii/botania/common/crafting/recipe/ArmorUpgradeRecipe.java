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
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

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
	public ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess registries) {
		ItemStack out = super.assemble(inv, registries);
		for (int i = 0; i < inv.getContainerSize(); i++) {
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
		public static final Codec<ArmorUpgradeRecipe> CODEC = SHAPED_RECIPE.codec()
				.xmap(ArmorUpgradeRecipe::new, Function.identity());

		@Override
		public ArmorUpgradeRecipe wrap(Recipe<?> recipe) {
			if (!(recipe instanceof ShapedRecipe shapedRecipe)) {
				throw new IllegalArgumentException("Unsupported recipe type to wrap: " + recipe.getType());
			}
			return new ArmorUpgradeRecipe(shapedRecipe);
		}

		@Override
		public Codec<ArmorUpgradeRecipe> codec() {
			return CODEC;
		}

		@Override
		public ArmorUpgradeRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
			return new ArmorUpgradeRecipe(SHAPED_RECIPE.fromNetwork(buffer));
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull ArmorUpgradeRecipe recipe) {
			SHAPED_RECIPE.toNetwork(buffer, recipe);
		}
	}
}
