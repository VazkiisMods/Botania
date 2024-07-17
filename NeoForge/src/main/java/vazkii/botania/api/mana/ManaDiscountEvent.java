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
import net.neoforged.bus.api.Event;

public class ManaDiscountEvent extends Event {

	private final Player entityPlayer;
	private float discount;
	private final ItemStack tool;

	public ManaDiscountEvent(Player entityPlayer, float discount, ItemStack tool) {
		this.entityPlayer = entityPlayer;
		this.discount = discount;
		this.tool = tool;
	}

	public ItemStack getTool() {
		return tool;
	}

	public Player getEntityPlayer() {
		return entityPlayer;
	}

	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}
}
