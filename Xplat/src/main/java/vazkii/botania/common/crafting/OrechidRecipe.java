/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.commands.CacheableFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.recipe.StateIngredient;

import java.util.Optional;

public class OrechidRecipe implements vazkii.botania.api.recipe.OrechidRecipe {
	private final StateIngredient input;
	private final StateIngredient output;
	private final int weight;
	@Nullable
	private final CacheableFunction successFunction;
	private final int weightBonus;
	@Nullable
	private final TagKey<Biome> biomes;

	public OrechidRecipe(StateIngredient input, StateIngredient output, int weight,
			@Nullable CacheableFunction successFunction) {
		this(input, output, weight, successFunction, 0, null);
	}

	public OrechidRecipe(StateIngredient input, StateIngredient output, int weight,
			@Nullable CacheableFunction successFunction, int weightBonus, @Nullable TagKey<Biome> biomes) {
		this.input = input;
		this.output = output;
		this.weight = weight;
		this.successFunction = successFunction;
		this.weightBonus = weightBonus;
		this.biomes = biomes;
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private static OrechidRecipe of(StateIngredient input, StateIngredient output, int weight, int biomeWeight, Optional<TagKey<Biome>> biomeTag, Optional<CacheableFunction> successFunction) {
		return new OrechidRecipe(input, output, weight, successFunction.orElse(null), biomeWeight, biomeTag.orElse(null));
	}

	@Override
	public StateIngredient getInput() {
		return input;
	}

	@Override
	public StateIngredient getOutput() {
		return output;
	}

	@Override
	public int getWeight() {
		return weight;
	}

	@Override
	public int getWeight(@NotNull Level level, @NotNull BlockPos pos) {
		return this.biomes != null && level.getBiome(pos).is(this.biomes)
				? getWeight() + getWeightBonus()
				: getWeight();
	}

	public int getWeightBonus() {
		return weightBonus;
	}

	public Optional<TagKey<Biome>> getBiomes() {
		return Optional.ofNullable(biomes);
	}

	@Override
	public Optional<CacheableFunction> getSuccessFunction() {
		return Optional.ofNullable(this.successFunction);
	}

	@NotNull
	@Override
	public RecipeType<? extends vazkii.botania.api.recipe.OrechidRecipe> getType() {
		return BotaniaRecipeTypes.ORECHID_TYPE;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return BotaniaRecipeTypes.ORECHID_SERIALIZER;
	}

	public static class Serializer implements RecipeSerializer<OrechidRecipe> {
		private static final MapCodec<OrechidRecipe> RAW_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				StateIngredients.TYPED_CODEC.fieldOf("input").forGetter(OrechidRecipe::getInput),
				StateIngredients.TYPED_CODEC.fieldOf("output").forGetter(OrechidRecipe::getOutput),
				ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("weight", 0).forGetter(OrechidRecipe::getWeight),
				Codec.INT.optionalFieldOf("biome_bonus_weight", 0).forGetter(OrechidRecipe::getWeightBonus),
				TagKey.codec(Registries.BIOME).optionalFieldOf("biome_bonus_tag").forGetter(OrechidRecipe::getBiomes),
				CacheableFunction.CODEC.optionalFieldOf("success_function").forGetter(OrechidRecipe::getSuccessFunction)
		).apply(instance, OrechidRecipe::of));
		public static final MapCodec<OrechidRecipe> CODEC = RAW_CODEC.validate(orechidRecipe -> {
			if (orechidRecipe.getWeight() == 0 && orechidRecipe.getWeightBonus() == 0) {
				return DataResult.error(() -> "Weight and bonus weight cannot both be 0");
			}
			if (orechidRecipe.getWeight() + orechidRecipe.getWeightBonus() < 0) {
				return DataResult.error(() -> "Weight combined with bonus cannot be less than 0");
			}
			return DataResult.success(orechidRecipe);
		});
		public static final StreamCodec<RegistryFriendlyByteBuf, OrechidRecipe> STREAM_CODEC = StreamCodec.composite(
				StateIngredients.TYPED_STREAM_CODEC, OrechidRecipe::getInput,
				StateIngredients.TYPED_STREAM_CODEC, OrechidRecipe::getOutput,
				ByteBufCodecs.VAR_INT, OrechidRecipe::getWeight,
				ByteBufCodecs.VAR_INT, OrechidRecipe::getWeightBonus,
				ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC.map(
						id -> TagKey.create(Registries.BIOME, id), TagKey::location
				)), OrechidRecipe::getBiomes,
				(in, out, weight, weightBonus, biomes) -> new OrechidRecipe(in, out, weight, null, weightBonus, biomes.orElse(null))
		);

		@Override
		public MapCodec<OrechidRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, OrechidRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
