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
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
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
		Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
		Level world = source.getLevel();
		double x = source.x() + (double) direction.getStepX() * 1.125;
		double y = Math.floor(source.y()) + (double) direction.getStepY();
		double z = source.z() + (double) direction.getStepZ() * 1.125;
		BlockPos blockpos = source.getPos().relative(direction);
		BlockState blockState = world.getBlockState(blockpos);
		RailShape railShape = blockState.getBlock() instanceof BaseRailBlock
				? blockState.getValue(((BaseRailBlock) blockState.getBlock()).getShapeProperty())
				: RailShape.NORTH_SOUTH;
		double yOffset;
		if (blockState.is(BlockTags.RAILS)) {
			if (railShape.isAscending()) {
				yOffset = 0.6;
			} else {
				yOffset = 0.1;
			}
		} else {
			if (!blockState.isAir() || !world.getBlockState(blockpos.below()).is(BlockTags.RAILS)) {
				return this.behaviourDefaultDispenseItem.dispense(source, stack);
			}

			BlockState blockStateBelow = world.getBlockState(blockpos.below());
			RailShape railShapeBelow = blockStateBelow.getBlock() instanceof BaseRailBlock
					? blockStateBelow.getValue(((BaseRailBlock) blockStateBelow.getBlock()).getShapeProperty())
					: RailShape.NORTH_SOUTH;
			if (direction != Direction.DOWN && railShapeBelow.isAscending()) {
				yOffset = -0.4;
			} else {
				yOffset = -0.9;
			}
		}

		// changed from vanilla, because it uses AbstractMinecart.Type enum to resolve the entity type
		AbstractMinecart minecart = new ManaPoolMinecartEntity(world, x, y + yOffset, z);
		if (stack.hasCustomHoverName()) {
			minecart.setCustomName(stack.getHoverName());
		}

		world.addFreshEntity(minecart);
		stack.shrink(1);
		return stack;
	}

	@Override
	protected void playSound(BlockSource source) {
		source.getLevel().levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, source.getPos(), 0);
	}

}
