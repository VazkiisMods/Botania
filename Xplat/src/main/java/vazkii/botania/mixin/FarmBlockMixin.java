/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;

import vazkii.botania.common.block.decor.FloatingFlowerBlock;

@Mixin(FarmBlock.class)
public class FarmBlockMixin {
	@WrapOperation(method = "canSurvive", constant = @Constant(classValue = MovingPistonBlock.class))
	private boolean floatingFlowerOverride(Object object, Operation<Boolean> original, @Local(ordinal = 1) BlockState stateAbove) {
		return original.call(object) || stateAbove.getBlock() instanceof FloatingFlowerBlock;
	}
}
