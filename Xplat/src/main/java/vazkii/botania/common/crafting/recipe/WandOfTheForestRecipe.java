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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.block.decor.BotaniaMushroomBlock;
import vazkii.botania.common.item.WandOfTheForestItem;
import vazkii.botania.common.item.material.MysticalPetalItem;
import vazkii.botania.mixin.ShapedRecipeAccessor;

import java.util.function.Function;

public class WandOfTheForestRecipe extends ShapedRecipe {
	public static final WrappingRecipeSerializer<WandOfTheForestRecipe> SERIALIZER = new Serializer();

	private WandOfTheForestRecipe(ShapedRecipe recipe) {
		super(recipe.getGroup(), recipe.category(), ((ShapedRecipeAccessor) recipe).botania_getPattern(),
				((ShapedRecipeAccessor) recipe).botania_getResult(), recipe.showNotification());
	}

	@NotNull
	@Override
	public ItemStack assemble(CraftingInput inv, @NotNull HolderLookup.Provider registries) {
		int first = -1;
		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getItem(i);
			Item item = stack.getItem();

			int colorId;
			if (item instanceof MysticalPetalItem petal) {
				colorId = petal.color.getId();
			} else if (item instanceof BlockItem block && block.getBlock() instanceof BotaniaMushroomBlock mushroom) {
				colorId = mushroom.color.getId();
			} else {
				continue;
			}
			if (first == -1) {
				first = colorId;
			} else {
				return WandOfTheForestItem.setColors(getResultItem(registries).copy(), first, colorId);
			}
		}
		return WandOfTheForestItem.setColors(getResultItem(registries).copy(), first != -1 ? first : 0, 0);
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	private static class Serializer implements WrappingRecipeSerializer<WandOfTheForestRecipe> {
		public static final MapCodec<WandOfTheForestRecipe> CODEC = SHAPED_RECIPE.codec()
				.xmap(WandOfTheForestRecipe::new, Function.identity());
		public static final StreamCodec<RegistryFriendlyByteBuf, WandOfTheForestRecipe> STREAM_CODEC = SHAPED_RECIPE.streamCodec()
				.map(WandOfTheForestRecipe::new, Function.identity());

		@Override
		public WandOfTheForestRecipe wrap(Recipe<?> recipe) {
			if (!(recipe instanceof ShapedRecipe shapedRecipe)) {
				throw new IllegalArgumentException("Unsupported recipe type to wrap: " + recipe.getType());
			}
			return new WandOfTheForestRecipe(shapedRecipe);
		}

		@Override
		public MapCodec<WandOfTheForestRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, WandOfTheForestRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
