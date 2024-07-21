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
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

public class NbtOutputRecipe<C extends Container> implements Recipe<C> {
	public static final RecipeSerializer<NbtOutputRecipe<?>> SERIALIZER = new NbtOutputRecipe.Serializer();

	private final Recipe<C> recipe;
	private final CompoundTag nbt;

	public NbtOutputRecipe(Recipe<C> recipe, CompoundTag nbt) {
		this.recipe = recipe;
		this.nbt = nbt;
	}

	@Override
	public boolean matches(C container, Level level) {
		return recipe.matches(container, level);
	}

	@Override
	public ItemStack assemble(C container, RegistryAccess registryAccess) {
		ItemStack result = recipe.assemble(container, registryAccess);
		result.setTag(nbt);
		return result;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return recipe.canCraftInDimensions(width, height);
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return recipe.getResultItem(registryAccess);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return recipe.getType();
	}

	private static class Serializer implements RecipeSerializer<NbtOutputRecipe<?>> {
		public static final Codec<NbtOutputRecipe<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Recipe.CODEC.fieldOf("recipe").forGetter(r -> r.recipe),
				CompoundTag.CODEC.fieldOf("nbt").forGetter(r -> r.nbt)
		).apply(instance, NbtOutputRecipe::new));

		@Override
		public Codec<NbtOutputRecipe<?>> codec() {
			return CODEC;
		}

		@NotNull
		@Override
		public NbtOutputRecipe<?> fromNetwork(@NotNull FriendlyByteBuf buffer) {
			var recipe = RecipeUtils.recipeFromNetwork(buffer);
			var nbt = buffer.readNbt();
			return new NbtOutputRecipe<>(recipe, nbt);
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull NbtOutputRecipe<?> recipe) {
			RecipeUtils.recipeToNetwork(buffer, recipe.recipe);
			buffer.writeNbt(recipe.nbt);
		}
	}
}
