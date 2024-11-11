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

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.block.BotaniaBlocks;

import java.util.ArrayList;
import java.util.List;

public class PetalApothecaryRecipe implements vazkii.botania.api.recipe.PetalApothecaryRecipe {
	private final ItemStack output;
	private final Ingredient reagent;
	private final NonNullList<Ingredient> ingredients;

	public PetalApothecaryRecipe(ItemStack output, Ingredient reagent, Ingredient... ingredients) {
		this.output = output;
		this.reagent = reagent;
		this.ingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
	}

	private static PetalApothecaryRecipe of(ItemStack output, Ingredient reagent, List<Ingredient> ingredients) {
		return new PetalApothecaryRecipe(output, reagent, ingredients.toArray(Ingredient[]::new));
	}

	@Override
	public boolean matches(RecipeInput inv, @NotNull Level world) {
		List<Ingredient> ingredientsMissing = new ArrayList<>(ingredients);

		for (int i = 0; i < inv.size(); i++) {
			ItemStack input = inv.getItem(i);
			if (input.isEmpty()) {
				break;
			}

			int stackIndex = -1;

			for (int j = 0; j < ingredientsMissing.size(); j++) {
				Ingredient ingr = ingredientsMissing.get(j);
				if (ingr.test(input)) {
					stackIndex = j;
					break;
				}
			}

			if (stackIndex != -1) {
				ingredientsMissing.remove(stackIndex);
			} else {
				return false;
			}
		}

		return ingredientsMissing.isEmpty();
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

	public ItemStack getOutput() {
		return output;
	}

	@Override
	public Ingredient getReagent() {
		return reagent;
	}

	@NotNull
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return ingredients;
	}

	@NotNull
	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(BotaniaBlocks.defaultAltar);
	}

	@NotNull
	@Override
	public RecipeSerializer<? extends PetalApothecaryRecipe> getSerializer() {
		return BotaniaRecipeTypes.PETAL_SERIALIZER;
	}

	public static class Serializer implements RecipeSerializer<PetalApothecaryRecipe> {
		public final MapCodec<PetalApothecaryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				ItemStack.CODEC.fieldOf("output").forGetter(PetalApothecaryRecipe::getOutput),
				Ingredient.CODEC_NONEMPTY.fieldOf("reagent").forGetter(PetalApothecaryRecipe::getReagent),
				Ingredient.CODEC_NONEMPTY.listOf(1, 16).fieldOf("ingredients").forGetter(PetalApothecaryRecipe::getIngredients)
		).apply(instance, PetalApothecaryRecipe::of));
		public static final StreamCodec<RegistryFriendlyByteBuf, PetalApothecaryRecipe> STREAM_CODEC = StreamCodec.composite(
				ItemStack.STREAM_CODEC, PetalApothecaryRecipe::getOutput,
				Ingredient.CONTENTS_STREAM_CODEC, PetalApothecaryRecipe::getReagent,
				ByteBufCodecs.collection(ArrayList::new, Ingredient.CONTENTS_STREAM_CODEC), PetalApothecaryRecipe::getIngredients,
				PetalApothecaryRecipe::of
		);

		@Override
		public MapCodec<PetalApothecaryRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, PetalApothecaryRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
