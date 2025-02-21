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
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.state.BotaniaStateProperties;
import vazkii.botania.common.block.BotaniaBlock;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.ManaEnchanterBlockEntity;
import vazkii.botania.common.item.WandOfTheForestItem;

public class ManaEnchanterBlock extends BotaniaBlock implements EntityBlock {

	public ManaEnchanterBlock(Properties builder) {
		super(builder);
		registerDefaultState(defaultBlockState().setValue(BotaniaStateProperties.ENCHANTER_DIRECTION, Direction.Axis.X));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BotaniaStateProperties.ENCHANTER_DIRECTION);
	}

	@NotNull
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new ManaEnchanterBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BotaniaBlockEntities.ENCHANTER, ManaEnchanterBlockEntity::commonTick);
	}

	/*OLD
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ManaEnchanterBlockEntity enchanter = (ManaEnchanterBlockEntity) world.getBlockEntity(pos);
		ItemStack stack = player.getItemInHand(hand);
		if (!stack.isEmpty() && stack.getItem() instanceof WandOfTheForestItem) {
			return InteractionResult.PASS;
		}
	
		boolean stackEnchantable = !stack.isEmpty()
				&& !stack.is(Items.BOOK)
				&& stack.isEnchantable()
				&& stack.getCount() == 1;
	
		if (enchanter.itemToEnchant.isEmpty()) {
			if (stackEnchantable) {
				enchanter.itemToEnchant = stack.copy();
				player.setItemInHand(hand, ItemStack.EMPTY);
				enchanter.sync();
			} else {
				return InteractionResult.PASS;
			}
		} else if (enchanter.stage == ManaEnchanterBlockEntity.State.IDLE) {
			player.getInventory().placeItemBackInInventory(enchanter.itemToEnchant.copy());
			enchanter.itemToEnchant = ItemStack.EMPTY;
			enchanter.sync();
		}
	
		return InteractionResult.sidedSuccess(world.isClientSide());
	}
	 */

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		ManaEnchanterBlockEntity enchanter = (ManaEnchanterBlockEntity) world.getBlockEntity(pos);

		if (!stack.isEmpty() && stack.getItem() instanceof WandOfTheForestItem) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		boolean stackEnchantable = !stack.isEmpty()
				&& !stack.is(Items.BOOK)
				&& stack.isEnchantable()
				&& stack.getCount() == 1;

		if (enchanter.itemToEnchant.isEmpty()) {
			if (stackEnchantable) {
				enchanter.itemToEnchant = stack.copy();
				player.setItemInHand(hand, ItemStack.EMPTY);
				enchanter.sync();
			} else {
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			}
		} else if (enchanter.stage == ManaEnchanterBlockEntity.State.IDLE) {
			player.getInventory().placeItemBackInInventory(enchanter.itemToEnchant.copy());
			enchanter.itemToEnchant = ItemStack.EMPTY;
			enchanter.sync();
		}

		return ItemInteractionResult.sidedSuccess(world.isClientSide());
	}

	@Override
	public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity tile = world.getBlockEntity(pos);

			if (tile instanceof ManaEnchanterBlockEntity enchanter) {

				if (!enchanter.itemToEnchant.isEmpty()) {
					Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), enchanter.itemToEnchant);
				}
			}

			super.onRemove(state, world, pos, newState, isMoving);
		}
	}
}
