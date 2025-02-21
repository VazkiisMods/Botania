/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.mana;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.block.BotaniaWaterloggedBlock;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.RunicAltarBlockEntity;
import vazkii.botania.common.block.block_entity.SimpleInventoryBlockEntity;
import vazkii.botania.common.helper.InventoryHelper;

public class RunicAltarBlock extends BotaniaWaterloggedBlock implements EntityBlock {

	private static final VoxelShape TOP = Block.box(0, 6, 0, 16, 12, 16);
	private static final VoxelShape BOTTOM = Block.box(2, 0, 2, 14, 6, 14);
	private static final VoxelShape SHAPE = Shapes.join(TOP, BOTTOM, BooleanOp.OR);

	public RunicAltarBlock(Properties builder) {
		super(builder);
	}

	@NotNull
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return SHAPE;
	}

	/*OLD
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!(world.getBlockEntity(pos) instanceof RunicAltarBlockEntity altar)) {
			return InteractionResult.PASS;
		}
		ItemStack stack = player.getItemInHand(hand);
		boolean mainHandEmpty = player.getMainHandItem().isEmpty();
	
		if (altar.canAddLastRecipe() && mainHandEmpty) {
			return altar.trySetLastRecipe(player);
		} else if (!altar.isEmpty() && altar.manaToGet == 0 && mainHandEmpty) {
			InventoryHelper.withdrawFromInventory(altar, player);
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(altar);
			return InteractionResult.sidedSuccess(world.isClientSide());
		} else if (!stack.isEmpty()) {
			boolean result = altar.addItem(player, stack, hand);
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(altar);
			if (result) {
				return InteractionResult.sidedSuccess(world.isClientSide());
			}
		}
	
		return InteractionResult.PASS;
	}
	 */

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!(world.getBlockEntity(pos) instanceof RunicAltarBlockEntity altar)) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		boolean mainHandEmpty = player.getMainHandItem().isEmpty();

		if (altar.canAddLastRecipe() && mainHandEmpty) {
			return altar.trySetLastRecipe(player);
		} else if (!altar.isEmpty() && altar.manaToGet == 0 && mainHandEmpty) {
			InventoryHelper.withdrawFromInventory(altar, player);
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(altar);
			return ItemInteractionResult.sidedSuccess(world.isClientSide());
		} else if (!stack.isEmpty()) {
			boolean result = altar.addItem(player, stack, hand);
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(altar);
			if (result) {
				return ItemInteractionResult.sidedSuccess(world.isClientSide());
			}
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

	@NotNull
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new RunicAltarBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide) {
			return createTickerHelper(type, BotaniaBlockEntities.RUNE_ALTAR, RunicAltarBlockEntity::clientTick);
		} else {
			return createTickerHelper(type, BotaniaBlockEntities.RUNE_ALTAR, RunicAltarBlockEntity::serverTick);
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		RunicAltarBlockEntity altar = (RunicAltarBlockEntity) world.getBlockEntity(pos);
		return altar.signal;
	}

}
