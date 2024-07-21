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
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.mixin.ShapedRecipeAccessor;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.function.Function;

public class ManaUpgradeRecipe extends ShapedRecipe {
	public static final WrappingRecipeSerializer<ManaUpgradeRecipe> SERIALIZER = new Serializer();

	private ManaUpgradeRecipe(ShapedRecipe recipe) {
		super(recipe.getGroup(), recipe.category(), ((ShapedRecipeAccessor) recipe).botania_getPattern(),
				((ShapedRecipeAccessor) recipe).botania_getResult(), recipe.showNotification());
	}

	public static ItemStack output(ItemStack output, Container inv) {
		ItemStack out = output.copy();
		var outItem = XplatAbstractions.INSTANCE.findManaItem(out);
		if (outItem == null) {
			return out;
		}
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			var item = XplatAbstractions.INSTANCE.findManaItem(stack);
			if (!stack.isEmpty() && item != null) {
				outItem.addMana(item.getMana());
			}
		}
		return out;
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess registries) {
		return output(super.assemble(inv, registries), inv);
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	private static class Serializer implements WrappingRecipeSerializer<ManaUpgradeRecipe> {
		public static final Codec<ManaUpgradeRecipe> CODEC = SHAPED_RECIPE.codec()
				.xmap(ManaUpgradeRecipe::new, Function.identity());

		@Override
		public ManaUpgradeRecipe wrap(Recipe<?> recipe) {
			if (!(recipe instanceof ShapedRecipe shapedRecipe)) {
				throw new IllegalArgumentException("Unsupported recipe type to wrap: " + recipe.getType());
			}
			return new ManaUpgradeRecipe(shapedRecipe);
		}

		@Override
		public Codec<ManaUpgradeRecipe> codec() {
			return CODEC;
		}

		@Override
		public ManaUpgradeRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
			return new ManaUpgradeRecipe(SHAPED_RECIPE.fromNetwork(buffer));
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull ManaUpgradeRecipe recipe) {
			SHAPED_RECIPE.toNetwork(buffer, recipe);
		}
	}
}
