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
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.block.block_entity.SimpleInventoryBlockEntity;
import vazkii.botania.common.block.block_entity.SparkTinkererBlockEntity;
import vazkii.botania.common.item.SparkAugmentItem;

public class SparkTinkererBlock extends BotaniaWaterloggedBlock implements EntityBlock {

	private static final VoxelShape SHAPE = box(0, 0, 0, 16, 3, 16);

	public SparkTinkererBlock(Properties builder) {
		super(builder);
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.POWERED, true));
	}

	@NotNull
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return SHAPE;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(BlockStateProperties.POWERED);
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		boolean power = world.getBestNeighborSignal(pos) > 0;
		boolean powered = state.getValue(BlockStateProperties.POWERED);

		if (power && !powered) {
			((SparkTinkererBlockEntity) world.getBlockEntity(pos)).doSwap();
			world.setBlock(pos, state.setValue(BlockStateProperties.POWERED, true), Block.UPDATE_INVISIBLE);
		} else if (!power && powered) {
			world.setBlock(pos, state.setValue(BlockStateProperties.POWERED, false), Block.UPDATE_INVISIBLE);
		}
	}

	/*OLD
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		SparkTinkererBlockEntity changer = (SparkTinkererBlockEntity) world.getBlockEntity(pos);
		ItemStack pstack = player.getItemInHand(hand);
		ItemStack cstack = changer.getItemHandler().getItem(0);
		if (!cstack.isEmpty()) {
			changer.getItemHandler().setItem(0, ItemStack.EMPTY);
			player.getInventory().placeItemBackInInventory(cstack);
			return InteractionResult.sidedSuccess(world.isClientSide());
		} else if (!pstack.isEmpty() && pstack.getItem() instanceof SparkAugmentItem) {
			changer.getItemHandler().setItem(0, pstack.split(1));
			changer.setChanged();

			return InteractionResult.sidedSuccess(world.isClientSide());
		}

		return InteractionResult.PASS;
	}
	 */

	@Override
	protected ItemInteractionResult useItemOn(ItemStack pstack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		SparkTinkererBlockEntity changer = (SparkTinkererBlockEntity) world.getBlockEntity(pos);
		ItemStack cstack = changer.getItemHandler().getItem(0);
		if (!cstack.isEmpty()) {
			changer.getItemHandler().setItem(0, ItemStack.EMPTY);
			player.getInventory().placeItemBackInInventory(cstack);
			return ItemInteractionResult.sidedSuccess(world.isClientSide());
		} else if (!pstack.isEmpty() && pstack.getItem() instanceof SparkAugmentItem) {
			changer.getItemHandler().setItem(0, pstack.split(1));
			changer.setChanged();

			return ItemInteractionResult.sidedSuccess(world.isClientSide());
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof SimpleInventoryBlockEntity inventory) {
				Containers.dropContents(world, pos, inventory.getItemHandler());
			}
			super.onRemove(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		SparkTinkererBlockEntity changer = (SparkTinkererBlockEntity) world.getBlockEntity(pos);
		ItemStack stack = changer.getItemHandler().getItem(0);
		if (!stack.isEmpty() && stack.getItem() instanceof SparkAugmentItem upgrade) {
			return upgrade.type.ordinal() + 1;
		}
		return 0;
	}

	@NotNull
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new SparkTinkererBlockEntity(pos, state);
	}

}
