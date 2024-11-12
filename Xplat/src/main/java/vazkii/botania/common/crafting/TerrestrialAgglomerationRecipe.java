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
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.crafting.recipe.RecipeUtils;

import java.util.List;

public class TerrestrialAgglomerationRecipe implements vazkii.botania.api.recipe.TerrestrialAgglomerationRecipe {
	private final int mana;
	private final NonNullList<Ingredient> ingredients;
	private final ItemStack output;

	public TerrestrialAgglomerationRecipe(int mana, ItemStack output, Ingredient... ingredients) {
		this.mana = mana;
		this.ingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
		this.output = output;
	}

	private static TerrestrialAgglomerationRecipe of(List<Ingredient> ingredients, int mana, ItemStack output) {
		return new TerrestrialAgglomerationRecipe(mana, output, ingredients.toArray(Ingredient[]::new));
	}

	@Override
	public int getMana() {
		return mana;
	}

	public ItemStack getOutput() {
		return output;
	}

	@Override
	public boolean matches(RecipeInput inv, @NotNull Level world) {
		int nonEmptySlots = 0;
		for (int i = 0; i < inv.size(); i++) {
			if (!inv.getItem(i).isEmpty()) {
				if (inv.getItem(i).getCount() > 1) {
					return false;
				}
				nonEmptySlots++;
			}
		}

		IntOpenHashSet usedSlots = new IntOpenHashSet(inv.size());
		return RecipeUtils.matches(ingredients, inv, usedSlots) && usedSlots.size() == nonEmptySlots;
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull RecipeInput inv, @NotNull HolderLookup.Provider registries) {
		return output.copy();
	}

	@NotNull
	@Override
	public ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
		return output;
	}

	@NotNull
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return ingredients;
	}

	@NotNull
	@Override
	public RecipeSerializer<? extends TerrestrialAgglomerationRecipe> getSerializer() {
		return BotaniaRecipeTypes.TERRA_PLATE_SERIALIZER;
	}

	public static class Serializer implements RecipeSerializer<TerrestrialAgglomerationRecipe> {
		public static final MapCodec<TerrestrialAgglomerationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				ExtraCodecs.nonEmptyList(Ingredient.CODEC_NONEMPTY.listOf()).fieldOf("ingredients")
						.forGetter(TerrestrialAgglomerationRecipe::getIngredients),
				ExtraCodecs.POSITIVE_INT.fieldOf("mana").forGetter(TerrestrialAgglomerationRecipe::getMana),
				ItemStack.CODEC.fieldOf("result").forGetter(TerrestrialAgglomerationRecipe::getOutput)
		).apply(instance, TerrestrialAgglomerationRecipe::of));
		public static final StreamCodec<RegistryFriendlyByteBuf, TerrestrialAgglomerationRecipe> STREAM_CODEC = StreamCodec.composite(
				Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), TerrestrialAgglomerationRecipe::getIngredients,
				ByteBufCodecs.VAR_INT, TerrestrialAgglomerationRecipe::getMana,
				ItemStack.STREAM_CODEC, TerrestrialAgglomerationRecipe::getOutput,
				TerrestrialAgglomerationRecipe::of
		);

		@Override
		public MapCodec<TerrestrialAgglomerationRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, TerrestrialAgglomerationRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
