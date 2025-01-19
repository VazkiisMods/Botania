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
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.xplat.BotaniaConfig;

import java.util.*;

public class MunchdewBlockEntity extends GeneratingFlowerBlockEntity {
	public static final String TAG_COOLDOWN = "cooldown";
	private static final String TAG_ATE_ONCE = "ateOnce";

	private static final int RANGE = 8;
	private static final int RANGE_Y = 16;

	private boolean ateOnce = false;
	private int ticksWithoutEating = -1;
	private int cooldown = 0;

	public MunchdewBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaFlowerBlocks.MUNCHDEW, pos, state);
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		if (getLevel().isClientSide) {
			return;
		}

		if (cooldown > 0) {
			cooldown--;
			ticksWithoutEating = 0;
			ateOnce = false; // don't start ticking ticksWithoutEating again until we eat again
			return;
		}

		int manaPerLeaf = 160;
		eatLeaves: {
			if (getMaxMana() - getMana() >= manaPerLeaf && ticksExisted % 4 == 0) {
				Map<BlockPos, Float> coordsMap = new HashMap<>();
				Random rng = new Random();
				BlockPos pos = getEffectivePos();

				for (BlockPos pos_ : BlockPos.betweenClosed(pos.offset(-RANGE, 0, -RANGE),
						pos.offset(RANGE, RANGE_Y, RANGE))) {
					BlockState state = getLevel().getBlockState(pos_);
					if (state.is(BlockTags.LEAVES)) {
						for (Direction dir : Direction.values()) {
							if (getLevel().isEmptyBlock(pos_.relative(dir))) {
								coordsMap.put(pos_.immutable(), (state.hasProperty(LeavesBlock.DISTANCE)
										? state.getValue(LeavesBlock.DISTANCE) : 1) + 2.0f * rng.nextFloat());
								break;
							}
						}
					}
				}

				if (coordsMap.isEmpty()) {
					break eatLeaves;
				}

				float maxDistance = 0F;
				for (float distance : coordsMap.values()) {
					maxDistance = Math.max(maxDistance, distance);
				}

				float finalMaxDistance = maxDistance;
				coordsMap.values().removeIf(dist -> dist < finalMaxDistance - 1f);
				List<BlockPos> coords = new ArrayList<>(coordsMap.keySet());

				BlockPos breakCoords = coords.get(level.getRandom().nextInt(coords.size()));
				BlockState state = getLevel().getBlockState(breakCoords);
				getLevel().removeBlock(breakCoords, false);
				ticksWithoutEating = 0;
				ateOnce = true;
				if (BotaniaConfig.common().blockBreakParticles()) {
					getLevel().levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, breakCoords, Block.getId(state));
				}
				getLevel().gameEvent(null, GameEvent.BLOCK_DESTROY, breakCoords);
				addMana(manaPerLeaf);
			}
		}

		if (ateOnce) {
			ticksWithoutEating++;
			if (ticksWithoutEating >= 5) {
				cooldown = 1600;
				sync();
			}
		}
	}

	@Override
	public RadiusDescriptor getRadius() {
		return RadiusDescriptor.Rectangle.square(getEffectivePos(), RANGE);
	}

	@Override
	public void writeToPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.writeToPacketNBT(cmp, registries);

		cmp.putInt(TAG_COOLDOWN, cooldown);
		cmp.putBoolean(TAG_ATE_ONCE, ateOnce);
	}

	@Override
	public void readFromPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.readFromPacketNBT(cmp, registries);

		cooldown = cmp.getInt(TAG_COOLDOWN);
		ateOnce = cmp.getBoolean(TAG_ATE_ONCE);
	}

	@Override
	public int getColor() {
		return 0x79C42F;
	}

	@Override
	public int getMaxMana() {
		return 10000;
	}

}
