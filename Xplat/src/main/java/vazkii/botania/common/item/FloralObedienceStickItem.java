/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.block_entity.BindableSpecialFlowerBlockEntity;
import vazkii.botania.api.block_entity.FunctionalFlowerBlockEntity;
import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;
import vazkii.botania.api.mana.ManaCollector;
import vazkii.botania.api.mana.ManaPool;
import vazkii.botania.common.helper.MathHelper;
import vazkii.botania.xplat.XplatAbstractions;

public class FloralObedienceStickItem extends Item {

	public FloralObedienceStickItem(Properties props) {
		super(props);
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		return applyStick(world, pos)
				? InteractionResult.sidedSuccess(world.isClientSide())
				: InteractionResult.PASS;
	}

	public static boolean applyStick(Level world, BlockPos pos) {
		var receiver = XplatAbstractions.INSTANCE.findManaReceiver(world, pos, null);
		if (receiver instanceof ManaPool || receiver instanceof ManaCollector) {
			int range = receiver instanceof ManaPool ? FunctionalFlowerBlockEntity.LINK_RANGE : GeneratingFlowerBlockEntity.LINK_RANGE;

			for (BlockPos iterPos : BlockPos.betweenClosed(pos.offset(-range, -range, -range), pos.offset(range, range, range))) {
				if (MathHelper.distSqr(iterPos, pos) > range * range) {
					continue;
				}

				BlockEntity tile = world.getBlockEntity(iterPos);
				if (tile instanceof BindableSpecialFlowerBlockEntity<?> bindable && bindable.wouldBeValidBinding(pos)) {
					bindable.setBindingPos(pos);
					WandOfTheForestItem.doParticleBeamWithOffset(world, iterPos, pos);
				}
			}

			return true;
		}
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile instanceof BindableSpecialFlowerBlockEntity<?> bindableFlower) {
			bindableFlower.setBindingPos(null);
			return true;
		}

		return false;
	}
}
