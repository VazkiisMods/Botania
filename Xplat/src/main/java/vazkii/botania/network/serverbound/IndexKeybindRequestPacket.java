/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.network.serverbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.common.block.block_entity.corporea.CorporeaIndexBlockEntity;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public record IndexKeybindRequestPacket(ItemStack stack) implements CustomPacketPayload {
	public static final Type<IndexKeybindRequestPacket> ID = new Type<>(botaniaRL("idx"));
	public static final StreamCodec<RegistryFriendlyByteBuf, IndexKeybindRequestPacket> STREAM_CODEC = ItemStack.STREAM_CODEC
			.map(IndexKeybindRequestPacket::new, IndexKeybindRequestPacket::stack);

	@Override
	public Type<IndexKeybindRequestPacket> type() {
		return ID;
	}

	public void handle(MinecraftServer server, ServerPlayer player) {
		var stack = this.stack();
		server.execute(() -> {
			if (player.isSpectator()) {
				return;
			}

			for (CorporeaIndexBlockEntity index : CorporeaIndexBlockEntity.getNearbyValidIndexes(player)) {
				index.performPlayerRequest(player, CorporeaHelper.instance().createMatcher(stack, true), stack.getCount());
			}
		});
	}
}
