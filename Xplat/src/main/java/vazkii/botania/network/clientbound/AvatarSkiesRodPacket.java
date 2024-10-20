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

import vazkii.botania.common.item.rod.SkiesRodItem;
import vazkii.botania.network.BotaniaPacket;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public record AvatarSkiesRodPacket(boolean elytra) implements BotaniaPacket {
	public static final ResourceLocation ID = botaniaRL("atr");

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBoolean(elytra);
	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	public static AvatarSkiesRodPacket decode(FriendlyByteBuf buf) {
		return new AvatarSkiesRodPacket(buf.readBoolean());
	}

	public static class Handler {
		public static void handle(AvatarSkiesRodPacket packet) {
			boolean elytra = packet.elytra();
			// Lambda trips verifier on forge
			Minecraft.getInstance().execute(
					new Runnable() {
						@Override
						public void run() {
							var player = Minecraft.getInstance().player;
							var world = Minecraft.getInstance().level;
							if (elytra) {
								SkiesRodItem.doAvatarElytraBoost(player, world);
							} else {
								SkiesRodItem.doAvatarJump(player, world);
							}
						}
					}

			);
		}
	}
}
