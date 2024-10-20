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
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import vazkii.botania.common.entity.GaiaGuardianEntity;
import vazkii.botania.network.BotaniaPacket;

import java.util.UUID;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public record SpawnGaiaGuardianPacket(ClientboundAddEntityPacket inner, int playerCount, boolean hardMode,
		BlockPos source, UUID bossInfoId) implements BotaniaPacket {

	public static final ResourceLocation ID = botaniaRL("spg");

	@Override
	public void encode(FriendlyByteBuf buf) {
		inner().write(buf);
		buf.writeVarInt(playerCount());
		buf.writeBoolean(hardMode());
		buf.writeBlockPos(source());
		buf.writeUUID(bossInfoId());
	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	public static SpawnGaiaGuardianPacket decode(FriendlyByteBuf buf) {
		return new SpawnGaiaGuardianPacket(
				new ClientboundAddEntityPacket(buf),
				buf.readVarInt(),
				buf.readBoolean(),
				buf.readBlockPos(),
				buf.readUUID()
		);
	}

	public static class Handler {
		public static void handle(SpawnGaiaGuardianPacket packet) {
			var inner = packet.inner();
			int playerCount = packet.playerCount();
			boolean hardMode = packet.hardMode();
			BlockPos source = packet.source();
			UUID bossInfoUuid = packet.bossInfoId();

			Minecraft.getInstance().execute(() -> {
				var player = Minecraft.getInstance().player;
				if (player != null) {
					player.connection.handleAddEntity(inner);
					Entity e = player.level().getEntity(inner.getId());
					if (e instanceof GaiaGuardianEntity dopple) {
						dopple.readSpawnData(playerCount, hardMode, source, bossInfoUuid);
					}
				}
			});
		}
	}
}
