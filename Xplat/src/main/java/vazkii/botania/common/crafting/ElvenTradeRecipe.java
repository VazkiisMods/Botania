/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.block.BotaniaBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ElvenTradeRecipe implements vazkii.botania.api.recipe.ElvenTradeRecipe {
	private final ImmutableList<ItemStack> outputs;
	private final NonNullList<Ingredient> inputs;

	public ElvenTradeRecipe(ItemStack[] outputs, Ingredient... inputs) {
		this.outputs = ImmutableList.copyOf(outputs);
		this.inputs = NonNullList.of(Ingredient.EMPTY, inputs);
	}

	public ElvenTradeRecipe(List<ItemStack> outputs, List<Ingredient> ingredients) {
		this.outputs = ImmutableList.copyOf(outputs);
		this.inputs = NonNullList.of(Ingredient.EMPTY, ingredients.toArray(Ingredient[]::new));
	}

	@Override
	public Optional<List<ItemStack>> match(List<ItemStack> stacks) {
		List<Ingredient> inputsMissing = new ArrayList<>(inputs);
		List<ItemStack> stacksToRemove = new ArrayList<>();

		for (ItemStack stack : stacks) {
			if (stack.isEmpty()) {
				continue;
			}
			if (inputsMissing.isEmpty()) {
				break;
			}

			int stackIndex = -1;

			for (int i = 0; i < inputsMissing.size(); i++) {
				Ingredient ingr = inputsMissing.get(i);
				if (ingr.test(stack)) {
					if (!stacksToRemove.contains(stack)) {
						stacksToRemove.add(stack);
					}
					stackIndex = i;
					break;
				}
			}

			if (stackIndex != -1) {
				inputsMissing.remove(stackIndex);
			}
		}

		return inputsMissing.isEmpty() ? Optional.of(stacksToRemove) : Optional.empty();
	}

	@Override
	public boolean containsItem(ItemStack stack) {
		for (Ingredient input : inputs) {
			if (input.test(stack)) {
				return true;
			}
		}
		return false;
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return BotaniaRecipeTypes.ELVEN_TRADE_SERIALIZER;
	}

	@NotNull
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return inputs;
	}

	@NotNull
	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(BotaniaBlocks.alfPortal);
	}

	@Override
	public List<ItemStack> getOutputs() {
		return outputs;
	}

	@Override
	public List<ItemStack> getOutputs(List<ItemStack> inputs) {
		return getOutputs();
	}

	public static class Serializer implements RecipeSerializer<ElvenTradeRecipe> {
		public static final Codec<ElvenTradeRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ExtraCodecs.nonEmptyList(ItemStack.ITEM_WITH_COUNT_CODEC.listOf()).fieldOf("output").forGetter(ElvenTradeRecipe::getOutputs),
				ExtraCodecs.nonEmptyList(Ingredient.CODEC_NONEMPTY.listOf()).fieldOf("ingredients").forGetter(ElvenTradeRecipe::getIngredients)
		).apply(instance, ElvenTradeRecipe::new));

		@Override
		public Codec<ElvenTradeRecipe> codec() {
			return CODEC;
		}

		@Override
		public ElvenTradeRecipe fromNetwork(FriendlyByteBuf buf) {
			Ingredient[] inputs = new Ingredient[buf.readVarInt()];
			for (int i = 0; i < inputs.length; i++) {
				inputs[i] = Ingredient.fromNetwork(buf);
			}
			ItemStack[] outputs = new ItemStack[buf.readVarInt()];
			for (int i = 0; i < outputs.length; i++) {
				outputs[i] = buf.readItem();
			}
			return new ElvenTradeRecipe(outputs, inputs);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, ElvenTradeRecipe recipe) {
			buf.writeVarInt(recipe.getIngredients().size());
			for (Ingredient input : recipe.getIngredients()) {
				input.toNetwork(buf);
			}
			buf.writeVarInt(recipe.getOutputs().size());
			for (ItemStack output : recipe.getOutputs()) {
				buf.writeItem(output);
			}
		}
	}
}
