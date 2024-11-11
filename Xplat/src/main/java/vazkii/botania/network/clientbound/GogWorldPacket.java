/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;

import io.netty.buffer.ByteBuf;
import vazkii.botania.client.core.SkyblockWorldInfo;
import vazkii.botania.network.BotaniaPacket;

public class GogWorldPacket implements BotaniaPacket<ByteBuf, GogWorldPacket> {
	public static final GogWorldPacket INSTANCE = new GogWorldPacket();
	public static final Type<GogWorldPacket> ID = BotaniaPacket.createType("gog");
	public static final StreamCodec<ByteBuf, GogWorldPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<GogWorldPacket> type() {
		return ID;
	}

	public static class Handler {
		public static void handle(GogWorldPacket packet) {
			Minecraft.getInstance().execute(() -> {
				if (Minecraft.getInstance().level.getLevelData() instanceof SkyblockWorldInfo skyblockInfo) {
					skyblockInfo.markGardenOfGlass();
				}
			});
		}
	}
}
