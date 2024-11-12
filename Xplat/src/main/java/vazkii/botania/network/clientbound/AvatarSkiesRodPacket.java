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

import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import vazkii.botania.common.item.rod.SkiesRodItem;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public record AvatarSkiesRodPacket(boolean elytra) implements CustomPacketPayload {
	public static final Type<AvatarSkiesRodPacket> ID = new Type<>(botaniaRL("atr"));
	public static final StreamCodec<ByteBuf, AvatarSkiesRodPacket> STREAM_CODEC = ByteBufCodecs.BOOL
			.map(AvatarSkiesRodPacket::new, AvatarSkiesRodPacket::elytra);

	@Override
	public Type<AvatarSkiesRodPacket> type() {
		return ID;
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
