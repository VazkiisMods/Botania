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
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;

public class ManaInfusionRecipe implements vazkii.botania.api.recipe.ManaInfusionRecipe {
	private final ItemStack output;
	private final Ingredient input;
	private final int mana;
	private final StateIngredient catalyst;
	private final String group;

	public ManaInfusionRecipe(ItemStack output, Ingredient input, int mana, String group, StateIngredient catalyst) {
		this.output = output;
		this.input = input;
		this.mana = mana;
		this.group = group == null ? "" : group;
		this.catalyst = catalyst == null ? StateIngredients.NONE : catalyst;
	}

	@NotNull
	@Override
	public RecipeSerializer<ManaInfusionRecipe> getSerializer() {
		return BotaniaRecipeTypes.MANA_INFUSION_SERIALIZER;
	}

	@Override
	public boolean matches(ItemStack stack) {
		return input.test(stack);
	}

	@NotNull
	@Override
	public StateIngredient getRecipeCatalyst() {
		return catalyst;
	}

	@Override
	public int getManaToConsume() {
		return mana;
	}

	@NotNull
	@Override
	public ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
		return output;
	}

	@NotNull
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY, input);
	}

	@NotNull
	@Override
	public String getGroup() {
		return group;
	}

	@NotNull
	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(BotaniaBlocks.manaPool);
	}

	protected Ingredient getInput() {
		return input;
	}

	protected ItemStack getOutput() {
		return output;
	}

	public static class Serializer implements RecipeSerializer<ManaInfusionRecipe> {
		public static final MapCodec<ManaInfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				ItemStack.CODEC.fieldOf("output").forGetter(ManaInfusionRecipe::getOutput),
				Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(ManaInfusionRecipe::getInput),
				// Leaving wiggle room for a certain modpack having creative-pool-only recipes
				ExtraCodecs.intRange(1, ManaPoolBlockEntity.MAX_MANA + 1).fieldOf("mana")
						.forGetter(ManaInfusionRecipe::getManaToConsume),
				Codec.STRING.optionalFieldOf("group", "").forGetter(ManaInfusionRecipe::getGroup),
				StateIngredients.TYPED_CODEC.optionalFieldOf("catalyst", StateIngredients.NONE)
						.forGetter(ManaInfusionRecipe::getRecipeCatalyst)
		).apply(instance, ManaInfusionRecipe::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, ManaInfusionRecipe> STREAM_CODEC = StreamCodec.composite(
				ItemStack.STREAM_CODEC, ManaInfusionRecipe::getOutput,
				Ingredient.CONTENTS_STREAM_CODEC, ManaInfusionRecipe::getInput,
				ByteBufCodecs.VAR_INT, ManaInfusionRecipe::getManaToConsume,
				ByteBufCodecs.STRING_UTF8, ManaInfusionRecipe::getGroup,
				ByteBufCodecs.fromCodec(StateIngredients.TYPED_CODEC), ManaInfusionRecipe::getRecipeCatalyst,
				ManaInfusionRecipe::new
		);

		@Override
		public MapCodec<ManaInfusionRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, ManaInfusionRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
