/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting.recipe;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.crafting.RunicAltarRecipe;
import vazkii.botania.common.helper.ItemNBTHelper;

import java.util.function.Function;

public class HeadRecipe extends RunicAltarRecipe {

	public HeadRecipe(ItemStack output, Ingredient reagent, int mana, Ingredient... inputs) {
		super(output, reagent, mana, inputs, new Ingredient[0]);
	}

	private HeadRecipe(RunicAltarRecipe recipe) {
		super(recipe.getOutput(), recipe.getReagent(), recipe.getMana(),
				recipe.getIngredients().toArray(Ingredient[]::new), recipe.getCatalysts().toArray(Ingredient[]::new));
	}

	@Override
	public boolean matches(RecipeInput inv, @NotNull Level world) {
		boolean matches = super.matches(inv, world);

		if (matches) {
			for (int i = 0; i < inv.size(); i++) {
				ItemStack stack = inv.getItem(i);
				if (stack.isEmpty()) {
					break;
				}

				if (stack.is(Items.NAME_TAG)) {
					String defaultName = Component.translatable(Items.NAME_TAG.getDescriptionId()).getString();
					if (stack.getHoverName().getString().equals(defaultName)) {
						return false;
					}
				}
			}
		}

		return matches;
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull RecipeInput inv, @NotNull HolderLookup.Provider registries) {
		ItemStack stack = getResultItem(registries).copy();
		for (int i = 0; i < inv.size(); i++) {
			ItemStack ingr = inv.getItem(i);
			if (ingr.is(Items.NAME_TAG)) {
				ItemNBTHelper.setString(stack, "SkullOwner", ingr.getHoverName().getString());
				break;
			}
		}
		return stack;
	}

	public static class Serializer implements RecipeSerializer<HeadRecipe> {
		public static final MapCodec<HeadRecipe> CODEC = RunicAltarRecipe.Serializer.CODEC
				.xmap(HeadRecipe::new, Function.identity());
		public static final StreamCodec<RegistryFriendlyByteBuf, HeadRecipe> STREAM_CODEC = RunicAltarRecipe.Serializer.STREAM_CODEC
				.map(HeadRecipe::new, Function.identity());

		@Override
		public MapCodec<HeadRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, HeadRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}

}
