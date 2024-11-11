/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.network.serverbound;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import io.netty.buffer.ByteBuf;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraBladeItem;
import vazkii.botania.network.BotaniaPacket;

public class LeftClickPacket implements BotaniaPacket<ByteBuf, LeftClickPacket> {
	public static final LeftClickPacket INSTANCE = new LeftClickPacket();
	public static final Type<LeftClickPacket> ID = BotaniaPacket.createType("lc");
	public static final StreamCodec<ByteBuf, LeftClickPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<LeftClickPacket> type() {
		return ID;
	}

	public void handle(MinecraftServer server, ServerPlayer player) {
		// The swing packet will run on the netty thread immediately,
		// so we need to fetch the attack strength ahead of time
		float scale = player.getAttackStrengthScale(0F);
		server.execute(() -> TerraBladeItem.trySpawnBurst(player, scale));
	}
}
