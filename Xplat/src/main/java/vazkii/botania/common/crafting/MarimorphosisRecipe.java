/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting;

import com.mojang.serialization.MapCodec;

import net.minecraft.commands.CacheableFunction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.recipe.StateIngredient;

import java.util.function.Function;

public class MarimorphosisRecipe extends OrechidRecipe {
	public MarimorphosisRecipe(StateIngredient input, StateIngredient output, int weight,
			CacheableFunction successFunction, int weightBonus, TagKey<Biome> biomes) {
		super(input, output, weight, successFunction, weightBonus, biomes);
	}

	private MarimorphosisRecipe(OrechidRecipe orechidRecipe) {
		this(orechidRecipe.getInput(), orechidRecipe.getOutput(), orechidRecipe.getWeight(),
				orechidRecipe.getSuccessFunction().orElse(null), orechidRecipe.getWeightBonus(),
				orechidRecipe.getBiomes().orElse(null));
	}

	@NotNull
	@Override
	public RecipeType<? extends vazkii.botania.api.recipe.OrechidRecipe> getType() {
		return BotaniaRecipeTypes.MARIMORPHOSIS_TYPE;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return BotaniaRecipeTypes.MARIMORPHOSIS_SERIALIZER;
	}

	public static class Serializer implements RecipeSerializer<MarimorphosisRecipe> {
		public static final MapCodec<MarimorphosisRecipe> CODEC = BotaniaRecipeTypes.ORECHID_SERIALIZER.codec()
				.xmap(MarimorphosisRecipe::new, Function.identity());
		public static final StreamCodec<RegistryFriendlyByteBuf, MarimorphosisRecipe> STREAM_CODEC = BotaniaRecipeTypes.ORECHID_SERIALIZER.streamCodec()
				.map(MarimorphosisRecipe::new, Function.identity());

		@Override
		public MapCodec<MarimorphosisRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, MarimorphosisRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
