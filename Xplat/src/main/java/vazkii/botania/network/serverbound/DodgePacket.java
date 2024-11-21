/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.network.serverbound;

import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

import io.netty.buffer.ByteBuf;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.bauble.RingOfDexterousMotionItem;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class DodgePacket implements CustomPacketPayload {
	public static final DodgePacket INSTANCE = new DodgePacket();
	public static final Type<DodgePacket> ID = new Type<>(botaniaRL("do"));
	public static final StreamCodec<ByteBuf, DodgePacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<DodgePacket> type() {
		return ID;
	}

	public void handle(MinecraftServer server, ServerPlayer player) {
		server.execute(() -> {
			player.level().playSound(null, player.getX(), player.getY(), player.getZ(), BotaniaSounds.dash, SoundSource.PLAYERS, 1F, 1F);

			ItemStack ringStack = EquipmentHandler.findOrEmpty(BotaniaItems.dodgeRing, player);
			if (ringStack.isEmpty()) {
				player.connection.disconnect(Component.translatable("botaniamisc.invalidDodge"));
				return;
			}

			player.causeFoodExhaustion(0.3F);
			player.getCooldowns().addCooldown(ringStack.getItem(), RingOfDexterousMotionItem.MAX_CD);
		});
	}
}
