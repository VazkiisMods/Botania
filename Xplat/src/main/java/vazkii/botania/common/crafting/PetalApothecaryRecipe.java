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
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
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
	public boolean matches(Container inv, @NotNull Level world) {
		List<Ingredient> ingredientsMissing = new ArrayList<>(ingredients);

		for (int i = 0; i < inv.getContainerSize(); i++) {
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
	public final ItemStack getResultItem(@NotNull RegistryAccess registries) {
		return output;
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull Container inv, @NotNull RegistryAccess registries) {
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
		public final Codec<PetalApothecaryRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ItemStack.CODEC.fieldOf("output").forGetter(PetalApothecaryRecipe::getOutput),
				Ingredient.CODEC_NONEMPTY.fieldOf("reagent").forGetter(PetalApothecaryRecipe::getReagent),
				ExtraCodecs.validate(ExtraCodecs.nonEmptyList(Ingredient.CODEC_NONEMPTY.listOf()), ingredients -> {
					if (ingredients.size() > 16) {
						return DataResult.error(() -> "Cannot have more than 16 ingredients");
					}
					return DataResult.success(ingredients);
				}).fieldOf("ingredients").forGetter(PetalApothecaryRecipe::getIngredients)
		).apply(instance, PetalApothecaryRecipe::of));

		@Override
		public Codec<PetalApothecaryRecipe> codec() {
			return CODEC;
		}

		@Override
		public PetalApothecaryRecipe fromNetwork(@NotNull FriendlyByteBuf buf) {
			Ingredient[] inputs = new Ingredient[buf.readVarInt()];
			for (int i = 0; i < inputs.length; i++) {
				inputs[i] = Ingredient.fromNetwork(buf);
			}
			Ingredient reagent = Ingredient.fromNetwork(buf);
			ItemStack output = buf.readItem();
			return new PetalApothecaryRecipe(output, reagent, inputs);
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull PetalApothecaryRecipe recipe) {
			buf.writeVarInt(recipe.getIngredients().size());
			for (Ingredient input : recipe.getIngredients()) {
				input.toNetwork(buf);
			}
			recipe.getReagent().toNetwork(buf);
			buf.writeItem(recipe.getOutput());
		}
	}
}
