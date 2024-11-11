package vazkii.botania.network.serverbound;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import io.netty.buffer.ByteBuf;
import vazkii.botania.common.block.block_entity.corporea.CorporeaIndexBlockEntity;
import vazkii.botania.network.BotaniaPacket;

public record IndexStringRequestPacket(String message) implements BotaniaPacket<ByteBuf, IndexStringRequestPacket> {
	public static final Type<IndexStringRequestPacket> ID = BotaniaPacket.createType("idxs");
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
