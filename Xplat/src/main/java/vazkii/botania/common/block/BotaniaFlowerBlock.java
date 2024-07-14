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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.item.material.MysticalPetalItem;
import vazkii.botania.xplat.BotaniaConfig;

public class BotaniaFlowerBlock extends FlowerBlock implements BonemealableBlock {
	public final DyeColor color;

	protected BotaniaFlowerBlock(DyeColor color, Properties builder) {
		super(effectForFlower(color), 4, builder);
		this.color = color;
	}

	private static MobEffect effectForFlower(DyeColor color) {
		return switch (color) {
			case WHITE -> MobEffects.MOVEMENT_SPEED;
			case ORANGE -> MobEffects.FIRE_RESISTANCE;
			case MAGENTA -> MobEffects.DIG_SLOWDOWN;
			case LIGHT_BLUE -> MobEffects.JUMP;
			case YELLOW -> MobEffects.ABSORPTION;
			case LIME -> MobEffects.POISON;
			case PINK -> MobEffects.REGENERATION;
			case GRAY -> MobEffects.DAMAGE_RESISTANCE;
			case LIGHT_GRAY -> MobEffects.WEAKNESS;
			case CYAN -> MobEffects.WATER_BREATHING;
			case PURPLE -> MobEffects.CONFUSION;
			case BLUE -> MobEffects.NIGHT_VISION;
			case BROWN -> MobEffects.WITHER;
			case GREEN -> MobEffects.HUNGER;
			case RED -> MobEffects.DAMAGE_BOOST;
			case BLACK -> MobEffects.BLINDNESS;
		};
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
		int hex = MysticalPetalItem.getPetalLikeColor(this.color);
		int r = (hex & 0xFF0000) >> 16;
		int g = (hex & 0xFF00) >> 8;
		int b = hex & 0xFF;
		Vec3 offset = state.getOffset(world, pos);
		double x = pos.getX() + offset.x;
		double y = pos.getY() + offset.y;
		double z = pos.getZ() + offset.z;

		if (rand.nextDouble() < BotaniaConfig.client().flowerParticleFrequency()) {
			SparkleParticleData data = SparkleParticleData.sparkle(rand.nextFloat(), r / 255F, g / 255F, b / 255F, 5);
			world.addParticle(data, x + 0.3 + rand.nextFloat() * 0.5, y + 0.5 + rand.nextFloat() * 0.5, z + 0.3 + rand.nextFloat() * 0.5, 0, 0, 0);
		}
	}

	@Override
	public boolean isValidBonemealTarget(@NotNull LevelReader world, @NotNull BlockPos pos, @NotNull BlockState state) {
		return world.getBlockState(pos.above()).isAir();
	}

	@Override
	public boolean isBonemealSuccess(@NotNull Level world, @NotNull RandomSource rand, @NotNull BlockPos pos, @NotNull BlockState state) {
		return isValidBonemealTarget(world, pos, state);
	}

	@Override
	public void performBonemeal(@NotNull ServerLevel world, @NotNull RandomSource rand, @NotNull BlockPos pos, @NotNull BlockState state) {
		Block block = BotaniaBlocks.getDoubleFlower(color);
		if (block instanceof DoublePlantBlock) {
			DoublePlantBlock.placeAt(world, block.defaultBlockState(), pos, 3);
		}
	}
}
