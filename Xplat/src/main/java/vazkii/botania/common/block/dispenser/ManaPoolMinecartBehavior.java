/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.entity.ManaPoolMinecartEntity;

// [VanillaCopy] MinecartItem
public class ManaPoolMinecartBehavior extends DefaultDispenseItemBehavior {
	private final DefaultDispenseItemBehavior behaviourDefaultDispenseItem = new DefaultDispenseItemBehavior();

	@NotNull
	@Override
	public ItemStack execute(BlockSource source, ItemStack stack) {
		Direction enumfacing = source.state().getValue(DispenserBlock.FACING);
		Level world = source.level();
		double d0 = source.center().x() + (double) enumfacing.getStepX() * 1.125D;
		double d1 = Math.floor(source.center().y()) + (double) enumfacing.getStepY();
		double d2 = source.center().z() + (double) enumfacing.getStepZ() * 1.125D;
		BlockPos blockpos = source.pos().relative(enumfacing);
		BlockState iblockstate = world.getBlockState(blockpos);
		RailShape railshape = iblockstate.getBlock() instanceof BaseRailBlock ? iblockstate.getValue(((BaseRailBlock) iblockstate.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
		double d3;
		if (iblockstate.is(BlockTags.RAILS)) {
			if (railshape.isAscending()) {
				d3 = 0.6D;
			} else {
				d3 = 0.1D;
			}
		} else {
			if (!iblockstate.isAir() || !world.getBlockState(blockpos.below()).is(BlockTags.RAILS)) {
				return this.behaviourDefaultDispenseItem.dispense(source, stack);
			}

			BlockState iblockstate1 = world.getBlockState(blockpos.below());
			RailShape railshape1 = iblockstate1.getBlock() instanceof BaseRailBlock ? iblockstate1.getValue(((BaseRailBlock) iblockstate1.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
			if (enumfacing != Direction.DOWN && railshape1.isAscending()) {
				d3 = -0.4D;
			} else {
				d3 = -0.9D;
			}
		}

		AbstractMinecart entityminecart = new ManaPoolMinecartEntity(world, d0, d1 + d3, d2);
		if (stack.hasCustomHoverName()) {
			entityminecart.setCustomName(stack.getHoverName());
		}

		world.addFreshEntity(entityminecart);
		stack.shrink(1);
		return stack;
	}

	@Override
	protected void playSound(BlockSource source) {
		source.level().levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, source.pos(), 0);
	}

}
