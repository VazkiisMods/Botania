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

import net.minecraft.commands.CacheableFunction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.recipe.StateIngredient;

import java.util.function.Function;

public class OrechidIgnemRecipe extends OrechidRecipe {
	public OrechidIgnemRecipe(StateIngredient input, StateIngredient output, int weight,
			CacheableFunction successFunction, int weightBonus, TagKey<Biome> biomes) {
		super(input, output, weight, successFunction, weightBonus, biomes);
	}

	private OrechidIgnemRecipe(OrechidRecipe orechidRecipe) {
		this(orechidRecipe.getInput(), orechidRecipe.getOutput(), orechidRecipe.getWeight(),
				orechidRecipe.getSuccessFunction().orElse(null), orechidRecipe.getWeightBonus(),
				orechidRecipe.getBiomes().orElse(null));
	}

	@NotNull
	@Override
	public RecipeType<? extends OrechidIgnemRecipe> getType() {
		return BotaniaRecipeTypes.ORECHID_IGNEM_TYPE;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return BotaniaRecipeTypes.ORECHID_IGNEM_SERIALIZER;
	}

	public static class Serializer implements RecipeSerializer<OrechidIgnemRecipe> {
		public static final Codec<OrechidIgnemRecipe> CODEC = BotaniaRecipeTypes.ORECHID_SERIALIZER.codec()
				.xmap(OrechidIgnemRecipe::new, Function.identity());

		@Override
		public Codec<OrechidIgnemRecipe> codec() {
			return null;
		}

		@Override
		public OrechidIgnemRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
			OrechidRecipe base = BotaniaRecipeTypes.ORECHID_SERIALIZER.fromNetwork(buffer);
			return new OrechidIgnemRecipe(base);
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull OrechidIgnemRecipe recipe) {
			BotaniaRecipeTypes.ORECHID_SERIALIZER.toNetwork(buffer, recipe);
		}
	}
}
