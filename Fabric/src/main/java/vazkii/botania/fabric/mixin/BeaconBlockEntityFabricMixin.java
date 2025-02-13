/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.PermanentBifrostBlock;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityFabricMixin {
	@Unique
	private static boolean bifrost = false;

	@ModifyVariable(method = "tick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"), argsOnly = true)
	private static BlockState captureBifrost(BlockState obj) {
		bifrost = obj == BotaniaBlocks.bifrostPerm.defaultBlockState();
		return obj;
	}

	@ModifyVariable(method = "tick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/DyeColor;getTextureDiffuseColor()I"), argsOnly = true)
	private static int bifrostColor(int obj, Level level) {
		if (bifrost) {
			return ((PermanentBifrostBlock) BotaniaBlocks.bifrostPerm).getBeaconColorMultiplier(
					BotaniaBlocks.bifrostPerm.defaultBlockState(),
					level, BlockPos.ZERO, BlockPos.ZERO);
		}
		return obj;
	}
}
