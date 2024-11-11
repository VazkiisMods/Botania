/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.network.serverbound;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import io.netty.buffer.ByteBuf;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.item.equipment.bauble.CirrusAmuletItem;
import vazkii.botania.network.BotaniaPacket;

public class JumpPacket implements BotaniaPacket {
	public static final JumpPacket INSTANCE = new JumpPacket();
	public static final Type<JumpPacket> ID = BotaniaPacket.createType("jmp");
	public static final StreamCodec<ByteBuf, JumpPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<JumpPacket> type() {
		return ID;
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
