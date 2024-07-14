/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.mana;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

/**
 * Fired when the mod is gathering all items on a player that have mana.
 * For example, if you are an accessory mod, you would listen to this event and add stacks in your
 * accessory slots to the list.
 */

public class ManaItemsEvent extends Event {

	private final Player entityPlayer;
	private final List<ItemStack> items;

	public ManaItemsEvent(Player entityPlayer, List<ItemStack> items) {
		this.entityPlayer = entityPlayer;
		this.items = items;
	}

	public Player getPlayer() {
		return entityPlayer;
	}

	public List<ItemStack> getItems() {
		return items;
	}
}
