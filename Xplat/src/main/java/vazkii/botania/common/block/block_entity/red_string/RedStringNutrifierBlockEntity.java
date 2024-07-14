/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity.red_string;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;

public class RedStringNutrifierBlockEntity extends RedStringBlockEntity {
	public RedStringNutrifierBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.RED_STRING_FERTILIZER, pos, state);
	}

	public boolean canGrow(LevelReader world) {
		BlockPos binding = getBinding();
		Block block = getBlockAtBinding();

		return block instanceof BonemealableBlock mealable && mealable.isValidBonemealTarget(world, binding, world.getBlockState(binding));
	}

	public boolean canUseBonemeal(Level world, RandomSource rand) {
		BlockPos binding = getBinding();
		Block block = getBlockAtBinding();
		return block instanceof BonemealableBlock mealable && mealable.isBonemealSuccess(world, rand, binding, world.getBlockState(binding));
	}

	public void grow(ServerLevel world, RandomSource rand) {
		BlockPos binding = getBinding();
		Block block = getBlockAtBinding();
		if (block instanceof BonemealableBlock mealable) {
			mealable.performBonemeal(world, rand, binding, world.getBlockState(binding));
		}
	}

	@Override
	public boolean acceptBlock(BlockPos pos) {
		return level.getBlockState(pos).getBlock() instanceof BonemealableBlock;
	}

}
