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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

import vazkii.botania.network.BotaniaPacket;
import vazkii.botania.xplat.XplatAbstractions;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public record ItemAgePacket(int entityId, int timeCounter) implements BotaniaPacket {

	public static final ResourceLocation ID = botaniaRL("ia");

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeVarInt(entityId());
		buf.writeVarInt(timeCounter());
	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	public static ItemAgePacket decode(FriendlyByteBuf buf) {
		return new ItemAgePacket(buf.readVarInt(), buf.readVarInt());
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
