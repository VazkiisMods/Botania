/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.Event;

import java.util.List;

/**
 * An event fired when an Elven Portal updates. The portal's
 * relevant bounding box and other stuff is passed in for convenience.
 */
public class ElvenPortalUpdateEvent extends Event {

	private final BlockEntity portalTile;
	private final AABB aabb;
	private final boolean open;
	private final List<ItemStack> stacksInside;

	public ElvenPortalUpdateEvent(BlockEntity te, AABB aabb, boolean open, List<ItemStack> stacks) {
		portalTile = te;
		this.aabb = aabb;
		this.open = open;
		stacksInside = stacks;
	}

	/**
	 * May be casted to AlfheimPortalBlockEntity if you have botania code access aside from the API.
	 */
	public BlockEntity getPortalTile() {
		return portalTile;
	}

	public AABB getAabb() {
		return aabb;
	}

	public boolean isOpen() {
		return open;
	}

	public List<ItemStack> getStacksInside() {
		return stacksInside;
	}
}
