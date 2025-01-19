package vazkii.botania.network.serverbound;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import vazkii.botania.common.block.block_entity.corporea.CorporeaIndexBlockEntity;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

import io.netty.buffer.ByteBuf;

public record IndexStringRequestPacket(String message) implements CustomPacketPayload {
	public static final Type<IndexStringRequestPacket> ID = new Type<>(botaniaRL("idxs"));
	public static final StreamCodec<ByteBuf, IndexStringRequestPacket> STREAM_CODEC = ByteBufCodecs.STRING_UTF8
			.map(IndexStringRequestPacket::new, IndexStringRequestPacket::message);

	@Override
	public Type<IndexStringRequestPacket> type() {
		return ID;
	}

	public void handle(MinecraftServer server, ServerPlayer player) {
		server.execute(() -> CorporeaIndexBlockEntity.onChatMessage(player, message()));
	}
}
