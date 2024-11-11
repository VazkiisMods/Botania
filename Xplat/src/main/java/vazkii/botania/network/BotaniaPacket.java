package vazkii.botania.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import vazkii.botania.api.BotaniaAPI;

public interface BotaniaPacket extends CustomPacketPayload {
	static <T extends CustomPacketPayload> Type<T> createType(String path) {
		return new Type<>(BotaniaAPI.botaniaRL(path));
	}
}
