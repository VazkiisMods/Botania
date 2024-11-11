/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting;

import com.google.common.base.Preconditions;
import com.mojang.serialization.DataResult;
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
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.crafting.recipe.RecipeUtils;

import java.util.ArrayList;
import java.util.List;

public class RunicAltarRecipe implements vazkii.botania.api.recipe.RunicAltarRecipe {
	private final ItemStack output;
	private final Ingredient reagent;
	private final NonNullList<Ingredient> ingredients;
	private final NonNullList<Ingredient> catalysts;
	private final NonNullList<Ingredient> allInputs;
	private final int mana;

	public RunicAltarRecipe(ItemStack output, Ingredient reagent, int mana, Ingredient[] ingredients, Ingredient[] catalysts) {
		int numIngredients = ingredients.length;
		int numCatalysts = catalysts.length;
		Preconditions.checkArgument(numIngredients <= 16, "Cannot have more than 16 ingredients");
		this.output = output;
		this.reagent = reagent;
		this.ingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
		this.catalysts = NonNullList.of(Ingredient.EMPTY, catalysts);
		this.mana = mana;

		this.allInputs = NonNullList.withSize(numIngredients + numCatalysts, Ingredient.EMPTY);
		for (int i = 0; i < numIngredients; i++) {
			allInputs.set(i, ingredients[i]);
		}
		for (int i = 0; i < numCatalysts; i++) {
			allInputs.set(i + numIngredients, catalysts[i]);
		}
	}

	private static RunicAltarRecipe of(List<Ingredient> ingredients, List<Ingredient> catalysts, Ingredient reagent, int mana, ItemStack output) {
		return new RunicAltarRecipe(output, reagent, mana, ingredients.toArray(Ingredient[]::new), catalysts.toArray(Ingredient[]::new));
	}

	@Override
	public boolean matches(RecipeInput container, @NotNull Level world) {
		return RecipeUtils.matches(allInputs, container, null);
	}

	@NotNull
	@Override
	public NonNullList<ItemStack> getRemainingItems(RecipeInput container) {
		List<Ingredient> ingredientsMissing = new ArrayList<>(ingredients);
		List<Ingredient> catalystsMissing = new ArrayList<>(catalysts);
		NonNullList<ItemStack> foundCatalysts = NonNullList.of(ItemStack.EMPTY);

		containerLoop: for (int i = 0; i < container.size(); i++) {
			ItemStack input = container.getItem(i);
			if (input.isEmpty()) {
				break;
			}

			for (int j = 0; j < ingredientsMissing.size(); j++) {
				Ingredient ingr = ingredientsMissing.get(j);
				if (ingr.test(input)) {
					ingredientsMissing.remove(j);
					continue containerLoop;
				}
			}

			for (int j = 0; j < catalystsMissing.size(); j++) {
				Ingredient ingr = catalystsMissing.get(j);
				if (ingr.test(input)) {
					catalystsMissing.remove(j);
					foundCatalysts.add(input);
					continue containerLoop;
				}
			}
		}

		return foundCatalysts;
	}

	@NotNull
	@Override
	public final ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
		return output;
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull RecipeInput inv, @NotNull HolderLookup.Provider registries) {
		return getResultItem(registries).copy();
	}

	@NotNull
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return ingredients;
	}

	@NotNull
	@Override
	public NonNullList<Ingredient> getCatalysts() {
		return catalysts;
	}

	@NotNull
	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(BotaniaBlocks.runeAltar);
	}

	@NotNull
	@Override
	public RecipeSerializer<? extends RunicAltarRecipe> getSerializer() {
		return BotaniaRecipeTypes.RUNE_SERIALIZER;
	}

	@Override
	public int getMana() {
		return mana;
	}

	public ItemStack getOutput() {
		return output;
	}

	@Override
	public Ingredient getReagent() {
		return reagent;
	}

	public static class Serializer implements RecipeSerializer<RunicAltarRecipe> {
		private static final MapCodec<RunicAltarRecipe> RAW_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				ExtraCodecs.nonEmptyList(Ingredient.CODEC_NONEMPTY.listOf()).fieldOf("ingredients")
						.forGetter(RunicAltarRecipe::getIngredients),
				ExtraCodecs.nonEmptyList(Ingredient.CODEC_NONEMPTY.listOf()).fieldOf("catalysts")
						.forGetter(RunicAltarRecipe::getCatalysts),
				Ingredient.CODEC_NONEMPTY.fieldOf("reagent").forGetter(RunicAltarRecipe::getReagent),
				ExtraCodecs.POSITIVE_INT.fieldOf("mana").forGetter(RunicAltarRecipe::getMana),
				ItemStack.SIMPLE_ITEM_CODEC.fieldOf("output").forGetter(RunicAltarRecipe::getOutput)
		).apply(instance, RunicAltarRecipe::of));
		public static final MapCodec<RunicAltarRecipe> CODEC = RAW_CODEC.validate(recipe -> {
			if (recipe.getIngredients().size() + recipe.getCatalysts().size() > 16) {
				return DataResult.error(() -> "Cannot have more than 16 ingredients and catalysts in total");
			}
			return DataResult.success(recipe);
		});
		public static final StreamCodec<RegistryFriendlyByteBuf, RunicAltarRecipe> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.collection(ArrayList::new, Ingredient.CONTENTS_STREAM_CODEC), RunicAltarRecipe::getIngredients,
				ByteBufCodecs.collection(ArrayList::new, Ingredient.CONTENTS_STREAM_CODEC), RunicAltarRecipe::getCatalysts,
				Ingredient.CONTENTS_STREAM_CODEC, RunicAltarRecipe::getReagent,
				ByteBufCodecs.VAR_INT, RunicAltarRecipe::getMana,
				ItemStack.STREAM_CODEC, RunicAltarRecipe::getOutput,
				RunicAltarRecipe::of
		);

		@Override
		public MapCodec<RunicAltarRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, RunicAltarRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
