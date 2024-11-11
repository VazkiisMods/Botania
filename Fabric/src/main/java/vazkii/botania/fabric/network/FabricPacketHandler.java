/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import vazkii.botania.network.TriConsumer;
import vazkii.botania.network.clientbound.AvatarSkiesRodPacket;
import vazkii.botania.network.clientbound.BotaniaEffectPacket;
import vazkii.botania.network.clientbound.GogWorldPacket;
import vazkii.botania.network.clientbound.ItemAgePacket;
import vazkii.botania.network.clientbound.SpawnGaiaGuardianPacket;
import vazkii.botania.network.clientbound.UpdateItemsRemainingPacket;
import vazkii.botania.network.serverbound.DodgePacket;
import vazkii.botania.network.serverbound.IndexKeybindRequestPacket;
import vazkii.botania.network.serverbound.IndexStringRequestPacket;
import vazkii.botania.network.serverbound.JumpPacket;
import vazkii.botania.network.serverbound.LeftClickPacket;

import java.util.function.Consumer;

public final class FabricPacketHandler {
	public static void init() {
		PayloadTypeRegistry.playC2S().register(DodgePacket.ID, DodgePacket.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(IndexKeybindRequestPacket.ID, IndexKeybindRequestPacket.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(IndexStringRequestPacket.ID, IndexStringRequestPacket.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(JumpPacket.ID, JumpPacket.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(LeftClickPacket.ID, LeftClickPacket.STREAM_CODEC);

		PayloadTypeRegistry.playS2C().register(AvatarSkiesRodPacket.ID, AvatarSkiesRodPacket.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(BotaniaEffectPacket.ID, BotaniaEffectPacket.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(GogWorldPacket.ID, GogWorldPacket.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ItemAgePacket.ID, ItemAgePacket.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(SpawnGaiaGuardianPacket.ID, SpawnGaiaGuardianPacket.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(UpdateItemsRemainingPacket.ID, UpdateItemsRemainingPacket.STREAM_CODEC);

		ServerPlayNetworking.registerGlobalReceiver(DodgePacket.ID, makeServerBoundHandler(DodgePacket::handle));
		ServerPlayNetworking.registerGlobalReceiver(IndexKeybindRequestPacket.ID, makeServerBoundHandler(IndexKeybindRequestPacket::handle));
		ServerPlayNetworking.registerGlobalReceiver(IndexStringRequestPacket.ID, makeServerBoundHandler(IndexStringRequestPacket::handle));
		ServerPlayNetworking.registerGlobalReceiver(JumpPacket.ID, makeServerBoundHandler(JumpPacket::handle));
		ServerPlayNetworking.registerGlobalReceiver(LeftClickPacket.ID, makeServerBoundHandler(LeftClickPacket::handle));
	}

	private static <T extends CustomPacketPayload> ServerPlayNetworking.PlayPayloadHandler<T> makeServerBoundHandler(TriConsumer<T, MinecraftServer, ServerPlayer> handle) {
		return (payload, context) -> handle.accept(payload, context.server(), context.player());
	}

	public static void initClient() {
		ClientPlayNetworking.registerGlobalReceiver(AvatarSkiesRodPacket.ID, makeClientBoundHandler(AvatarSkiesRodPacket.Handler::handle));
		ClientPlayNetworking.registerGlobalReceiver(BotaniaEffectPacket.ID, makeClientBoundHandler(BotaniaEffectPacket.Handler::handle));
		ClientPlayNetworking.registerGlobalReceiver(GogWorldPacket.ID, makeClientBoundHandler(GogWorldPacket.Handler::handle));
		ClientPlayNetworking.registerGlobalReceiver(ItemAgePacket.ID, makeClientBoundHandler(ItemAgePacket.Handler::handle));
		ClientPlayNetworking.registerGlobalReceiver(SpawnGaiaGuardianPacket.ID, makeClientBoundHandler(SpawnGaiaGuardianPacket.Handler::handle));
		ClientPlayNetworking.registerGlobalReceiver(UpdateItemsRemainingPacket.ID, makeClientBoundHandler(UpdateItemsRemainingPacket.Handler::handle));
	}

	private static <T extends CustomPacketPayload> ClientPlayNetworking.PlayPayloadHandler<T> makeClientBoundHandler(Consumer<T> handler) {
		return (payload, context) -> handler.accept(payload);
	}

	private FabricPacketHandler() {}

}
