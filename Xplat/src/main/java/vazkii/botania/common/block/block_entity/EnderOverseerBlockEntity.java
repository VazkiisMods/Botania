/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import vazkii.botania.common.item.equipment.tool.ToolCommons;

import java.util.List;

public class EnderOverseerBlockEntity extends BotaniaBlockEntity {
	public EnderOverseerBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.ENDER_EYE, pos, state);
	}

	public static void serverTick(Level level, BlockPos worldPosition, BlockState state, EnderOverseerBlockEntity self) {
		boolean wasLooking = state.getValue(BlockStateProperties.POWERED);
		int range = 80;
		List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(worldPosition).inflate(range));

		boolean looking = false;
		for (Player player : players) {
			ItemStack helm = player.getItemBySlot(EquipmentSlot.HEAD);
			if (!helm.isEmpty() && helm.is(Blocks.CARVED_PUMPKIN.asItem())) {
				continue;
			}

			BlockHitResult hit = ToolCommons.raytraceFromEntity(player, 64, false);
			if (hit.getType() == HitResult.Type.BLOCK && hit.getBlockPos().equals(worldPosition)) {
				looking = true;
				break;
			}
		}

		if (looking != wasLooking) {
			level.setBlockAndUpdate(worldPosition, state.setValue(BlockStateProperties.POWERED, looking));
		}
	}

}
