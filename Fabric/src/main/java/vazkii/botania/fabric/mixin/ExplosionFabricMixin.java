/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.mixin;

import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.botania.common.item.equipment.bauble.BenevolentGoddessCharmItem;

@Mixin(Explosion.class)
public abstract class ExplosionFabricMixin {
	@Shadow
	@Final
	private Level level;

	@Shadow
	@Final
	private double x;

	@Shadow
	@Final
	private double y;

	@Shadow
	@Final
	private double z;

	@Shadow
	public abstract void clearToBlow();

	@Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
	private void onAffectWorld(CallbackInfo ci) {
		if (BenevolentGoddessCharmItem.shouldProtectExplosion(level, new Vec3(x, y, z))) {
			clearToBlow();
		}
	}
}
