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
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.common.block.block_entity.AnimatedTorchBlockEntity;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;

public class AnimatedTorchBlock extends BotaniaWaterloggedBlock implements EntityBlock {

	private static final VoxelShape SHAPE = box(0, 0, 0, 16, 4, 16);

	public AnimatedTorchBlock(Properties builder) {
		super(builder);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player playerIn, BlockHitResult hit) {
		if (playerIn.isSecondaryUseActive()) {
			((AnimatedTorchBlockEntity) worldIn.getBlockEntity(pos)).handRotate();
			return InteractionResult.sidedSuccess(worldIn.isClientSide());
		}

		return InteractionResult.PASS;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		((AnimatedTorchBlockEntity) world.getBlockEntity(pos)).onPlace(entity);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		return getSignal(blockState, blockAccess, pos, side);
	}

	@Override
	public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		AnimatedTorchBlockEntity tile = (AnimatedTorchBlockEntity) blockAccess.getBlockEntity(pos);

		if (tile.rotating || !tile.directionInitialized) {
			return 0;
		}

		if (AnimatedTorchBlockEntity.SIDES[tile.side] == side) {
			return 15;
		}

		return 0;
	}

	@NotNull
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@NotNull
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return SHAPE;
	}

	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new AnimatedTorchBlockEntity(pos, state);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BotaniaBlockEntities.ANIMATED_TORCH, AnimatedTorchBlockEntity::commonTick);
	}

	@Override
	public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
		// Block entity is already gone so best we can do is just notify everyone
		world.blockUpdated(pos, this);
		if (world instanceof ServerLevel level) {
			for (Direction e : AnimatedTorchBlockEntity.SIDES) {
				level.updateNeighborsAtExceptFromFacing(pos.relative(e), state.getBlock(), e.getOpposite());
			}
		}
		super.destroy(world, pos, state);
	}

}
