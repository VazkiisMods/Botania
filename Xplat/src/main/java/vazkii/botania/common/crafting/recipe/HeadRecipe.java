/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting.recipe;

import com.mojang.serialization.Codec;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.crafting.BotaniaRecipeTypes;
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
	public boolean matches(Container inv, @NotNull Level world) {
		boolean matches = super.matches(inv, world);

		if (matches) {
			for (int i = 0; i < inv.getContainerSize(); i++) {
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
	public ItemStack assemble(@NotNull Container inv, @NotNull RegistryAccess registries) {
		ItemStack stack = getResultItem(registries).copy();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack ingr = inv.getItem(i);
			if (ingr.is(Items.NAME_TAG)) {
				ItemNBTHelper.setString(stack, "SkullOwner", ingr.getHoverName().getString());
				break;
			}
		}
		return stack;
	}

	public static class Serializer implements RecipeSerializer<HeadRecipe> {
		public static final Codec<HeadRecipe> CODEC = RunicAltarRecipe.Serializer.CODEC
				.xmap(HeadRecipe::new, Function.identity());

		@Override
		public Codec<HeadRecipe> codec() {
			return CODEC;
		}

		@Override
		public HeadRecipe fromNetwork(@NotNull FriendlyByteBuf buf) {
			return new HeadRecipe(BotaniaRecipeTypes.RUNE_SERIALIZER.fromNetwork(buf));
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull HeadRecipe recipe) {
			BotaniaRecipeTypes.RUNE_SERIALIZER.toNetwork(buf, recipe);
		}
	}

}
