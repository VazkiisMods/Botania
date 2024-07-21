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

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;

public class ManaInfusionRecipe implements vazkii.botania.api.recipe.ManaInfusionRecipe {
	private final ItemStack output;
	private final Ingredient input;
	private final int mana;
	private final StateIngredient catalyst;
	private final String group;

	public ManaInfusionRecipe(ItemStack output, Ingredient input, int mana, String group, StateIngredient catalyst) {
		this.output = output;
		this.input = input;
		this.mana = mana;
		this.group = group == null ? "" : group;
		this.catalyst = catalyst == null ? StateIngredients.NONE : catalyst;
	}

	@NotNull
	@Override
	public RecipeSerializer<ManaInfusionRecipe> getSerializer() {
		return BotaniaRecipeTypes.MANA_INFUSION_SERIALIZER;
	}

	@Override
	public boolean matches(ItemStack stack) {
		return input.test(stack);
	}

	@NotNull
	@Override
	public StateIngredient getRecipeCatalyst() {
		return catalyst;
	}

	@Override
	public int getManaToConsume() {
		return mana;
	}

	@NotNull
	@Override
	public ItemStack getResultItem(@NotNull RegistryAccess registries) {
		return output;
	}

	@NotNull
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY, input);
	}

	@NotNull
	@Override
	public String getGroup() {
		return group;
	}

	@NotNull
	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(BotaniaBlocks.manaPool);
	}

	protected Ingredient getInput() {
		return input;
	}

	protected ItemStack getOutput() {
		return output;
	}

	public static class Serializer implements RecipeSerializer<ManaInfusionRecipe> {
		public static final Codec<ManaInfusionRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("output").forGetter(ManaInfusionRecipe::getOutput),
				Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(ManaInfusionRecipe::getInput),
				// Leaving wiggle room for a certain modpack having creative-pool-only recipes
				ExtraCodecs.intRange(1, ManaPoolBlockEntity.MAX_MANA + 1).fieldOf("mana")
						.forGetter(ManaInfusionRecipe::getManaToConsume),
				Codec.STRING.optionalFieldOf("group", "").forGetter(ManaInfusionRecipe::getGroup),
				StateIngredients.TYPED_CODEC.optionalFieldOf("catalyst", StateIngredients.NONE)
						.forGetter(ManaInfusionRecipe::getRecipeCatalyst)
		).apply(instance, ManaInfusionRecipe::new));

		@Override
		public Codec<ManaInfusionRecipe> codec() {
			return CODEC;
		}

		@Override
		public ManaInfusionRecipe fromNetwork(@NotNull FriendlyByteBuf buf) {
			Ingredient input = Ingredient.fromNetwork(buf);
			ItemStack output = buf.readItem();
			int mana = buf.readVarInt();
			StateIngredient catalyst = StateIngredients.NONE;
			if (buf.readBoolean()) {
				catalyst = StateIngredients.fromNetwork(buf);
			}
			String group = buf.readUtf();
			return new ManaInfusionRecipe(output, input, mana, group, catalyst);
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull ManaInfusionRecipe recipe) {
			recipe.getIngredients().get(0).toNetwork(buf);
			buf.writeItem(recipe.output);
			buf.writeVarInt(recipe.getManaToConsume());
			StateIngredient recipeCatalyst = recipe.getRecipeCatalyst();
			boolean hasCatalyst = recipeCatalyst != StateIngredients.NONE;
			buf.writeBoolean(hasCatalyst);
			if (hasCatalyst) {
				StateIngredients.toNetwork(buf, recipeCatalyst);
			}
			buf.writeUtf(recipe.getGroup());
		}

	}
}
