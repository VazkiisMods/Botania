/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.flower.generating;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.block.block_entity.CellularBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class DandelifeonBlockEntity extends GeneratingFlowerBlockEntity {
	public static final int RANGE = 12;
	public static final int SPEED = 10;
	public static final int OVERGROWN_SPEED = SPEED / 2;
//	private static final int MAX_GENERATIONS = 100;
	public static final int MAX_MANA_GENERATIONS = 100;
	public static final int MANA_PER_GEN = 60;

	private static final String TAG_RADIUS = "radius";

	private int radius = RANGE;

	private static final int[][] ADJACENT_BLOCKS = new int[][] {
			{ -1, -1 },
			{ -1, +0 },
			{ -1, +1 },
			{ +0, +1 },
			{ +1, +1 },
			{ +1, +0 },
			{ +1, -1 },
			{ +0, -1 }
	};

	public DandelifeonBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaFlowerBlocks.DANDELIFEON, pos, state);
	}

	public int getRange() {
		return radius;
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		if (!getLevel().isClientSide) {
			if (shouldTick(getLevel().getGameTime())) {
				runSimulation();
			} else if (shouldTick(getLevel().getGameTime() + 1)) {
				int diameter = radius * 2;

				for (int i = 0; i <= diameter; i++) {
					for (int j = 0; j <= diameter; j++) {
						BlockPos pos = getEffectivePos().offset(-radius + i, 0, -radius + j);
						BlockEntity tile = getLevel().getBlockEntity(pos);
						if (tile instanceof CellularBlockEntity) {
							((CellularBlockEntity) (tile)).claim(this);
						}
					}
				}
			}
		}
	}

	private boolean shouldTick(long gameTime) {
		return (gameTime % SPEED == 0 || (gameTime % (OVERGROWN_SPEED) == 0 && overgrowthBoost)) && getLevel().hasNeighborSignal(getBlockPos());
	}

	private void runSimulation() {
		var table = new CellTable(radius, this);
		List<LifeUpdate> changes = new ArrayList<>();
		boolean wipe = false;

		for (int i = 0; i < table.diameter; i++) {
			for (int j = 0; j < table.diameter; j++) {
				int oldLife = table.at(i, j);
				int adj = table.getAdjCells(i, j);

				int newLife;
				if (adj == 3 && oldLife == Cell.DEAD) {
					// spawn new cell
					newLife = table.getSpawnCellGeneration(i, j);
				} else if ((adj == 2 || adj == 3) && Cell.isLive(oldLife)) {
					// sustain extant cell
					newLife = oldLife + 1;
				} else {
					// kill cell
					newLife = Cell.DEAD;
				}

				int xdist = Math.abs(i - radius);
				int zdist = Math.abs(j - radius);
				int allowDist = 1;
				if (xdist <= allowDist && zdist <= allowDist && Cell.isLive(newLife)) {
					if (oldLife == 1) {
						// fresh cells shouldn't be placed here at all
						newLife = Cell.DEAD;
					} else {
						// save the new value and consume this cell
						oldLife = newLife;
						newLife = Cell.CONSUME;
						wipe = true;
					}
				}

				if (newLife != oldLife) {
					changes.add(new LifeUpdate(i, j, newLife, oldLife));
				}
			}
		}

		for (var change : changes) {
			BlockPos pos_ = table.center.offset(-radius + change.x(), 0, -radius + change.z());
			int newLife = change.newLife();
			if (newLife != Cell.CONSUME && wipe) {
				newLife = Cell.DEAD;
			}

			setBlockForGeneration(pos_, Math.min(newLife, MAX_MANA_GENERATIONS), change.oldLife());
		}
	}

	void setBlockForGeneration(BlockPos pos, int cell, int prevCell) {
		Level world = getLevel();
		BlockState stateAt = world.getBlockState(pos);
		BlockEntity tile = world.getBlockEntity(pos);
		if (cell == Cell.CONSUME) {
			if (stateAt.isAir()) {
				int val = prevCell * MANA_PER_GEN;
				world.removeBlock(pos, true);
				addMana(val);
				sync();
			}
		} else if (tile instanceof CellularBlockEntity cellBlock) {
			cellBlock.setNextGeneration(this, cell);
		} else if (Cell.isLive(cell) && stateAt.isAir()) {
			world.setBlockAndUpdate(pos, BotaniaBlocks.cellBlock.defaultBlockState());
			tile = world.getBlockEntity(pos);
			((CellularBlockEntity) tile).setNextGeneration(this, cell);
			((CellularBlockEntity) tile).setGeneration(Cell.DEAD); // so that other flowers know this is 'dead' this tick
		}
	}

	public static final class Cell {
		private Cell() {}

		public static final int CONSUME = -2;
		public static final int DEAD = -1;

		public static boolean isLive(int i) {
			return i >= 0;
		}

		public static int boundaryPunish(int life) {
			return isLive(life) ? life / 4 : life;
		}
	}

	private static class CellTable {
		public final BlockPos center;
		public final int diameter;
		private int[][] cells;

		public CellTable(int range, DandelifeonBlockEntity dandie) {
			center = dandie.getEffectivePos();
			diameter = range * 2 + 1;
			// store everything in range + one outside
			cells = new int[diameter + 2][diameter + 2];

			for (int i = -1; i <= diameter; i++) {
				for (int j = -1; j <= diameter; j++) {
					BlockPos pos = center.offset(-range + i, 0, -range + j);
					cells[i + 1][j + 1] = getCellGeneration(pos, dandie, onBoundary(i, j));
				}
			}
		}

		private static int getCellGeneration(BlockPos pos, DandelifeonBlockEntity dandie, boolean onBoundary) {
			BlockEntity tile = dandie.getLevel().getBlockEntity(pos);
			if (tile instanceof CellularBlockEntity cell) {
				return onBoundary ? (cell.hasActiveParent(dandie) ? Cell.boundaryPunish(cell.getGeneration()) : Cell.DEAD) : cell.getGeneration();
			}

			return Cell.DEAD;
		}

		public boolean inBounds(int x, int z) {
			return x >= 0 && z >= 0 && x < diameter && z < diameter;
		}

		private boolean onBoundary(int x, int z) {
			return x == -1 || z == -1 || x == diameter || z == diameter;
		}

		public int getAdjCells(int x, int z) {
			int count = 0;
			for (int[] shift : ADJACENT_BLOCKS) {
				if (Cell.isLive(this.at(x + shift[0], z + shift[1]))) {
					count++;
				}
			}

			return count;
		}

		public int getSpawnCellGeneration(int x, int z) {
			int max = -1;
			for (int[] shift : ADJACENT_BLOCKS) {
				max = Math.max(max, this.at(x + shift[0], z + shift[1]));
			}

			return max == -1 ? Cell.DEAD : max + 1;
		}

		public int at(int x, int z) {
			return cells[x + 1][z + 1];
		}
	}

	private static record LifeUpdate(int x, int z, int newLife, int oldLife) {
	}

	@Override
	public RadiusDescriptor getRadius() {
		return RadiusDescriptor.Rectangle.square(getEffectivePos(), radius);
	}

	@Override
	public RadiusDescriptor getSecondaryRadius() {
		return RadiusDescriptor.Rectangle.square(getEffectivePos(), 1);
	}

	@Override
	public int getMaxMana() {
		return 50000;
	}

	@Override
	public int getColor() {
		return 0x9c0a7e;
	}

	@Override
	public void writeToPacketNBT(CompoundTag cmp) {
		super.writeToPacketNBT(cmp);
		if (radius != RANGE) {
			cmp.putInt(TAG_RADIUS, radius);
		}
	}

	@Override
	public void readFromPacketNBT(CompoundTag cmp) {
		super.readFromPacketNBT(cmp);
		radius = cmp.contains(TAG_RADIUS) ? cmp.getInt(TAG_RADIUS) : RANGE;
	}

}
