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

import vazkii.botania.client.core.SkyblockWorldInfo;
import vazkii.botania.network.BotaniaPacket;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class GogWorldPacket implements BotaniaPacket {
	public static final GogWorldPacket INSTANCE = new GogWorldPacket();
	public static final ResourceLocation ID = botaniaRL("gog");

	@Override
	public void encode(FriendlyByteBuf buf) {}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	public static GogWorldPacket decode(FriendlyByteBuf buf) {
		return INSTANCE;
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
