/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.relic;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import vazkii.botania.common.item.equipment.bauble.BaubleItem;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;

public abstract class RelicBaubleItem extends BaubleItem {

	public RelicBaubleItem(Properties props) {
		super(props);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean held) {
		if (!world.isClientSide && entity instanceof Player player) {
			var relic = XplatAbstractions.INSTANCE.findRelic(stack);
			if (relic != null) {
				relic.tickBinding(player);
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
		super.appendHoverText(stack, context, tooltip, flags);
		RelicImpl.addDefaultTooltip(stack, tooltip);
	}

	@Override
	public void onWornTick(ItemStack stack, LivingEntity entity) {
		var relic = XplatAbstractions.INSTANCE.findRelic(stack);
		if (relic != null && entity instanceof Player ePlayer) {
			relic.tickBinding(ePlayer);
			if (relic.isRightPlayer(ePlayer)) {
				onValidPlayerWornTick(ePlayer);
			}
		}
	}

	public void onValidPlayerWornTick(Player player) {}

	@Override
	public boolean canEquip(ItemStack stack, LivingEntity entity) {
		var relic = XplatAbstractions.INSTANCE.findRelic(stack);
		return entity instanceof Player player && relic != null && relic.isRightPlayer(player);
	}
}
