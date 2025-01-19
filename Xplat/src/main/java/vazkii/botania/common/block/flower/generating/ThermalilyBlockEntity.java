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
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.handler.BotaniaSounds;

public class ThermalilyBlockEntity extends FluidGeneratorBlockEntity {
	public static final int COOLDOWN_TICKS_MULTIPLER = 400;
	public static final String TAG_COOLDOWN_MAGNITUDE = "cooldownStrength";

	private int cooldownStrength = 15;
	public static final int[] COOLDOWN_ROLL_PDF = { 10, 5, 3, 2, 1, 1, 3, 3, 3, 2, 1, 1, 1, 2, 2 };
	public static final int COOLDOWN_ROLL_TOTAL;

	static {
		int acc = 0;
		for (var i : COOLDOWN_ROLL_PDF) {
			acc += i;
		}
		COOLDOWN_ROLL_TOTAL = acc;
	}

	public ThermalilyBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaFlowerBlocks.THERMALILY, pos, state, FluidTags.LAVA, 600, 45);
	}

	@Override
	public int getCooldownTime(boolean finishedPrevious) {
		if (finishedPrevious) {
			cooldownStrength = rollNewCooldownStrength(getLevel().getRandom());
		}
		return COOLDOWN_TICKS_MULTIPLER * cooldownStrength;
	}

	public static int rollNewCooldownStrength(RandomSource random) {
		var total = random.nextInt(COOLDOWN_ROLL_TOTAL);
		var index = 0;
		while (total >= COOLDOWN_ROLL_PDF[index]) {
			total -= COOLDOWN_ROLL_PDF[index];
			index++;
		}
		return index + 1;
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
	public void writeToPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.writeToPacketNBT(cmp, registries);

		cmp.putInt(TAG_COOLDOWN_MAGNITUDE, cooldownStrength);
	}

	@Override
	public void readFromPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.readFromPacketNBT(cmp, registries);

		cooldownStrength = cmp.getInt(TAG_COOLDOWN_MAGNITUDE);
	}
}
