/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.relic;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.api.item.Relic;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.item.BotaniaItems;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class RingOfThorItem extends RelicBaubleItem {

	public RingOfThorItem(Properties props) {
		super(props);
	}

	public static Relic makeRelic(ItemStack stack) {
		return new RelicImpl(stack, botaniaRL("challenge/thor_ring"));
	}

	public static ItemStack getThorRing(Player player) {
		return EquipmentHandler.findOrEmpty(BotaniaItems.thorRing, player);
	}
}
