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
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.block.Wandable;
import vazkii.botania.xplat.XplatAbstractions;

public class WandBehavior extends OptionalDispenseItemBehavior {

	@NotNull
	@Override
	protected ItemStack execute(BlockSource source, ItemStack stack) {
		Level world = source.level();
		Direction facing = world.getBlockState(source.pos()).getValue(DispenserBlock.FACING);
		BlockPos pos = source.pos().relative(facing);
		BlockState state = world.getBlockState(pos);
		Wandable wandable = XplatAbstractions.INSTANCE.findWandable(world, pos, state, world.getBlockEntity(pos));
		setSuccess(wandable != null && wandable.onUsedByWand(null, stack, facing.getOpposite()));
		return stack;
	}

}
