/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.mana;

import net.minecraftforge.eventbus.api.Event;

public class ManaNetworkEvent extends Event {
	private final ManaReceiver thing;
	private final ManaBlockType type;
	private final ManaNetworkAction action;

	public ManaNetworkEvent(ManaReceiver thing, ManaBlockType type, ManaNetworkAction action) {
		this.thing = thing;
		this.type = type;
		this.action = action;
	}

	/**
	 * @return If {@link #getType()} is {@link ManaBlockType#COLLECTOR}, an {@link ManaCollector},
	 *         otherwise if {@link #getType()} is {@link ManaBlockType#POOL}, an {@link ManaPool}
	 */
	public ManaReceiver getReceiver() {
		return thing;
	}

	public ManaBlockType getType() {
		return type;
	}

	public ManaNetworkAction getAction() {
		return action;
	}
}
