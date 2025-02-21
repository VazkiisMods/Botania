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

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.botania.common.BotaniaStats;
import vazkii.botania.common.block.block_entity.LuminizerBlockEntity;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.world.SkyblockWorldEvents;
import vazkii.botania.xplat.XplatAbstractions;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
	/**
	 * Updates the distance by luminizer stat
	 */
	@WrapOperation(
		method = "checkRidingStatistics", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerPlayer;getVehicle()Lnet/minecraft/world/entity/Entity;"
		)
	)
	private Entity trackLuminizerTravel(ServerPlayer player, Operation<Entity> original, @Local int cm) {
		Entity entity = original.call(player);
		if (entity instanceof LuminizerBlockEntity.PlayerMoverEntity) {
			player.awardStat(BotaniaStats.LUMINIZER_ONE_CM, cm);
		}
		return entity;
	}

	/**
	 * Setups up a player when spawning into a GoG world for the first time
	 */
	@Inject(at = @At("RETURN"), method = "initMenu")
	private void onLogin(CallbackInfo ci) {
		if (XplatAbstractions.INSTANCE.gogLoaded()) {
			SkyblockWorldEvents.onPlayerJoin((ServerPlayer) (Object) this);
		}
	}

	@Inject(at = @At("RETURN"), method = "tick")
	private void onTick(CallbackInfo ci) {
		if (EquipmentHandler.instance instanceof EquipmentHandler.InventoryEquipmentHandler) {
			((EquipmentHandler.InventoryEquipmentHandler) EquipmentHandler.instance).onPlayerTick((Player) (Object) this);
		}
	}

}
