/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.annotations.SoftImplement;
import vazkii.botania.common.block.decor.BotaniaGlassBlock;

public class PermanentBifrostBlock extends BotaniaGlassBlock implements BeaconBeamBlock {
	public PermanentBifrostBlock(Properties builder) {
		super(builder);
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
		if (rand.nextBoolean()) {
			SparkleParticleData data = SparkleParticleData.sparkle(0.45F + 0.2F * (float) Math.random(), (float) Math.random(), (float) Math.random(), (float) Math.random(), 6);
			world.addParticle(data, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0, 0, 0);
		}
	}

	@Override
	public DyeColor getColor() {
		return DyeColor.WHITE;
	}

	@SoftImplement("IBlockExtension")
	public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
		// Note: pos and beaconPos are not accurate when called from Fabric code
		int rgb = Mth.hsvToRgb(((Level) level).getGameTime() * 5 % 360 / 360F, 0.4F, 0.9F);
		float[] ret = new float[3];
		ret[0] = ((rgb >> 16) & 0xFF) / 255.0F;
		ret[1] = ((rgb >> 8) & 0xFF) / 255.0F;
		ret[2] = (rgb & 0xFF) / 255.0F;
		return ret;
	}
}
