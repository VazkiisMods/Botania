/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.mixin;

import com.mojang.authlib.GameProfile;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import vazkii.botania.common.BotaniaStats;
import vazkii.botania.common.entity.BotaniaEntities;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.world.SkyblockWorldEvents;
import vazkii.botania.xplat.XplatAbstractions;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
	protected ServerPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
		super(level, pos, yRot, gameProfile);
	}

	/**
	 * Updates the distance by luminizer stat
	 */
	@Inject(
		at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/level/ServerPlayer;getVehicle()Lnet/minecraft/world/entity/Entity;"),
		method = "checkRidingStatistics", locals = LocalCapture.CAPTURE_FAILSOFT
	)
	private void trackLuminizerTravel(double dx, double dy, double dz, CallbackInfo ci, int cm, Entity mount) {
		if (mount.getType() == BotaniaEntities.PLAYER_MOVER) {
			awardStat(BotaniaStats.LUMINIZER_ONE_CM, cm);
		}
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
