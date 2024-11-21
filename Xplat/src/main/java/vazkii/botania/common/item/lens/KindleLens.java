/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.lens;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import vazkii.botania.api.internal.ManaBurst;

public class KindleLens extends Lens {
	@Override
	public void updateBurst(ManaBurst burst, ItemStack stack) {
		Entity entity = burst.entity();
		if (!entity.level().isClientSide) {
			entity.setRemainingFireTicks(3 * 20);
		}
	}

	@Override
	public boolean collideBurst(ManaBurst burst, HitResult rtr, boolean isManaBlock, boolean shouldKill, ItemStack stack) {
		Entity entity = burst.entity();

		if (!entity.level().isClientSide && rtr.getType() == HitResult.Type.BLOCK
				&& !burst.isFake() && !isManaBlock) {
			BlockHitResult brtr = (BlockHitResult) rtr;
			BlockPos pos = brtr.getBlockPos();
			Direction dir = brtr.getDirection();

			BlockPos offPos = pos.relative(dir);

			BlockState stateAt = entity.level().getBlockState(pos);
			BlockState stateAtOffset = entity.level().getBlockState(offPos);

			if (stateAt.is(Blocks.NETHER_PORTAL)) {
				entity.level().removeBlock(pos, false);
			}
			if (stateAtOffset.is(Blocks.NETHER_PORTAL)) {
				entity.level().removeBlock(offPos, false);
			} else if (BaseFireBlock.canBePlacedAt(entity.level(), offPos, dir.getOpposite())) {
				entity.level().setBlockAndUpdate(offPos, BaseFireBlock.getState(entity.level(), offPos));
			}
		}

		return shouldKill;
	}

}
