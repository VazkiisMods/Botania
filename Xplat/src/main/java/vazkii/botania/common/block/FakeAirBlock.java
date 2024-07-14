/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.block.block_entity.FakeAirBlockEntity;

public class FakeAirBlock extends AirBlock implements EntityBlock, LiquidBlockContainer {

	public FakeAirBlock(Properties builder) {
		super(builder);
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (shouldRemove(world, pos)) {
			world.scheduleTick(pos, this, 4);
		}
	}

	private boolean shouldRemove(Level world, BlockPos pos) {
		return !(world.getBlockEntity(pos) instanceof FakeAirBlockEntity fakeAir) || !fakeAir.canStay();
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		if (shouldRemove(world, pos)) {
			world.setBlockAndUpdate(pos, rand.nextInt(10) == 0 ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState());
		}
	}

	@NotNull
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new FakeAirBlockEntity(pos, state);
	}

	@Override
	public boolean canPlaceLiquid(Player player, BlockGetter blockGetter, BlockPos pos, BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	public boolean placeLiquid(LevelAccessor levelAccessor, BlockPos pos, BlockState blockState, FluidState fluidState) {
		return false;
	}
}
