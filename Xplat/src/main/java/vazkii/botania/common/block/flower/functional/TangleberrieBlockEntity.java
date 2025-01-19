/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.flower.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import vazkii.botania.api.block_entity.FunctionalFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.helper.MathHelper;

import java.util.List;

public class TangleberrieBlockEntity extends FunctionalFlowerBlockEntity {
	private static final double RANGE = 7;
	private static final double MAXDISTANCE = 6;
	private static final double RANGE_MINI = 3;
	private static final double MAXDISTANCE_MINI = 2;

	public TangleberrieBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public TangleberrieBlockEntity(BlockPos pos, BlockState state) {
		this(BotaniaFlowerBlocks.TANGLEBERRIE, pos, state);
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		if (getMana() > 0) {
			double x1 = getEffectivePos().getX() + 0.5;
			double y1 = getEffectivePos().getY() + 0.5;
			double z1 = getEffectivePos().getZ() + 0.5;

			double maxDist = getMaxDistance();
			double range = getRange();

			AABB boundingBox = new AABB(x1 - range, y1 - range, z1 - range, x1 + range + 1, y1 + range + 1, z1 + range + 1);
			List<LivingEntity> entities = getLevel().getEntitiesOfClass(LivingEntity.class, boundingBox);

			SparkleParticleData data = SparkleParticleData.sparkle(1F, 0.5F, 0.5F, 0.5F, 3);
			for (LivingEntity entity : entities) {
				if (entity instanceof Player || !entity.canUsePortal(false)) {
					continue;
				}

				double x2 = entity.getX();
				double y2 = entity.getY();
				double z2 = entity.getZ();

				float distance = MathHelper.pointDistanceSpace(x1, y1, z1, x2, y2, z2);

				if (distance > maxDist && distance < range) {
					MathHelper.setEntityMotionFromVector(entity, new Vec3(x1, y1, z1), getMotionVelocity(entity));
					if (getLevel().random.nextInt(3) == 0) {
						level.addParticle(data, x2 + Math.random() * entity.getBbWidth(), y2 + Math.random() * entity.getBbHeight(), z2 + Math.random() * entity.getBbWidth(), 0, 0, 0);
					}
				}
			}

			if (ticksExisted % 4 == 0) {
				addMana(-1);
				sync();
			}
		}
	}

	double getMaxDistance() {
		return MAXDISTANCE;
	}

	double getRange() {
		return RANGE;
	}

	float getMotionVelocity(LivingEntity entity) {
		return Math.max(entity.getSpeed() / 2F, 0.05F);
	}

	@Override
	public RadiusDescriptor getRadius() {
		return new RadiusDescriptor.Circle(getEffectivePos(), getRange());
	}

	@Override
	public RadiusDescriptor getSecondaryRadius() {
		if (getMaxDistance() == getRange()) {
			return null;
		}
		return new RadiusDescriptor.Circle(getEffectivePos(), getMaxDistance());
	}

	@Override
	public int getColor() {
		return 0x4B797C;
	}

	@Override
	public int getMaxMana() {
		return 20;
	}

	public static class Mini extends TangleberrieBlockEntity {
		public Mini(BlockPos pos, BlockState state) {
			super(BotaniaFlowerBlocks.TANGLEBERRIE_CHIBI, pos, state);
		}

		@Override
		public double getMaxDistance() {
			return MAXDISTANCE_MINI;
		}

		@Override
		public double getRange() {
			return RANGE_MINI;
		}
	}
}
