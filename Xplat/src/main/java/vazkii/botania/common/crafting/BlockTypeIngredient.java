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

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.api.recipe.StateIngredientType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public record BlockTypeIngredient(Block block) implements StateIngredient {

	@Override
	public boolean test(BlockState blockState) {
		return blockState.is(block);
	}

	@Override
	public BlockState pick(RandomSource random) {
		return block.defaultBlockState();
	}

	@Override
	public StateIngredientType<BlockTypeIngredient> getType() {
		return StateIngredients.BLOCK_TYPE;
	}

	@Override
	public List<ItemStack> getDisplayedStacks() {
		return block == Blocks.AIR ? Collections.emptyList() : Collections.singletonList(new ItemStack(block.asItem()));
	}

	@Override
	public List<BlockState> getDisplayed() {
		return Collections.singletonList(block.defaultBlockState());
	}

	@Override
	public Stream<BlockState> streamBlockStates() {
		return Stream.of(block.defaultBlockState());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		return block.equals(((BlockTypeIngredient) o).block);
	}

	@Override
	public String toString() {
		return "BlockTypeIngredient{" + block + "}";
	}

	public static class Type implements StateIngredientType<BlockTypeIngredient> {
		public static final Codec<BlockTypeIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourceLocation.CODEC.fieldOf("block").forGetter(bi -> BuiltInRegistries.BLOCK.getKey(bi.block()))
		).apply(instance, id -> new BlockTypeIngredient(BuiltInRegistries.BLOCK.get(id))));

		@Override
		public Codec<BlockTypeIngredient> codec() {
			return CODEC;
		}

		@Override
		public BlockTypeIngredient fromNetwork(FriendlyByteBuf buffer) {
			ResourceLocation blockId = buffer.readResourceLocation();
			Block block = BuiltInRegistries.BLOCK.get(blockId);
			return new BlockTypeIngredient(block);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, BlockTypeIngredient ingredient) {
			buffer.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(ingredient.block()));
		}
	}
}
