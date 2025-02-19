/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity.red_string;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.block.Bound;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntity;

import java.util.Objects;

public abstract class RedStringBlockEntity extends BotaniaBlockEntity implements Bound {

	private BlockPos binding;

	protected RedStringBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public static void commonTick(Level level, BlockPos pos_, BlockState state, RedStringBlockEntity self) {
		Direction dir = self.getOrientation();
		int range = self.getRange();
		BlockPos currBinding = self.getBinding();
		self.setBinding(null);

		for (int i = 0; i < range; i++) {
			pos_ = pos_.relative(dir);
			if (level.isEmptyBlock(pos_)) {
				continue;
			}

			BlockEntity tile = level.getBlockEntity(pos_);
			if (tile instanceof RedStringBlockEntity) {
				continue;
			}

			if (self.acceptBlock(pos_)) {
				self.setBinding(pos_);
				if (!Objects.equals(currBinding, pos_)) {
					self.onBound(pos_);
				}
				return;
			}
		}
		if (!level.isClientSide && !Objects.equals(currBinding, self.binding)) {
			self.onBound(self.binding);
		}
	}

	public int getRange() {
		return 8;
	}

	public abstract boolean acceptBlock(BlockPos pos);

	public void onBound(@Nullable BlockPos pos) {}

	@Nullable
	@Override
	public BlockPos getBinding() {
		return binding;
	}

	public void setBinding(BlockPos binding) {
		this.binding = binding;
	}

	public Direction getOrientation() {
		return getBlockState().getValue(BlockStateProperties.FACING);
	}

	@Nullable
	public BlockEntity getTileAtBinding() {
		BlockPos binding = getBinding();
		return binding == null || level == null ? null : level.getBlockEntity(binding);
	}

	public BlockState getStateAtBinding() {
		BlockPos binding = getBinding();
		return binding == null ? Blocks.AIR.defaultBlockState() : level.getBlockState(binding);
	}

	public Block getBlockAtBinding() {
		return getStateAtBinding().getBlock();
	}
}
