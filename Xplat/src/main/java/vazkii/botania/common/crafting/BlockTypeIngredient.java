/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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
		public static final MapCodec<BlockTypeIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(BlockTypeIngredient::block)
		).apply(instance, BlockTypeIngredient::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, BlockTypeIngredient> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.registry(Registries.BLOCK), BlockTypeIngredient::block,
				BlockTypeIngredient::new
		);

		@Override
		public MapCodec<BlockTypeIngredient> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BlockTypeIngredient> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
