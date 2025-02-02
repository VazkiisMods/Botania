/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.crafting.BotaniaRecipeTypes;
import vazkii.botania.common.crafting.RunicAltarRecipe;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.data.UuidNameProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HeadRecipe extends RunicAltarRecipe {

	public HeadRecipe(ResourceLocation id, ItemStack output, int mana, Ingredient... inputs) {
		super(id, output, mana, inputs);
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
				String nameTagText = ingr.getHoverName().getString();
				if (isUUID(nameTagText)) {
					// UUID
					String PlayerName = UuidNameProvider.getPlayerNameFromUUID(nameTagText.replace("-", ""));
					ItemNBTHelper.setString(stack, "SkullOwner", PlayerName);
				} else {
					// Player Name
					ItemNBTHelper.setString(stack, "SkullOwner", ingr.getHoverName().getString());
				}
				break;
			}
		}
		return stack;
	}

	public static class Serializer implements RecipeSerializer<HeadRecipe> {

		@NotNull
		@Override
		public HeadRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
			ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
			int mana = GsonHelper.getAsInt(json, "mana");
			JsonArray ingrs = GsonHelper.getAsJsonArray(json, "ingredients");
			List<Ingredient> inputs = new ArrayList<>();
			for (JsonElement e : ingrs) {
				inputs.add(Ingredient.fromJson(e));
			}
			return new HeadRecipe(id, output, mana, inputs.toArray(new Ingredient[0]));
		}

		@Override
		public HeadRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
			Ingredient[] inputs = new Ingredient[buf.readVarInt()];
			for (int i = 0; i < inputs.length; i++) {
				inputs[i] = Ingredient.fromNetwork(buf);
			}
			ItemStack output = buf.readItem();
			int mana = buf.readVarInt();
			return new HeadRecipe(id, output, mana, inputs);
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull HeadRecipe recipe) {
			BotaniaRecipeTypes.RUNE_SERIALIZER.toNetwork(buf, recipe);
		}
	}

	private boolean isUUID(String input) {
		String uuidRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$|^[0-9a-fA-F]{32}$";
		Pattern pattern = Pattern.compile(uuidRegex);
		return pattern.matcher(input).matches();
	}

}
