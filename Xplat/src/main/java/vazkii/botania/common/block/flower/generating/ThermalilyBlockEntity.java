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
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.handler.BotaniaSounds;

public class ThermalilyBlockEntity extends FluidGeneratorBlockEntity {
	public static final int COOLDOWN_TICKS_MULTIPLER = 400;
	public static final String TAG_COOLDOWN_MAGNITUDE = "cooldownStrength";
	public static final int FAST_PROVIDE_TICKS = 10;
	public static final int MAX_HEAT = 25;

	private int cooldownStrength = 15;
	private int ticksSinceFueled = 0;
	private int heat;

	public ThermalilyBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaFlowerBlocks.THERMALILY, pos, state, FluidTags.LAVA, 600);
	}

	@Override
	public int manaPerTick() {
		return 45 + heat * 2;
	}

	@Override
	public int getCooldownTime(boolean finishedPrevious) {
		if (finishedPrevious) {
			cooldownStrength = rollNewCooldownStrength(getLevel().getRandom(), heat);
		}
		return COOLDOWN_TICKS_MULTIPLER * cooldownStrength;
	}

	public static int rollNewCooldownStrength(RandomSource random, int bias) {
		return Math.min(Math.max(Math.round(Mth.normal(random, 10 - bias / 3, 4 - bias / 10)), 1), 15);
	}

	@Override
	public void tickFlower() {
		super.tickFlower();
		if (burnTime == 0 && cooldown == 0) {
			ticksSinceFueled++;
		} else if (burnTime == startBurnTime) {
			if (ticksSinceFueled <= FAST_PROVIDE_TICKS) {
				heat = heat < MAX_HEAT ? heat + 1 : heat;
			} else {
				heat = 0;
			}
			ticksSinceFueled = 0;
		}
	}

	@Override
	public int getColor() {
		return 0xD03C00;
	}

	@Override
	public void doBurnParticles() {
		WispParticleData data = WispParticleData.wisp((float) Math.random() / 6, 0.7F, 0.05F, 0.05F, 1);
		emitParticle(data, 0.5 + Math.random() * 0.2 - 0.1, 0.9 + Math.random() * 0.2 - 0.1, 0.5 + Math.random() * 0.2 - 0.1, 0, (float) Math.random() / 60, 0);
	}

	@Override
	public void playSound() {
		getLevel().playSound(null, getEffectivePos(), BotaniaSounds.thermalily, SoundSource.BLOCKS, 1F, 1F);
	}

	@Override
	public int getComparatorSignal() {
		return burnTime > 0 ? 0 : cooldownStrength;
	}

	@Override
	public int getMaxMana() {
		return 750;
	}

	@Override
	public void writeToPacketNBT(CompoundTag cmp) {
		super.writeToPacketNBT(cmp);

		cmp.putInt(TAG_COOLDOWN_MAGNITUDE, cooldownStrength);
	}

	@Override
	public void readFromPacketNBT(CompoundTag cmp) {
		super.readFromPacketNBT(cmp);

		cooldownStrength = cmp.getInt(TAG_COOLDOWN_MAGNITUDE);
	}
}
