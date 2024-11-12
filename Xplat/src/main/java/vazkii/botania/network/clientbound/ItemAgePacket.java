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
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

import io.netty.buffer.ByteBuf;
import vazkii.botania.xplat.XplatAbstractions;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public record ItemAgePacket(int entityId, int timeCounter) implements CustomPacketPayload {

	public static final Type<ItemAgePacket> ID = new Type<>(botaniaRL("ia"));
	public static final StreamCodec<ByteBuf, ItemAgePacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, ItemAgePacket::entityId,
			ByteBufCodecs.VAR_INT, ItemAgePacket::timeCounter,
			ItemAgePacket::new
	);

	@Override
	public Type<ItemAgePacket> type() {
		return ID;
	}

	public static class Handler {
		public static void handle(ItemAgePacket packet) {
			int entityId = packet.entityId();
			int counter = packet.timeCounter();
			Minecraft.getInstance().execute(() -> {
				Entity e = Minecraft.getInstance().level.getEntity(entityId);
				if (e instanceof ItemEntity item) {
					XplatAbstractions.INSTANCE.itemFlagsComponent(item).timeCounter = counter;
				}
			});
		}
	}

}
