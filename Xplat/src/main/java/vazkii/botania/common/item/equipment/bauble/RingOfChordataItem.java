/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.bauble;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.item.BotaniaItems;

public class RingOfChordataItem extends BaubleItem {

	private static final int COST = 3;

	public RingOfChordataItem(Properties props) {
		super(props);
	}

	@Override
	public void onWornTick(ItemStack stack, LivingEntity living) {
		if (living.isInWaterOrBubble()) {
			// only activate for one ring at a time
			ItemStack result = EquipmentHandler.findOrEmpty(BotaniaItems.waterRing, living);
			if (result != stack) {
				return;
			}

			if (!living.level().isClientSide) {
				if (living instanceof Player player && !ManaItemHandler.instance().requestManaExact(stack, player, COST, true)) {
					onUnequipped(stack, living);
				} else {
					addEffect(living, MobEffects.CONDUIT_POWER);
					addEffect(living, MobEffects.DOLPHINS_GRACE);
				}
			}
		} else {
			onUnequipped(stack, living);
		}
	}

	private static void addEffect(LivingEntity living, Holder<MobEffect> effect) {
		MobEffectInstance inst = living.getEffect(effect);
		if (inst == null || (inst.getAmplifier() == 0 && inst.getDuration() == 1)) {
			MobEffectInstance neweffect = new MobEffectInstance(effect, 100, 0, true, true);
			living.addEffect(neweffect);
		}
	}

}
