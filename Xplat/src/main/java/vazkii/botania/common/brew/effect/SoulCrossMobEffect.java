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

import vazkii.botania.common.brew.BotaniaMobEffects;

public class SoulCrossMobEffect extends MobEffect {

	public SoulCrossMobEffect() {
		super(MobEffectCategory.BENEFICIAL, 0x47453d);
	}

	public static void onEntityKill(LivingEntity dying, LivingEntity killer) {
		if (killer.hasEffect(BotaniaMobEffects.SOUL_CROSS)) {
			killer.heal(dying.getMaxHealth() / 20);
		}
	}

}
