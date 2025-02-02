/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.NaturalSpawner;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import vazkii.botania.common.block.flower.generating.NarslimmusBlockEntity;
import vazkii.botania.common.brew.effect.BloodthirstMobEffect;
import vazkii.botania.common.brew.effect.EmptinessMobEffect;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {
	/**
	 * Adds the naturally-spawned tag to slimes
	 */
	@ModifyArg(
		at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"),
		method = "spawnCategoryForPosition(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/NaturalSpawner$SpawnPredicate;Lnet/minecraft/world/level/NaturalSpawner$AfterSpawnCallback;)V"
	)
	private static Entity onSpawned(Entity entity) {
		NarslimmusBlockEntity.onSpawn(entity);
		return entity;
	}

	/**
	 * Prevents spawning when near emptiness users
	 */
	@Inject(at = @At("HEAD"), method = "isValidPositionForMob", cancellable = true)
	private static void emptiness(ServerLevel world, Mob entity, double squaredDistance, CallbackInfoReturnable<Boolean> cir) {
		if (EmptinessMobEffect.shouldCancel(entity)) {
			cir.setReturnValue(false);
		}
	}

	// Jump over entity.checkSpawnRules(pos, reason) and entity.checkSpawnObstruction(pos) under Bloodlust
	@Inject(at = @At("RETURN"), cancellable = true, method = "isValidPositionForMob")
	private static void bloodthirstOverride(ServerLevel world, Mob entity, double distance, CallbackInfoReturnable<Boolean> cir) {
		if (BloodthirstMobEffect.overrideSpawn(world, entity.blockPosition(), entity.getType().getCategory())) {
			cir.setReturnValue(true);
		}
	}
}
