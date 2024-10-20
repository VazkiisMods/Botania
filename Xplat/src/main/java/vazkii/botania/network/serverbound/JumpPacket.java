/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.network.serverbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.item.equipment.bauble.CirrusAmuletItem;
import vazkii.botania.network.BotaniaPacket;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class JumpPacket implements BotaniaPacket {
	public static final JumpPacket INSTANCE = new JumpPacket();
	public static final ResourceLocation ID = botaniaRL("jmp");

	@Override
	public void encode(FriendlyByteBuf buf) {

	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	public static JumpPacket decode(FriendlyByteBuf buf) {
		return INSTANCE;
	}

	public void handle(MinecraftServer server, ServerPlayer player) {
		server.execute(() -> {
			ItemStack amuletStack = EquipmentHandler.findOrEmpty(s -> s.getItem() instanceof CirrusAmuletItem, player);
			if (!amuletStack.isEmpty()) {
				player.causeFoodExhaustion(0.3F);
				player.fallDistance = 0;

				CirrusAmuletItem.setJumping(player);
			}
		});
	}
}
