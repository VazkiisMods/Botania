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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.api.block.Bound;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.flower.generating.DandelifeonBlockEntity;
import vazkii.botania.common.block.flower.generating.DandelifeonBlockEntity.Cell;

public class CellularBlockEntity extends BotaniaBlockEntity {
	private static final String TAG_GENERATION = "generation";
	private static final String TAG_TICKED = "ticked";
	private static final String TAG_FLOWER_X = "flowerX";
	private static final String TAG_FLOWER_Y = "flowerY";
	private static final String TAG_FLOWER_Z = "flowerZ";
	private static final String TAG_VALID_X = "validX";
	private static final String TAG_VALID_Y = "validY";
	private static final String TAG_VALID_Z = "validZ";

	private int generation;
	private int nextGeneration;
	private boolean ticked;
	private BlockPos flowerCoords = Bound.UNBOUND_POS;
	private BlockPos validCoords = Bound.UNBOUND_POS;

	public CellularBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.CELL_BLOCK, pos, state);
	}

	public void setGeneration(int gen) {
		generation = gen;
	}

	public void setNextGeneration(DandelifeonBlockEntity flower, int gen) {
		nextGeneration = gen;
		getLevel().scheduleTick(getBlockPos(), BotaniaBlocks.cellBlock, 1);
		if (!ticked) {
			claim(flower);
			ticked = true;
		} else if (!validCoords.equals(getBlockPos()) || !flowerCoords.equals(flower.getEffectivePos())) {
			level.removeBlock(worldPosition, false);
		}
	}

	public void claim(DandelifeonBlockEntity flower) {
		if (!ticked) {
			flowerCoords = flower.getEffectivePos();
			validCoords = getBlockPos();
		}
	}

	public void update(Level level) {
		if (nextGeneration == Cell.DEAD) {
			level.removeBlock(getBlockPos(), false);
		}
		generation = nextGeneration;
	}

	public boolean hasActiveParent(DandelifeonBlockEntity dandie) {
		return flowerCoords != null && dandie.getLevel().getBlockEntity(flowerCoords) instanceof DandelifeonBlockEntity parent && dandie.getLevel().hasNeighborSignal(flowerCoords) && (!dandie.overgrowthBoost || parent.isOnSpecialSoil());
	}

	public int getGeneration() {
		return generation;
	}

	@Override
	public void writePacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		cmp.putInt(TAG_GENERATION, generation);
		cmp.putBoolean(TAG_TICKED, ticked);
		if (ticked) {
			cmp.putInt(TAG_FLOWER_X, flowerCoords.getX());
			cmp.putInt(TAG_FLOWER_Y, flowerCoords.getY());
			cmp.putInt(TAG_FLOWER_Z, flowerCoords.getZ());
			cmp.putInt(TAG_VALID_X, validCoords.getX());
			cmp.putInt(TAG_VALID_Y, validCoords.getY());
			cmp.putInt(TAG_VALID_Z, validCoords.getZ());
		}
	}

	@Override
	public void readPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		generation = cmp.getInt(TAG_GENERATION);
		ticked = cmp.getBoolean(TAG_TICKED);
		if (ticked) {
			flowerCoords = new BlockPos(
					cmp.getInt(TAG_FLOWER_X),
					cmp.getInt(TAG_FLOWER_Y),
					cmp.getInt(TAG_FLOWER_Z)
			);
			validCoords = new BlockPos(
					cmp.getInt(TAG_VALID_X),
					cmp.getInt(TAG_VALID_Y),
					cmp.getInt(TAG_VALID_Z)
			);
		}
	}

	@Override
	public boolean onlyOpCanSetNbt() {
		// targeting Create here, sorry about any instances of https://xkcd.com/1172/
		return true;
	}
}
