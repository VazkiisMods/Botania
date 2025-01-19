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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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

import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.TeruTeruBozuBlockEntity;
import vazkii.botania.common.helper.EntityHelper;

public class TeruTeruBozuBlock extends BotaniaWaterloggedBlock implements EntityBlock {

	private static final VoxelShape SHAPE = box(4, 0.16, 4, 12, 15.84, 12);

	public TeruTeruBozuBlock(Properties builder) {
		super(builder);
	}

	@NotNull
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return SHAPE;
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity e) {
		if (!world.isClientSide && e instanceof ItemEntity item) {
			ItemStack stack = item.getItem();
			if (isSunflower(stack) && removeRain(world) || isBlueOrchid(stack) && startRain(world)) {
				EntityHelper.shrinkItem(item);
			}
		}
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (isSunflower(stack) && removeRain(level) || isBlueOrchid(stack) && startRain(level)) {
			if (!player.getAbilities().instabuild) {
				stack.shrink(1);
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide());
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	private boolean isSunflower(ItemStack stack) {
		return stack.is(Blocks.SUNFLOWER.asItem());
	}

	private boolean isBlueOrchid(ItemStack stack) {
		return stack.is(Blocks.BLUE_ORCHID.asItem());
	}

	private boolean removeRain(Level world) {
		if (world.isRaining()) {
			world.getLevelData().setRaining(false);
			TeruTeruBozuBlockEntity.resetRainTime(world);
			return true;
		}
		return false;
	}

	private boolean startRain(Level world) {
		if (!world.isRaining()) {
			if (world.random.nextInt(10) == 0) {
				world.getLevelData().setRaining(true);
				TeruTeruBozuBlockEntity.resetRainTime(world);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		return world.isRaining() ? 15 : 0;
	}

	@NotNull
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@NotNull
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new TeruTeruBozuBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (!level.isClientSide) {
			return createTickerHelper(type, BotaniaBlockEntities.TERU_TERU_BOZU, TeruTeruBozuBlockEntity::serverTick);
		}
		return null;
	}
}
