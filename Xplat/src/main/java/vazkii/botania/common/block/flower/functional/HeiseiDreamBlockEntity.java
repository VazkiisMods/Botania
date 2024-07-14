/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.flower.functional;

import com.google.common.base.Predicates;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import vazkii.botania.api.block_entity.FunctionalFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.mixin.HurtByTargetGoalAccessor;
import vazkii.botania.mixin.MobAccessor;

import java.util.Arrays;
import java.util.List;

public class HeiseiDreamBlockEntity extends FunctionalFlowerBlockEntity {
	private static final int RANGE = 5;
	private static final int COST = 100;

	public HeiseiDreamBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaFlowerBlocks.HEISEI_DREAM, pos, state);
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		if (getLevel().isClientSide) {
			return;
		}

		List<Mob> mobs = getLevel().getEntitiesOfClass(Mob.class, new AABB(getEffectivePos()).inflate(RANGE), Predicates.instanceOf(Enemy.class));

		if (mobs.size() > 1 && getMana() >= COST) {
			for (Mob mob : mobs) {
				if (brainwashEntity(mob, mobs)) {
					addMana(-COST);
					sync();
					break;
				}
			}
		}
	}

	public static boolean brainwashEntity(Mob entity, List<Mob> mobs) {
		LivingEntity target = entity.getTarget();
		boolean did = false;

		if (!(target instanceof Enemy)) {
			Mob newTarget;
			do {
				newTarget = mobs.get(entity.level().random.nextInt(mobs.size()));
			} while (newTarget == entity);

			entity.setTarget(null);

			// Move any HurtByTargetGoal to highest priority
			GoalSelector targetSelector = ((MobAccessor) entity).getTargetSelector();
			for (WrappedGoal entry : targetSelector.getAvailableGoals()) {
				if (entry.getGoal() instanceof HurtByTargetGoal goal) {
					// Remove all ignorals. We can't actually resize or overwrite
					// the array, but we can fill it with classes that will never pass
					// the game logic's checks.
					var ignoreClasses = ((HurtByTargetGoalAccessor) goal).getIgnoreDamageClasses();
					Arrays.fill(ignoreClasses, Void.TYPE);

					// Concurrent modification OK since we break out of the loop
					targetSelector.removeGoal(goal);
					targetSelector.addGoal(-1, goal);
					break;
				}
			}

			// Now set last hurt by, which HurtByTargetGoal will pick up
			entity.setLastHurtByMob(newTarget);
			did = true;
		}

		return did;
	}

	@Override
	public RadiusDescriptor getRadius() {
		return RadiusDescriptor.Rectangle.square(getEffectivePos(), RANGE);
	}

	@Override
	public int getColor() {
		return 0xFF219D;
	}

	@Override
	public int getMaxMana() {
		return 1000;
	}

}
