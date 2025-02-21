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
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.IncensePlateBlockEntity;

public class IncensePlateBlock extends BotaniaWaterloggedBlock implements EntityBlock {

	private static final VoxelShape X_SHAPE = box(6, 0, 2, 10, 1, 14);
	private static final VoxelShape Z_SHAPE = box(2, 0, 6, 14, 1, 10);

	protected IncensePlateBlock(Properties builder) {
		super(builder);
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}

	/*OLD
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		IncensePlateBlockEntity plate = (IncensePlateBlockEntity) world.getBlockEntity(pos);
		ItemStack plateStack = plate.getItemHandler().getItem(0);
		ItemStack stack = player.getItemInHand(hand);
		boolean did = false;
	
		if (plateStack.isEmpty() && plate.acceptsItem(stack)) {
			plate.getItemHandler().setItem(0, stack.copy());
			world.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
			stack.shrink(1);
			did = true;
		} else if (!plateStack.isEmpty() && !plate.burning) {
			if (!stack.isEmpty() && stack.is(Items.FLINT_AND_STEEL)) {
				plate.ignite();
				stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
			} else {
				player.getInventory().placeItemBackInInventory(plateStack);
				plate.getItemHandler().setItem(0, ItemStack.EMPTY);
				world.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
			}
			did = true;
		}
	
		if (did) {
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(plate);
		}
	
		return did
				? InteractionResult.sidedSuccess(world.isClientSide())
				: InteractionResult.PASS;
	}
	 */

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		IncensePlateBlockEntity plate = (IncensePlateBlockEntity) world.getBlockEntity(pos);
		ItemStack plateStack = plate.getItemHandler().getItem(0);

		boolean did = false;

		if (plateStack.isEmpty() && plate.acceptsItem(stack)) {
			plate.getItemHandler().setItem(0, stack.copy());
			world.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
			stack.shrink(1);
			did = true;
		} else if (!plateStack.isEmpty() && !plate.burning) {
			if (!stack.isEmpty() && stack.is(Items.FLINT_AND_STEEL)) {
				plate.ignite();
				stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
			} else {
				player.getInventory().placeItemBackInInventory(plateStack);
				plate.getItemHandler().setItem(0, ItemStack.EMPTY);
				world.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
			}
			did = true;
		}

		if (did) {
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(plate);
		}

		return did
				? ItemInteractionResult.sidedSuccess(world.isClientSide())
				: ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
	}

	@NotNull
	@Override
	public BlockState mirror(@NotNull BlockState state, Mirror mirror) {
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, mirror.mirror(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}

	@NotNull
	@Override
	public BlockState rotate(@NotNull BlockState state, Rotation rot) {
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rot.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		return ((IncensePlateBlockEntity) world.getBlockEntity(pos)).comparatorOutput;
	}

	@NotNull
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		if (state.getValue(BlockStateProperties.HORIZONTAL_FACING).getAxis() == Direction.Axis.X) {
			return X_SHAPE;
		} else {
			return Z_SHAPE;
		}
	}

	@NotNull
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new IncensePlateBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BotaniaBlockEntities.INCENSE_PLATE, IncensePlateBlockEntity::commonTick);
	}

	@Override
	public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity block = world.getBlockEntity(pos);
			if (block instanceof IncensePlateBlockEntity plate && !plate.burning) {
				Containers.dropContents(world, pos, plate.getItemHandler());
			}
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public void onProjectileHit(@NotNull Level level, @NotNull BlockState blockState,
			@NotNull BlockHitResult hit, @NotNull Projectile projectile) {
		if (!level.isClientSide && projectile.mayInteract(level, hit.getBlockPos())
				&& projectile.isOnFire()) {
			if (level.getBlockEntity(hit.getBlockPos()) instanceof IncensePlateBlockEntity plate) {
				plate.ignite();
				VanillaPacketDispatcher.dispatchTEToNearbyPlayers(plate);
			}
		}
	}
}
