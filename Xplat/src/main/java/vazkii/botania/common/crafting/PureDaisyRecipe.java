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
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.commands.CacheableFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.recipe.StateIngredient;

import java.util.Optional;

public class PureDaisyRecipe implements vazkii.botania.api.recipe.PureDaisyRecipe {

	public static final int DEFAULT_TIME = 150;

	private final StateIngredient input;
	private final StateIngredient output;
	private final int time;
	private final boolean copyInputProperties;
	private final CacheableFunction successFunction;

	public PureDaisyRecipe(StateIngredient input, StateIngredient output, int time, boolean copyInputProperties,
			@Nullable CacheableFunction successFunction) {
		this.input = input;
		this.output = output;
		this.time = time;
		this.successFunction = successFunction;
		this.copyInputProperties = copyInputProperties;
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private static PureDaisyRecipe of(StateIngredient input, StateIngredient output, int time,
			boolean copyInputProperties, Optional<CacheableFunction> successFunction) {
		return new PureDaisyRecipe(input, output, time, copyInputProperties, successFunction.orElse(null));
	}

	@Override
	public boolean matches(Level world, BlockPos pos, BlockState state) {
		return input.test(state);
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
	public boolean isCopyInputProperties() {
		return copyInputProperties;
	}

	@Override
	public Optional<CacheableFunction> getSuccessFunction() {
		return Optional.ofNullable(this.successFunction);
	}

	@Override
	public int getTime() {
		return time;
	}

	@Override
	public RecipeSerializer<? extends PureDaisyRecipe> getSerializer() {
		return BotaniaRecipeTypes.PURE_DAISY_SERIALIZER;
	}

	public static class Serializer implements RecipeSerializer<PureDaisyRecipe> {
		public static final Codec<PureDaisyRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				StateIngredients.TYPED_CODEC.fieldOf("input").forGetter(PureDaisyRecipe::getInput),
				StateIngredients.TYPED_CODEC.fieldOf("output").forGetter(PureDaisyRecipe::getOutput),
				ExtraCodecs.POSITIVE_INT.optionalFieldOf("time", 0).forGetter(PureDaisyRecipe::getTime),
				Codec.BOOL.optionalFieldOf("copy_properties", false).forGetter(PureDaisyRecipe::isCopyInputProperties),
				CacheableFunction.CODEC.optionalFieldOf("success_function").forGetter(PureDaisyRecipe::getSuccessFunction)
		).apply(instance, PureDaisyRecipe::of));

		@Override
		public Codec<PureDaisyRecipe> codec() {
			return CODEC;
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buffer, PureDaisyRecipe recipe) {
			StateIngredients.toNetwork(buffer, recipe.getInput());
			StateIngredients.toNetwork(buffer, recipe.getOutput());
			buffer.writeVarInt(recipe.getTime());
			buffer.writeBoolean(recipe.isCopyInputProperties());
		}

		@NotNull
		@Override
		public PureDaisyRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
			var input = StateIngredients.fromNetwork(buffer);
			var output = StateIngredients.fromNetwork(buffer);
			var time = buffer.readVarInt();
			var copyInputProperties = buffer.readBoolean();
			return new PureDaisyRecipe(input, output, time, copyInputProperties, null);
		}
	}
}
