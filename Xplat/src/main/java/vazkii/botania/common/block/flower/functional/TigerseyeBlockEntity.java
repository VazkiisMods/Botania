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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import vazkii.botania.api.block_entity.FunctionalFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.mixin.*;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.Set;

public class TigerseyeBlockEntity extends FunctionalFlowerBlockEntity {
	private static final int RANGE = 10;
	private static final int RANGE_Y = 4;
	private static final int COST = 70;
	private static final int SUCCESS_EVENT = 0;

	public TigerseyeBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaFlowerBlocks.TIGERSEYE, pos, state);
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		if (getLevel().isClientSide) {
			return;
		}

		for (Creeper entity : getLevel().getEntitiesOfClass(Creeper.class, new AABB(getEffectivePos()).inflate(RANGE))) {
			((CreeperAccessor) entity).setCurrentFuseTime(2);
			entity.setTarget(null);

			if (getMana() >= COST) {
				if (pacifyCreeper(entity)) {
					XplatAbstractions.INSTANCE.tigersEyeComponent(entity).setPacified();
					entity.playSound(BotaniaSounds.tigerseyePacify, 1.0F, (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F);
					level.blockEvent(getBlockPos(), getBlockState().getBlock(), SUCCESS_EVENT, entity.getId());
					addMana(-COST);
					sync();
				}
			}
		}
	}

	public static void pacifyAfterLoad(Entity entity, ServerLevel level) {
		if (entity instanceof Creeper creeper && XplatAbstractions.INSTANCE.tigersEyeComponent(creeper).isPacified()) {
			pacifyCreeper(creeper);
		}
	}

	private static boolean pacifyCreeper(Creeper creeper) {
		boolean did = false;
		GoalSelector goalSelector = ((MobAccessor) creeper).getGoalSelector();
		Set<WrappedGoal> goals = goalSelector.getAvailableGoals();
		for (var goal : goals) {
			Goal wrapped = goal.getGoal();
			if (wrapped instanceof CreeperAvoidPlayerGoal playerGoal && !playerGoal.enabled) {
				playerGoal.enable();
				did = true;
				break;
			}
		}

		GoalSelector targetSelector = ((MobAccessor) creeper).getTargetSelector();
		for (var iterator = targetSelector.getAvailableGoals().iterator(); iterator.hasNext();) {
			WrappedGoal pg = iterator.next();
			if (pg.getGoal() instanceof NearestAttackableTargetGoalAccessor targetGoal
					&& targetGoal.getTargetClass() == Player.class) {
				iterator.remove();
				did = true;
			}
		}
		return did;
	}

	@Override
	public boolean triggerEvent(int id, int payload) {
		if (id == SUCCESS_EVENT) {
			if (level.isClientSide) {
				Entity e = level.getEntity(payload);
				if (e != null) {
					float r = (getColor() >> 16 & 0xFF) / 255F;
					float g = (getColor() >> 8 & 0xFF) / 255F;
					float b = (getColor() & 0xFF) / 255F;
					SparkleParticleData data = SparkleParticleData.sparkle(level.random.nextFloat(), r, g, b, 10);

					for (int i = 0; i < 50; i++) {
						double x = e.getX() + level.random.nextDouble() - 0.5;
						double y = e.getY() + e.getBbHeight() * level.random.nextDouble();
						double z = e.getZ() + level.random.nextDouble() - 0.5;
						level.addParticle(data, x, y, z, 0, 0, 0);
					}
				}
			}
			return true;
		}
		return super.triggerEvent(id, payload);
	}

	@Override
	public RadiusDescriptor getRadius() {
		return RadiusDescriptor.Rectangle.square(getEffectivePos(), RANGE);
	}

	@Override
	public int getColor() {
		return 0xB1A618;
	}

	@Override
	public int getMaxMana() {
		return 1000;
	}

	public static class CreeperAvoidPlayerGoal extends AvoidEntityGoal<Player> {
		private boolean enabled = false;

		public CreeperAvoidPlayerGoal(Creeper mob) {
			this(mob, 6.0F, 1.0, 1.2);
		}

		private CreeperAvoidPlayerGoal(Creeper mob, float maxDist, double walkSpeedModifier, double sprintSpeedModifier) {
			super(mob, Player.class, maxDist, walkSpeedModifier, sprintSpeedModifier);
		}

		public void enable() {
			enabled = true;
		}

		@Override
		public boolean canUse() {
			return enabled && super.canUse();
		}
	}

}
