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
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.HoveringHourglassBlockEntity;
import vazkii.botania.common.block.block_entity.SimpleInventoryBlockEntity;
import vazkii.botania.common.item.WandOfTheForestItem;

public class HoveringHourglassBlock extends BotaniaWaterloggedBlock implements EntityBlock {

	private static final VoxelShape SHAPE = box(4, 0, 4, 12, 18.4, 12);

	protected HoveringHourglassBlock(Properties builder) {
		super(builder);
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.POWERED, false));
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

	/* OLD
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		HoveringHourglassBlockEntity hourglass = (HoveringHourglassBlockEntity) world.getBlockEntity(pos);
		ItemStack hgStack = hourglass.getItemHandler().getItem(0);
		ItemStack stack = player.getItemInHand(hand);
		if (!stack.isEmpty() && stack.getItem() instanceof WandOfTheForestItem) {
			return InteractionResult.PASS;
		}

		if (hourglass.lock) {
			if (!player.level().isClientSide && hand == InteractionHand.OFF_HAND) {
				player.sendSystemMessage(Component.translatable("botaniamisc.hourglassLock"));
			}
			return InteractionResult.FAIL;
		}

		if (hgStack.isEmpty() && HoveringHourglassBlockEntity.getStackItemTime(stack) > 0) {
			hourglass.getItemHandler().setItem(0, stack.copy());
			stack.setCount(0);
			return InteractionResult.sidedSuccess(world.isClientSide());
		} else if (!hgStack.isEmpty()) {
			player.getInventory().placeItemBackInInventory(hgStack);
			hourglass.getItemHandler().setItem(0, ItemStack.EMPTY);
			return InteractionResult.sidedSuccess(world.isClientSide());
		}

		return InteractionResult.PASS;
	}

	 */

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		HoveringHourglassBlockEntity hourglass = (HoveringHourglassBlockEntity) world.getBlockEntity(pos);
		ItemStack hgStack = hourglass.getItemHandler().getItem(0);
		if (!stack.isEmpty() && stack.getItem() instanceof WandOfTheForestItem) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		if (hourglass.lock) {
			if (!player.level().isClientSide && hand == InteractionHand.OFF_HAND) {
				player.sendSystemMessage(Component.translatable("botaniamisc.hourglassLock"));
			}
			return ItemInteractionResult.FAIL;
		}

		if (hgStack.isEmpty() && HoveringHourglassBlockEntity.getStackItemTime(stack) > 0) {
			hourglass.getItemHandler().setItem(0, stack.copy());
			stack.setCount(0);
			return ItemInteractionResult.sidedSuccess(world.isClientSide());
		} else if (!hgStack.isEmpty()) {
			player.getInventory().placeItemBackInInventory(hgStack);
			hourglass.getItemHandler().setItem(0, ItemStack.EMPTY);
			return ItemInteractionResult.sidedSuccess(world.isClientSide());
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		return state.getValue(BlockStateProperties.POWERED) ? 15 : 0;
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		if (state.getValue(BlockStateProperties.POWERED)) {
			world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, false));
		}
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

	@NotNull
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@NotNull
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new HoveringHourglassBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BotaniaBlockEntities.HOURGLASS, HoveringHourglassBlockEntity::commonTick);
	}
}
