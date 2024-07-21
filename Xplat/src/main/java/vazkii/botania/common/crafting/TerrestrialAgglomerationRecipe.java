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

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

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
	public boolean matches(Container inv, @NotNull Level world) {
		int nonEmptySlots = 0;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			if (!inv.getItem(i).isEmpty()) {
				if (inv.getItem(i).getCount() > 1) {
					return false;
				}
				nonEmptySlots++;
			}
		}

		IntOpenHashSet usedSlots = new IntOpenHashSet(inv.getContainerSize());
		return RecipeUtils.matches(ingredients, inv, usedSlots) && usedSlots.size() == nonEmptySlots;
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull Container inv, @NotNull RegistryAccess registries) {
		return output.copy();
	}

	@NotNull
	@Override
	public ItemStack getResultItem(@NotNull RegistryAccess registries) {
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
		public static final Codec<TerrestrialAgglomerationRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ExtraCodecs.nonEmptyList(Ingredient.CODEC_NONEMPTY.listOf()).fieldOf("ingredients")
						.forGetter(TerrestrialAgglomerationRecipe::getIngredients),
				ExtraCodecs.POSITIVE_INT.fieldOf("mana").forGetter(TerrestrialAgglomerationRecipe::getMana),
				ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(TerrestrialAgglomerationRecipe::getOutput)
		).apply(instance, TerrestrialAgglomerationRecipe::of));

		@Override
		public Codec<TerrestrialAgglomerationRecipe> codec() {
			return CODEC;
		}

		@Override
		public TerrestrialAgglomerationRecipe fromNetwork(FriendlyByteBuf buffer) {
			int mana = buffer.readVarInt();
			Ingredient[] ingredients = new Ingredient[buffer.readVarInt()];
			for (int i = 0; i < ingredients.length; i++) {
				ingredients[i] = Ingredient.fromNetwork(buffer);
			}
			ItemStack output = buffer.readItem();
			return new TerrestrialAgglomerationRecipe(mana, output, ingredients);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, TerrestrialAgglomerationRecipe recipe) {
			buffer.writeVarInt(recipe.getMana());
			buffer.writeVarInt(recipe.getIngredients().size());
			for (Ingredient ingr : recipe.getIngredients()) {
				ingr.toNetwork(buffer);
			}
			buffer.writeItem(recipe.getOutput());
		}
	}
}
