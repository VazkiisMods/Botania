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

import org.jetbrains.annotations.NotNull;

public class FeatherfeetMobEffect extends MobEffect {

	public FeatherfeetMobEffect() {
		super(MobEffectCategory.BENEFICIAL, 0x26ADFF);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyEffectTick(@NotNull LivingEntity living, int amplified) {
		if (living.fallDistance > 2.5F) {
			living.fallDistance = 2.5F;
		}
	}

}
