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

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.recipe.ElvenTradeRecipe;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.LexicaBotaniaItem;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LexiconElvenTradeRecipe implements ElvenTradeRecipe {

	public LexiconElvenTradeRecipe() {}

	@Override
	public boolean containsItem(ItemStack stack) {
		return stack.is(BotaniaItems.lexicon) && !ItemNBTHelper.getBoolean(stack, LexicaBotaniaItem.TAG_ELVEN_UNLOCK, false);
	}

	@NotNull
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.withSize(1, Ingredient.of(BotaniaItems.lexicon));
	}

	@NotNull
	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(BotaniaBlocks.alfPortal);
	}

	@Override
	public List<ItemStack> getOutputs() {
		ItemStack stack = new ItemStack(BotaniaItems.lexicon);
		stack.getOrCreateTag().putBoolean(LexicaBotaniaItem.TAG_ELVEN_UNLOCK, true);
		return Collections.singletonList(stack);
	}

	@Override
	public Optional<List<ItemStack>> match(List<ItemStack> stacks) {
		for (ItemStack stack : stacks) {
			if (containsItem(stack)) {
				return Optional.of(Collections.singletonList(stack));
			}
		}
		return Optional.empty();
	}

	@Override
	public List<ItemStack> getOutputs(List<ItemStack> inputs) {
		ItemStack stack = inputs.get(0).copy();
		stack.getOrCreateTag().putBoolean(LexicaBotaniaItem.TAG_ELVEN_UNLOCK, true);
		return Collections.singletonList(stack);
	}

	@NotNull
	@Override
	public RecipeSerializer<LexiconElvenTradeRecipe> getSerializer() {
		return BotaniaRecipeTypes.LEXICON_ELVEN_TRADE_SERIALIZER;
	}

	public static class Serializer implements RecipeSerializer<LexiconElvenTradeRecipe> {
		@Override
		public Codec<LexiconElvenTradeRecipe> codec() {
			return Codec.unit(LexiconElvenTradeRecipe::new);
		}

		@Override
		public LexiconElvenTradeRecipe fromNetwork(FriendlyByteBuf friendlyByteBuf) {
			return new LexiconElvenTradeRecipe();
		}

		@Override
		public void toNetwork(FriendlyByteBuf friendlyByteBuf, LexiconElvenTradeRecipe recipe) {
			// noop
		}
	}
}
