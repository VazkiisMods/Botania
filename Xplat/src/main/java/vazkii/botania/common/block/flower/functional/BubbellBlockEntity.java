/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.flower.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.api.block_entity.FunctionalFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.block.block_entity.FakeAirBlockEntity;

public class BubbellBlockEntity extends FunctionalFlowerBlockEntity {
	private static final int RANGE = 12;
	private static final int RANGE_MINI = 6;
	private static final int COST_PER_TICK = 4;
	private static final String TAG_RANGE = "range";

	int range = 2;

	protected BubbellBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public BubbellBlockEntity(BlockPos pos, BlockState state) {
		this(BotaniaFlowerBlocks.BUBBELL, pos, state);
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		if (getLevel().isClientSide) {
			return;
		}

		if (ticksExisted % 200 == 0) {
			sync();
		}

		if (getMana() > COST_PER_TICK) {
			addMana(-COST_PER_TICK);

			if (ticksExisted % 10 == 0 && range < getRange()) {
				range++;
			}

			for (BlockPos pos : BlockPos.betweenClosed(getEffectivePos().offset(-range, -range, -range), getEffectivePos().offset(range, range, range))) {
				if (getEffectivePos().distSqr(pos) < range * range) {
					BlockState state = getLevel().getBlockState(pos);
					if (state.is(Blocks.WATER)) {
						getLevel().setBlock(pos, BotaniaBlocks.fakeAir.defaultBlockState(), Block.UPDATE_CLIENTS);
						FakeAirBlockEntity air = (FakeAirBlockEntity) getLevel().getBlockEntity(pos);
						air.setFlower(this);
					}
				}
			}
		}
	}

	public static boolean isValidBubbell(Level world, BlockPos pos) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile instanceof BubbellBlockEntity bubbell) {
			return bubbell.getMana() > COST_PER_TICK;
		}

		return false;
	}

	@Override
	public void writeToPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.writeToPacketNBT(cmp, registries);
		cmp.putInt(TAG_RANGE, range);
	}

	@Override
	public void readFromPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.readFromPacketNBT(cmp, registries);
		range = cmp.getInt(TAG_RANGE);
	}

	@Override
	public int getMaxMana() {
		return 2000;
	}

	@Override
	public int getColor() {
		return 0x0DCF89;
	}

	public int getRange() {
		return RANGE;
	}

	@Override
	public RadiusDescriptor getRadius() {
		return new RadiusDescriptor.Circle(getEffectivePos(), range);
	}

	public static class Mini extends BubbellBlockEntity {
		public Mini(BlockPos pos, BlockState state) {
			super(BotaniaFlowerBlocks.BUBBELL_CHIBI, pos, state);
		}

		@Override
		public int getRange() {
			return RANGE_MINI;
		}
	}

}
