/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import vazkii.botania.common.block.PermanentBifrostBlock;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityFabricMixin {
	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/DyeColor;getTextureDiffuseColor()I"))
	private static int getBifrostColor(DyeColor instance, Operation<Integer> original,
			@Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos beaconPos,
			@Local(ordinal = 1) BlockState blockState, @Local Block block, @Local(ordinal = 1) BlockPos blockPos) {
		return block instanceof PermanentBifrostBlock bifrostBlock
				? bifrostBlock.getBeaconColorMultiplier(blockState, level, blockPos, beaconPos)
				: original.call(instance);
	}
}
