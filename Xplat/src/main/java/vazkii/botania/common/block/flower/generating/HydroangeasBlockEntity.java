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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.block.BotaniaFlowerBlocks;

public class HydroangeasBlockEntity extends FluidGeneratorBlockEntity {
	public static final String TAG_PASSIVE_DECAY_TICKS = "passiveDecayTicks";

	private static final BlockPos[] OFFSETS = { new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(-1, 0, 1), new BlockPos(-1, 0, -1), new BlockPos(1, 0, 1), new BlockPos(1, 0, -1) };

	public static final int DECAY_TIME = 72000;

	private int passiveDecayTicks;

	public HydroangeasBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaFlowerBlocks.HYDROANGEAS, pos, state, FluidTags.WATER, 40, 1);
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		if (!getLevel().isClientSide) {
			if (++passiveDecayTicks > DECAY_TIME) {
				getLevel().destroyBlock(getBlockPos(), false);
				if (Blocks.DEAD_BUSH.defaultBlockState().canSurvive(getLevel(), getBlockPos())) {
					getLevel().setBlockAndUpdate(getBlockPos(), Blocks.DEAD_BUSH.defaultBlockState());
				}
			}
		}
	}

	@Override
	public int getCooldownTime(boolean finishedPrevious) {
		return 0; // No cooldown at all
	}

	@Override
	public void doBurnParticles() {
		WispParticleData data = WispParticleData.wisp((float) Math.random() / 6, 0.05F, 0.05F, 0.7F, 1);
		emitParticle(data, 0.5 + Math.random() * 0.2 - 0.1, 0.55 + Math.random() * 0.2 - 0.1, 0.5 + Math.random() * 0.2 - 0.1, 0, (float) Math.random() / 60, 0);
	}

	@Override
	public void playSound() {
		//Usage of vanilla sound event: Subtitle is "Sipping", generic sounds are meant to be reused.
		getLevel().playSound(null, getEffectivePos(), SoundEvents.GENERIC_DRINK, SoundSource.BLOCKS, 0.01F, 0.5F + (float) Math.random() * 0.5F);
	}

	@Override
	public int getMaxMana() {
		return 150;
	}

	@Override
	public int getColor() {
		return 0x532FE0;
	}

	@Override
	public void readFromPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.readFromPacketNBT(cmp, registries);
		passiveDecayTicks = cmp.getInt(TAG_PASSIVE_DECAY_TICKS);
	}

	@Override
	public void writeToPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.writeToPacketNBT(cmp, registries);
		cmp.putInt(TAG_PASSIVE_DECAY_TICKS, passiveDecayTicks);
	}

	@Override
	public int getGenerationDelay() {
		boolean rain = getLevel().getBiome(getEffectivePos()).value()
				.getPrecipitationAt(getEffectivePos()) == Biome.Precipitation.RAIN
				&& (getLevel().isRaining() || getLevel().isThundering());
		return rain ? 2 : 3;
	}

	@Override
	public boolean isOvergrowthAffected() {
		return false;
	}
}
