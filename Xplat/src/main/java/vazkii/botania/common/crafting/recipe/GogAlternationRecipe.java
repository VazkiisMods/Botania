/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting.recipe;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.xplat.XplatAbstractions;

import java.util.function.Supplier;

public class GogAlternationRecipe<C extends RecipeInput> implements Recipe<C> {
	public static final RecipeSerializer<GogAlternationRecipe<?>> SERIALIZER = new Serializer();

	private final Supplier<Boolean> isGog = Suppliers.memoize(XplatAbstractions.INSTANCE::gogLoaded);
	private final Recipe<?> baseRecipe;
	private final Recipe<?> gogRecipe;

	public GogAlternationRecipe(Recipe<?> baseRecipe, Recipe<?> gogRecipe) {
		this.baseRecipe = baseRecipe;
		this.gogRecipe = gogRecipe;
	}

	public Recipe<?> getBaseRecipe() {
		return baseRecipe;
	}

	public Recipe<?> getGogRecipe() {
		return gogRecipe;
	}

	@SuppressWarnings("unchecked")
	public Recipe<C> getRecipe() {
		return isGog.get() ? (Recipe<C>) gogRecipe : (Recipe<C>) baseRecipe;
	}

	@Override
	public boolean matches(C container, Level level) {
		return getRecipe().matches(container, level);
	}

	@Override
	public ItemStack assemble(C container, HolderLookup.Provider registryAccess) {
		return getRecipe().assemble(container, registryAccess);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return getRecipe().canCraftInDimensions(width, height);
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
		return getRecipe().getResultItem(registryAccess);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return getRecipe().getType();
	}

	private static class Serializer implements RecipeSerializer<GogAlternationRecipe<?>> {
		private static final MapCodec<GogAlternationRecipe<?>> RAW_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
						Recipe.CODEC.fieldOf("base").forGetter(GogAlternationRecipe::getBaseRecipe),
						Recipe.CODEC.fieldOf("gog").forGetter(GogAlternationRecipe::getGogRecipe)
				).apply(instance, GogAlternationRecipe::new));
		public static final MapCodec<GogAlternationRecipe<?>> CODEC = RAW_CODEC.validate(recipe -> {
					if (recipe.getBaseRecipe().getType() != recipe.getGogRecipe().getType()) {
						return DataResult.error(() -> "Subrecipes must have matching types");
					}
					return DataResult.success(recipe);
				});
		public static final StreamCodec<RegistryFriendlyByteBuf, GogAlternationRecipe<?>> STREAM_CODEC = StreamCodec.composite(
				Recipe.STREAM_CODEC, GogAlternationRecipe::getBaseRecipe,
				Recipe.STREAM_CODEC, GogAlternationRecipe::getGogRecipe,
				GogAlternationRecipe::new
		);

		@Override
		public MapCodec<GogAlternationRecipe<?>> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, GogAlternationRecipe<?>> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
