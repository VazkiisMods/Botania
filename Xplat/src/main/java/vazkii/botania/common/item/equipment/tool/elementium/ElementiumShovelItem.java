/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.tool.elementium;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.annotations.SoftImplement;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.item.equipment.tool.manasteel.ManasteelShovelItem;

public class ElementiumShovelItem extends ManasteelShovelItem {

	public ElementiumShovelItem(Properties props) {
		super(BotaniaAPI.instance().getElementiumItemTier(), props);
	}

	@SoftImplement("IItemExtension")
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
		Level world = player.level();
		BlockState blockState = world.getBlockState(pos);
		if (this.getDestroySpeed(stack, blockState) <= 1.0F) {
			return false;
		}

		Block blk = blockState.getBlock();
		if (blk instanceof FallingBlock) {
			ToolCommons.removeBlocksInIteration(player, stack, world, pos, new Vec3i(0, -12, 0),
					new Vec3i(1, 12, 1),
					state -> state.is(blk));
		}

		return false;
	}

}
