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
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.state.enums.LuminizerVariant;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.LuminizerBlockEntity;
import vazkii.botania.common.item.BotaniaItems;

public class LuminizerBlock extends BotaniaWaterloggedBlock implements EntityBlock {

	private static final VoxelShape SHAPE = box(5, 5, 5, 11, 11, 11);
	public final LuminizerVariant variant;

	protected LuminizerBlock(LuminizerVariant variant, Properties builder) {
		super(builder);
		this.variant = variant;
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
		ItemStack stack = player.getItemInHand(hand);
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof LuminizerBlockEntity relay) {
			if (stack.is(BotaniaItems.phantomInk) && !relay.isNoParticle()) {
				if (!world.isClientSide) {
					stack.shrink(1);
					relay.setNoParticle();
					world.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
					VanillaPacketDispatcher.dispatchTEToNearbyPlayers(relay);
				}
				return InteractionResult.sidedSuccess(world.isClientSide());
			} else if (!stack.is(Items.ENDER_PEARL)) {
				relay.mountEntity(player);
				return InteractionResult.sidedSuccess(world.isClientSide());
			}
		}

		return InteractionResult.PASS;
	}
	 */

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof LuminizerBlockEntity relay) {
			if (stack.is(BotaniaItems.phantomInk) && !relay.isNoParticle()) {
				if (!world.isClientSide) {
					stack.shrink(1);
					relay.setNoParticle();
					world.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
					VanillaPacketDispatcher.dispatchTEToNearbyPlayers(relay);
				}
				return ItemInteractionResult.sidedSuccess(world.isClientSide());
			} else if (!stack.is(Items.ENDER_PEARL)) {
				relay.mountEntity(player);
				return ItemInteractionResult.sidedSuccess(world.isClientSide());
			}
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (!worldIn.isClientSide && variant == LuminizerVariant.TOGGLE) {
			if (state.getValue(BlockStateProperties.POWERED) && !worldIn.hasNeighborSignal(pos)) {
				worldIn.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, false));
			} else if (!state.getValue(BlockStateProperties.POWERED) && worldIn.hasNeighborSignal(pos)) {
				worldIn.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, true));
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, false));
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return variant == LuminizerVariant.DETECTOR;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction s) {
		return variant == LuminizerVariant.DETECTOR
				&& state.getValue(BlockStateProperties.POWERED) ? 15 : 0;
	}

	@NotNull
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@NotNull
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new LuminizerBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BotaniaBlockEntities.LIGHT_RELAY, level.isClientSide ? LuminizerBlockEntity::clientTick : LuminizerBlockEntity::serverTick);
	}

}
