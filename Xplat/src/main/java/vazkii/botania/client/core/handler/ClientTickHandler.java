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

	public static final int TICKS_TO_OPEN = 10;

	private ClientTickHandler() {}

	public static int ticksWithLexicaOpen = 0;
	public static int pageFlipTicks = 0;

	// TODO: Reconsider if this is really a good idea to have in the first place.
	// Often this is combined with the current partial tick time, which may lead to precision loss if the world has
	// been open on the client for a long time.
	public static int ticksInGame = 0;

	public static void clientTickEnd(Minecraft mc) {
		RedStringBlockEntityRenderer.tick();
		ItemsRemainingRenderHandler.tick();

		if (mc.level == null) {
			ManaNetworkHandler.instance.clear();
			VinculotusBlockEntity.existingFlowers.clear();
		}

		if (!mc.isPaused()) {
			ticksInGame++;

			Player player = mc.player;
			if (player != null) {
				if (PlayerHelper.hasHeldItemClass(player, WandOfTheForestItem.class)) {
					for (var collector : ImmutableList.copyOf(ManaNetworkHandler.instance.getAllCollectorsInWorld(Minecraft.getInstance().level))) {
						collector.onClientDisplayTick();
					}
				}
			}
		}

		if (LexicaBotaniaItem.isOpen()) {
			if (ticksWithLexicaOpen < 0) {
				ticksWithLexicaOpen = 0;
			}
			if (ticksWithLexicaOpen < TICKS_TO_OPEN) {
				ticksWithLexicaOpen++;
			}
			if (pageFlipTicks > 0) {
				pageFlipTicks--;
			}
		} else {
			pageFlipTicks = 0;
			if (ticksWithLexicaOpen > 0) {
				if (ticksWithLexicaOpen > TICKS_TO_OPEN) {
					ticksWithLexicaOpen = TICKS_TO_OPEN;
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
