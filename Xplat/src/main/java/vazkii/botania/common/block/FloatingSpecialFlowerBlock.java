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
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.block_entity.FunctionalFlowerBlockEntity;
import vazkii.botania.api.block_entity.SpecialFlowerBlockEntity;
import vazkii.botania.common.block.decor.FloatingFlowerBlock;

import java.util.function.Supplier;

public class FloatingSpecialFlowerBlock extends FloatingFlowerBlock {
	private final Supplier<BlockEntityType<? extends SpecialFlowerBlockEntity>> blockEntityType;
	private final boolean hasComparatorOutput;

	public FloatingSpecialFlowerBlock(Properties props,
			Supplier<BlockEntityType<? extends SpecialFlowerBlockEntity>> blockEntityType) {
		this(props, blockEntityType, false);
	}

	public FloatingSpecialFlowerBlock(Properties props,
			Supplier<BlockEntityType<? extends SpecialFlowerBlockEntity>> blockEntityType,
			boolean hasComparatorOutput) {
		super(DyeColor.WHITE, props);
		this.blockEntityType = blockEntityType;
		this.hasComparatorOutput = hasComparatorOutput;
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
		redstoneParticlesIfPowered(state, world, pos, rand);
	}

	public static void redstoneParticlesIfPowered(BlockState state, Level world, BlockPos pos, RandomSource rand) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof FunctionalFlowerBlockEntity flower && rand.nextBoolean()) {
			if (flower.acceptsRedstone() && flower.redstoneSignal > 0) {
				VoxelShape shape = state.getShape(world, pos);
				if (!shape.isEmpty()) {
					AABB localBox = shape.bounds();
					double x = pos.getX() + localBox.minX + rand.nextDouble() * (localBox.maxX - localBox.minX);
					double y = pos.getY() + localBox.minY + rand.nextDouble() * (localBox.maxY - localBox.minY);
					double z = pos.getZ() + localBox.minZ + rand.nextDouble() * (localBox.maxZ - localBox.minZ);
					world.addParticle(DustParticleOptions.REDSTONE, x, y, z, 0, 0, 0);
				}
			}
		}
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		((SpecialFlowerBlockEntity) world.getBlockEntity(pos)).setPlacedBy(world, pos, state, entity, stack);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (hasComparatorOutput && !newState.hasAnalogOutputSignal()) {
			level.updateNeighbourForOutputSignal(pos, newState.getBlock());
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@NotNull
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		SpecialFlowerBlockEntity te = blockEntityType.get().create(pos, state);
		te.setFloating(true);
		return te;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
		return createTickerHelper(type, blockEntityType.get(), SpecialFlowerBlockEntity::commonTick);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState bs) {
		return hasComparatorOutput;
	}

	@Override
	public int getAnalogOutputSignal(BlockState bs, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof SpecialFlowerBlockEntity flower) {
			return flower.getComparatorSignal();
		}
		return 0;
	}
}
