/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity.red_string;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.api.block_entity.SpecialFlowerBlockEntity;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;

public class RedStringSpooferBlockEntity extends RedStringBlockEntity {
	public RedStringSpooferBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.RED_STRING_RELAY, pos, state);
	}

	@Override
	public boolean acceptBlock(BlockPos pos) {
		if (pos.equals(getBlockPos().above())) {
			return false;
		}

		Block block = level.getBlockState(pos).getBlock();
		if (isValidPlant(block)) {
			BlockEntity tile = level.getBlockEntity(pos);
			return !(tile instanceof SpecialFlowerBlockEntity);
		}
		return false;

	}

	private static boolean isValidPlant(Block block) {
		if (block instanceof FlowerPotBlock flowerPot) {
			block = flowerPot.getPotted();
		}
		return block instanceof FlowerBlock || block instanceof MushroomBlock || block instanceof FungusBlock || block instanceof DoublePlantBlock;
	}

}
