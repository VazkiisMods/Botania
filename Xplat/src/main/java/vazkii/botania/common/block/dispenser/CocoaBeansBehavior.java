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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

public class CocoaBeansBehavior extends OptionalDispenseItemBehavior {
	@NotNull
	@Override
	public ItemStack execute(BlockSource source, ItemStack stack) {
		Block block = Blocks.COCOA;
		Direction facing = source.state().getValue(DispenserBlock.FACING);
		BlockPos pos = source.pos().relative(facing);
		Level world = source.level();
		BlockPlaceContext ctx = new DirectionalPlaceContext(source.level(), source.pos().relative(facing), facing, new ItemStack(block), facing.getOpposite());
		BlockState cocoa = block.getStateForPlacement(ctx);
		if (cocoa != null && world.isEmptyBlock(pos)) {
			world.setBlockAndUpdate(pos, cocoa);
			stack.shrink(1);
			return stack;
		}

		return stack;
	}

}
