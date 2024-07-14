/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.corporea;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when a corporea request is initiated. Can be cancelled.
 */
@Cancelable
public class CorporeaRequestEvent extends Event {

	private final CorporeaRequestMatcher matcher;
	private final int count;
	private final CorporeaSpark spark;
	private final boolean dryRun;

	public CorporeaRequestEvent(CorporeaRequestMatcher matcher, int count, CorporeaSpark spark, boolean dryRun) {
		this.matcher = matcher;
		this.count = count;
		this.spark = spark;
		this.dryRun = dryRun;
	}

	public CorporeaRequestMatcher getMatcher() {
		return matcher;
	}

	public int getCount() {
		return count;
	}

	public CorporeaSpark getSpark() {
		return spark;
	}

	/**
	 * @return {@code true} if this is a dry run, else {@code false}.
	 */
	public boolean isDryRun() {
		return dryRun;
	}
}
