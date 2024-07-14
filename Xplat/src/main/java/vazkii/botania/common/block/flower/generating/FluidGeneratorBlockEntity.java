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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.client.fx.WispParticleData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class FluidGeneratorBlockEntity extends GeneratingFlowerBlockEntity {
	private static final String TAG_BURN_TIME = "burnTime";
	public static final String TAG_COOLDOWN = "cooldown";

	private static final BlockPos[] OFFSETS = { new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(-1, 0, 1), new BlockPos(-1, 0, -1), new BlockPos(1, 0, 1), new BlockPos(1, 0, -1) };

	public static final int DECAY_TIME = 72000;
	protected int burnTime, cooldown;
	private final TagKey<Fluid> consumedFluid;
	private final int startBurnTime, manaPerTick;

	protected FluidGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, TagKey<Fluid> consumedFluid, int startBurnTime, int manaPerTick) {
		super(type, pos, state);
		this.consumedFluid = consumedFluid;
		this.startBurnTime = startBurnTime;
		this.manaPerTick = manaPerTick;
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		if (cooldown > 0) {
			cooldown--;
			for (int i = 0; i < 3; i++) {
				WispParticleData data = WispParticleData.wisp((float) Math.random() / 6, 0.1F, 0.1F, 0.1F, 1);
				emitParticle(data, 0.5 + Math.random() * 0.2 - 0.1, 0.5 + Math.random() * 0.2 - 0.1, 0.5 + Math.random() * 0.2 - 0.1, 0, (float) Math.random() / 30, 0);
			}
		}

		if (!getLevel().isClientSide) {
			if (burnTime > 0 && ticksExisted % getGenerationDelay() == 0) {
				addMana(manaPerTick);
				sync();
			}
		}

		if (burnTime == 0) {
			if (getMana() < getMaxMana() && !getLevel().isClientSide) {
				List<BlockPos> offsets = Arrays.asList(OFFSETS);
				Collections.shuffle(offsets);

				for (BlockPos offset : offsets) {
					BlockPos pos = getEffectivePos().offset(offset);

					BlockState bstate = getLevel().getBlockState(pos);
					FluidState fstate = getLevel().getFluidState(pos);
					if (fstate.is(consumedFluid) && fstate.isSource()) {
						if (consumedFluid != FluidTags.WATER) {
							getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
						} else {
							int waterAround = 0;
							for (Direction dir : Direction.values()) {
								if (getLevel().getFluidState(pos.relative(dir)).is(consumedFluid)) {
									waterAround++;
								}
							}

							if (waterAround < 2) {
								if (bstate.getBlock() instanceof BucketPickup bucketPickup) {
									bucketPickup.pickupBlock(null, getLevel(), pos, bstate);
								} else {
									getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
								}
								getLevel().gameEvent(null, GameEvent.FLUID_PICKUP, pos);
							}
						}

						if (cooldown == 0) {
							burnTime += startBurnTime;
							getLevel().gameEvent(null, GameEvent.BLOCK_ACTIVATE, getBlockPos());
						} else {
							cooldown = getCooldownTime(false);
						}

						setChanged();
						sync();
						playSound();
						break;
					}
				}
			}
		} else {
			if (getLevel().random.nextInt(8) == 0) {
				doBurnParticles();
			}
			burnTime--;
			if (burnTime == 0) {
				cooldown = getCooldownTime(true);
				getLevel().gameEvent(null, GameEvent.BLOCK_DEACTIVATE, getBlockPos());
				setChanged();
				sync();
			}
		}
	}

	public abstract int getCooldownTime(boolean finishedPrevious);

	public int getGenerationDelay() {
		return 1;
	}

	public abstract void doBurnParticles();

	public abstract void playSound();

	@Override
	public void writeToPacketNBT(CompoundTag cmp) {
		super.writeToPacketNBT(cmp);

		cmp.putInt(TAG_BURN_TIME, burnTime);
		cmp.putInt(TAG_COOLDOWN, cooldown);
	}

	@Override
	public void readFromPacketNBT(CompoundTag cmp) {
		super.readFromPacketNBT(cmp);

		burnTime = cmp.getInt(TAG_BURN_TIME);
		cooldown = cmp.getInt(TAG_COOLDOWN);
	}

	@Override
	public RadiusDescriptor getRadius() {
		return RadiusDescriptor.Rectangle.square(getEffectivePos(), 1);
	}
}
