/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.brew.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import vazkii.botania.common.brew.BotaniaMobEffects;

public class EmptinessMobEffect extends MobEffect {

	private static final int RANGE = 128;

	public EmptinessMobEffect() {
		super(MobEffectCategory.BENEFICIAL, 0xFACFFF);
	}

	public static boolean shouldCancel(LivingEntity entity) {
		if (entity instanceof Enemy) {
			AABB aabb = new AABB(entity.getX() - RANGE, entity.getY() - RANGE, entity.getZ() - RANGE,
					entity.getX() + RANGE, entity.getY() + RANGE, entity.getZ() + RANGE);
			for (Player player : entity.level().players()) {
				if (player.hasEffect(BotaniaMobEffects.EMPTINESS) && player.getBoundingBox().intersects(aabb)) {
					return true;
				}
			}
		}
		return false;
	}

}
