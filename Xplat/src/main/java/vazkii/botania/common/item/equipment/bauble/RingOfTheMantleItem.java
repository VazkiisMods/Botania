/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.bauble;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.helper.EntityHelper;

public class RingOfTheMantleItem extends BaubleItem {

	public static final int MANA_COST = 5;
	public static final int HASTE_AMPLIFIER = 1; // Haste 2

	public RingOfTheMantleItem(Properties props) {
		super(props);
	}

	@Override
	public void onWornTick(ItemStack stack, LivingEntity entity) {
		if (!(entity instanceof Player player) || player.level().isClientSide) {
			return;
		}
		boolean hasMana = ManaItemHandler.instance().requestManaExact(stack, player, MANA_COST, false);
		if (!hasMana) {
			onUnequipped(stack, player);
		} else {
			onEquipped(stack, player);
		}

		if (player.attackAnim == 0.25F) {
			ManaItemHandler.instance().requestManaExact(stack, player, MANA_COST, true);
		}
	}

	@Override
	public void onEquipped(ItemStack stack, LivingEntity living) {
		boolean hasMana = living instanceof Player player
				&& ManaItemHandler.instance().requestManaExact(stack, player, MANA_COST, false);
		if (hasMana) {
			EntityHelper.addStaticEffect(living, MobEffects.DIG_SPEED, HASTE_AMPLIFIER);
		}
	}

	@Override
	public void onUnequipped(ItemStack stack, LivingEntity living) {
		EntityHelper.removeStaticEffect(living, MobEffects.DIG_SPEED, HASTE_AMPLIFIER);
	}

}
