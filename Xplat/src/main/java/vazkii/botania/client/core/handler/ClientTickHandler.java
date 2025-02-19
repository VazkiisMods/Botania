/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.core.handler;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import vazkii.botania.client.gui.ItemsRemainingRenderHandler;
import vazkii.botania.client.render.block_entity.RedStringBlockEntityRenderer;
import vazkii.botania.common.block.flower.functional.VinculotusBlockEntity;
import vazkii.botania.common.handler.ManaNetworkHandler;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.item.LexicaBotaniaItem;
import vazkii.botania.common.item.WandOfTheForestItem;

public final class ClientTickHandler {

	private ClientTickHandler() {}

	public static int ticksWithLexicaOpen = 0;
	public static int pageFlipTicks = 0;
	/**
	 * @deprecated Use {@link Minecraft#getTimer()} instead.
	 */
	@Deprecated(forRemoval = true)
	public static int ticksInGame = 0;
	/**
	 * @deprecated Use {@link Minecraft#getTimer()} instead.
	 */
	@Deprecated(forRemoval = true)
	public static float partialTicks;

	/**
	 * @deprecated Use {@link Minecraft#getTimer()} instead.
	 */
	@Deprecated(forRemoval = true)
	public static float total() {
		// TODO not sure if that's really the method to call
		return Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
	}

	public static void clientTickEnd(Minecraft mc) {
		RedStringBlockEntityRenderer.tick();
		ItemsRemainingRenderHandler.tick();

		if (mc.level == null) {
			ManaNetworkHandler.instance.clear();
			VinculotusBlockEntity.existingFlowers.clear();
		}

		if (!mc.isPaused()) {
			ticksInGame++;
			partialTicks = 0;

			Player player = mc.player;
			if (player != null) {
				if (PlayerHelper.hasHeldItemClass(player, WandOfTheForestItem.class)) {
					for (var collector : ImmutableList.copyOf(ManaNetworkHandler.instance.getAllCollectorsInWorld(Minecraft.getInstance().level))) {
						collector.onClientDisplayTick();
					}
				}
			}
		}

		int ticksToOpen = 10;
		if (LexicaBotaniaItem.isOpen()) {
			if (ticksWithLexicaOpen < 0) {
				ticksWithLexicaOpen = 0;
			}
			if (ticksWithLexicaOpen < ticksToOpen) {
				ticksWithLexicaOpen++;
			}
			if (pageFlipTicks > 0) {
				pageFlipTicks--;
			}
		} else {
			pageFlipTicks = 0;
			if (ticksWithLexicaOpen > 0) {
				if (ticksWithLexicaOpen > ticksToOpen) {
					ticksWithLexicaOpen = ticksToOpen;
				}
				ticksWithLexicaOpen--;
			}
		}
	}

	public static void notifyPageChange() {
		if (pageFlipTicks == 0) {
			pageFlipTicks = 5;
		}
	}

}
