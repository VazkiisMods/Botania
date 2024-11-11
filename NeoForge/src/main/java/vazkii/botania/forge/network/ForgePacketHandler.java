package vazkii.botania.forge.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

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

public class ForgePacketHandler {
	public static void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1");

		registrar.playToServer(DodgePacket.ID, DodgePacket.STREAM_CODEC, makeServerBoundHandler(DodgePacket::handle));
		registrar.playToServer(IndexKeybindRequestPacket.ID, IndexKeybindRequestPacket.STREAM_CODEC, makeServerBoundHandler(IndexKeybindRequestPacket::handle));
		registrar.playToServer(IndexStringRequestPacket.ID, IndexStringRequestPacket.STREAM_CODEC, makeServerBoundHandler(IndexStringRequestPacket::handle));
		registrar.playToServer(JumpPacket.ID, JumpPacket.STREAM_CODEC, makeServerBoundHandler(JumpPacket::handle));
		registrar.playToServer(LeftClickPacket.ID, LeftClickPacket.STREAM_CODEC, makeServerBoundHandler(LeftClickPacket::handle));

		registrar.playToClient(AvatarSkiesRodPacket.ID, AvatarSkiesRodPacket.STREAM_CODEC, makeClientBoundHandler(AvatarSkiesRodPacket.Handler::handle));
		registrar.playToClient(BotaniaEffectPacket.ID, BotaniaEffectPacket.STREAM_CODEC, makeClientBoundHandler(BotaniaEffectPacket.Handler::handle));
		registrar.playToClient(GogWorldPacket.ID, GogWorldPacket.STREAM_CODEC, makeClientBoundHandler(GogWorldPacket.Handler::handle));
		registrar.playToClient(ItemAgePacket.ID, ItemAgePacket.STREAM_CODEC, makeClientBoundHandler(ItemAgePacket.Handler::handle));
		registrar.playToClient(SpawnGaiaGuardianPacket.ID, SpawnGaiaGuardianPacket.STREAM_CODEC, makeClientBoundHandler(SpawnGaiaGuardianPacket.Handler::handle));
		registrar.playToClient(UpdateItemsRemainingPacket.ID, UpdateItemsRemainingPacket.STREAM_CODEC, makeClientBoundHandler(UpdateItemsRemainingPacket.Handler::handle));
	}

	private static <T extends CustomPacketPayload> IPayloadHandler<T> makeServerBoundHandler(TriConsumer<T, MinecraftServer, ServerPlayer> handler) {
		return (m, ctx) -> handler.accept(m, ctx.player().getServer(), (ServerPlayer) ctx.player());
	}

	private static <T extends CustomPacketPayload> IPayloadHandler<T> makeClientBoundHandler(Consumer<T> consumer) {
		return (m, ctx) -> consumer.accept(m);
	}
}
