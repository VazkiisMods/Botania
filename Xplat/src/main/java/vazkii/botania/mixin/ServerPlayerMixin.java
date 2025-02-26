/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.world.SkyblockWorldEvents;
import vazkii.botania.xplat.XplatAbstractions;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
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

	@Mixin(targets = "net/minecraft/server/level/ServerPlayer$2")
	public static class ContainerListenerMixin {
		@Unique
		private boolean botania$seenManaTablet;

		@Inject(at = @At("HEAD"), method = "slotChanged", cancellable = true)
		private void onSlotChanged(AbstractContainerMenu containerMenu, int slotIndex, ItemStack itemStack, CallbackInfo ci) {
			if (!botania$seenManaTablet && itemStack.is(BotaniaItems.manaTablet)) {
				// This might be the player's first mana tablet - let recipe advancement triggers run
				botania$seenManaTablet = true;
				return;
			}
			// ManaItems update frequently - skip advancement processing for them
			if (XplatAbstractions.INSTANCE.findManaItem(itemStack) != null) {
				ci.cancel();
			}
		}
	}
}
