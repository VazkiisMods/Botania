/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.corporea;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.common.block.BotaniaWaterloggedBlock;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.corporea.BaseCorporeaBlockEntity;
import vazkii.botania.common.block.block_entity.corporea.CorporeaCrystalCubeBlockEntity;
import vazkii.botania.common.item.WandOfTheForestItem;

public class CorporeaCrystalCubeBlock extends BotaniaWaterloggedBlock implements EntityBlock {

	private static final VoxelShape SHAPE = box(3.0, 0, 3.0, 13.0, 16, 13.0);

	public CorporeaCrystalCubeBlock(BlockBehaviour.Properties builder) {
		super(builder);
	}

	@Override
	public void attack(BlockState state, Level world, BlockPos pos, Player player) {
		if (!world.isClientSide) {
			CorporeaCrystalCubeBlockEntity cube = (CorporeaCrystalCubeBlockEntity) world.getBlockEntity(pos);
			cube.doRequest(player);
		}
	}

	@NotNull
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return SHAPE;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (stack.getItem() instanceof WandOfTheForestItem && player.isSecondaryUseActive()) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		CorporeaCrystalCubeBlockEntity cube = (CorporeaCrystalCubeBlockEntity) level.getBlockEntity(pos);
		if (cube.locked) {
			if (!level.isClientSide) {
				player.displayClientMessage(Component.translatable("botaniamisc.crystalCubeLocked"), false);
			}
		} else {
			cube.setRequestTarget(stack);
		}
		return ItemInteractionResult.sidedSuccess(level.isClientSide());
	}

	@NotNull
	@Override
	public BaseCorporeaBlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new CorporeaCrystalCubeBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
		if (!level.isClientSide) {
			return createTickerHelper(type, BotaniaBlockEntities.CORPOREA_CRYSTAL_CUBE, CorporeaCrystalCubeBlockEntity::serverTick);
		}
		return null;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		return ((CorporeaCrystalCubeBlockEntity) world.getBlockEntity(pos)).getComparatorValue();
	}
}
