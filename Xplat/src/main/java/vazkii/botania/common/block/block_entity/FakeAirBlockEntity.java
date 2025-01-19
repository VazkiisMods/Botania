/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.common.block.flower.functional.BubbellBlockEntity;

public class FakeAirBlockEntity extends BotaniaBlockEntity {
	private static final String TAG_FLOWER_X = "flowerX";
	private static final String TAG_FLOWER_Y = "flowerY";
	private static final String TAG_FLOWER_Z = "flowerZ";

	private BlockPos flowerPos = ManaBurst.NO_SOURCE;

	public FakeAirBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.FAKE_AIR, pos, state);
	}

	public void setFlower(BlockEntity tile) {
		flowerPos = tile.getBlockPos();
		setChanged();
	}

	public boolean canStay() {
		return BubbellBlockEntity.isValidBubbell(level, flowerPos);
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt(TAG_FLOWER_X, flowerPos.getX());
		tag.putInt(TAG_FLOWER_Y, flowerPos.getY());
		tag.putInt(TAG_FLOWER_Z, flowerPos.getZ());
	}

	@Override
	public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		flowerPos = new BlockPos(
				tag.getInt(TAG_FLOWER_X),
				tag.getInt(TAG_FLOWER_Y),
				tag.getInt(TAG_FLOWER_Z)
		);
	}

}
