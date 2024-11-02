/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.lens;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.api.mana.ManaSpreader;
import vazkii.botania.common.block.block_entity.mana.ThrottledPacket;
import vazkii.botania.common.helper.MathHelper;
import vazkii.botania.xplat.XplatAbstractions;

public class RedirectiveLens extends Lens {

	@Override
	public boolean collideBurst(ManaBurst burst, HitResult pos, boolean isManaBlock, boolean shouldKill, ItemStack stack) {
		BlockPos sourcePos = burst.getBurstSourceBlockPos();
		var burstEntity = burst.entity();
		if (!burstEntity.level().isClientSide && !burst.isFake()) {
			if (pos instanceof BlockHitResult result
					&& result.getType() != HitResult.Type.MISS
					&& !result.getBlockPos().equals(sourcePos)) {
				handleHitBlock(burst, result);
			} else if (pos instanceof EntityHitResult result
					&& result.getEntity() != burstEntity.getOwner()) {
				handleHitEntity(burst, result);
			}
		}

		return shouldKill;
	}

	@Nullable
	private static Vec3 getSourceVec(ManaBurst burst) {
		var entity = burst.entity();
		var owner = entity.getOwner();
		var sourcePos = burst.getBurstSourceBlockPos();
		if (!sourcePos.equals(ManaBurst.NO_SOURCE)) {
			var sourceVec = Vec3.atCenterOf(sourcePos);
			AABB axis;
			VoxelShape collideShape = entity.level().getBlockState(sourcePos).getCollisionShape(entity.level(), sourcePos);
			if (collideShape.isEmpty()) {
				axis = new AABB(sourcePos);
			} else {
				axis = collideShape.bounds().move(sourcePos);
			}

			if (!axis.contains(sourceVec)) {
				sourceVec = new Vec3(axis.minX + (axis.maxX - axis.minX) / 2, axis.minY + (axis.maxY - axis.minY) / 2, axis.minZ + (axis.maxZ - axis.minZ) / 2);
			}
			return sourceVec;
		} else if (owner != null) {
			return owner.getEyePosition();
		} else {
			// No block nor entity source, should never happen but don't crash
			return null;
		}
	}

	private void handleHitEntity(ManaBurst burst, EntityHitResult result) {
		var sourceVec = getSourceVec(burst);
		if (sourceVec != null) {
			result.getEntity().lookAt(EntityAnchorArgument.Anchor.EYES, sourceVec);
		}
	}

	private void handleHitBlock(ManaBurst burst, BlockHitResult result) {
		var sourceVec = getSourceVec(burst);
		if (sourceVec == null) {
			return;
		}

		var entity = burst.entity();
		var hitPos = result.getBlockPos();
		var receiver = XplatAbstractions.INSTANCE.findManaReceiver(entity.level(), hitPos, result.getDirection());
		if (receiver instanceof ManaSpreader spreader) {
			Vec3 tileVec = Vec3.atCenterOf(hitPos);
			Vec3 diffVec = sourceVec.subtract(tileVec);
			Vec3 diffVec2D = new Vec3(diffVec.x, diffVec.z, 0);
			Vec3 rotVec = new Vec3(0, 1, 0);
			double angle = MathHelper.angleBetween(rotVec, diffVec2D) / Math.PI * 180.0;

			if (sourceVec.x < tileVec.x) {
				angle = -angle;
			}

			spreader.setRotationX((float) angle + 90F);

			rotVec = new Vec3(diffVec.x, 0, diffVec.z);
			angle = MathHelper.angleBetween(diffVec, rotVec) * 180F / Math.PI;
			if (sourceVec.y < tileVec.y) {
				angle = -angle;
			}
			spreader.setRotationY((float) angle);

			spreader.commitRedirection();
			if (spreader instanceof ThrottledPacket pkt) {
				pkt.markDispatchable();
			}
		}
	}

}
