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

public class ManaProficiencyEvent extends Event {

	private final Player entityPlayer;
	private final ItemStack tool;
	private boolean proficient;

	public ManaProficiencyEvent(Player entityPlayer, ItemStack tool, boolean proficient) {
		this.entityPlayer = entityPlayer;
		this.tool = tool;
		this.proficient = proficient;
	}

	public Player getEntityPlayer() {
		return entityPlayer;
	}

	public ItemStack getTool() {
		return tool;
	}

	public boolean isProficient() {
		return proficient;
	}

	public void setProficient(boolean proficient) {
		this.proficient = proficient;
	}
}
