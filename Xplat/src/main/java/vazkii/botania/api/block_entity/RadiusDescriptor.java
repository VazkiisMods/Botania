/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

/**
 * This object describes the Radius of a BlockEntity. It can either be
 * a circle or rectangle.
 */
public sealed interface RadiusDescriptor permits RadiusDescriptor.Circle,RadiusDescriptor.Rectangle {
	record Circle(BlockPos subtileCoords, double radius) implements RadiusDescriptor {
	}

	record Rectangle(BlockPos subtileCoords, AABB aabb) implements RadiusDescriptor {
		public static Rectangle square(BlockPos subtileCoords, int expand) {
			return new Rectangle(subtileCoords, new AABB(subtileCoords.getX() - expand, subtileCoords.getY(), subtileCoords.getZ() - expand,
					subtileCoords.getX() + expand + 1, subtileCoords.getY(), subtileCoords.getZ() + expand + 1));
		}
	}
}
