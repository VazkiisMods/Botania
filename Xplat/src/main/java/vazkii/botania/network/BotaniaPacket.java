package vazkii.botania.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import io.netty.buffer.ByteBuf;
import vazkii.botania.api.BotaniaAPI;

public interface BotaniaPacket<B extends ByteBuf, T extends BotaniaPacket<B, T>> extends CustomPacketPayload {
	static <T extends CustomPacketPayload> Type<T> createType(String path) {
		return new Type<>(BotaniaAPI.botaniaRL(path));
	}
}
