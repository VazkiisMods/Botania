package vazkii.botania.forge.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;

import vazkii.botania.network.TriConsumer;
import vazkii.botania.network.clientbound.*;
import vazkii.botania.network.serverbound.*;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class ForgePacketHandler {
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			prefix("main"),
			// We don't use this
			() -> "0",
			"0"::equals,
			"0"::equals);

	public static void init() {
		int i = 0;
		// Serverbound
		CHANNEL.registerMessage(i++, DodgePacket.class, DodgePacket::encode, DodgePacket::decode,
				makeServerBoundHandler(DodgePacket::handle));
		CHANNEL.registerMessage(i++, IndexKeybindRequestPacket.class, IndexKeybindRequestPacket::encode, IndexKeybindRequestPacket::decode,
				makeServerBoundHandler(IndexKeybindRequestPacket::handle));
		CHANNEL.registerMessage(i++, IndexStringRequestPacket.class, IndexStringRequestPacket::encode, IndexStringRequestPacket::decode,
				makeServerBoundHandler(IndexStringRequestPacket::handle));
		CHANNEL.registerMessage(i++, JumpPacket.class, JumpPacket::encode, JumpPacket::decode,
				makeServerBoundHandler(JumpPacket::handle));
		CHANNEL.registerMessage(i++, LeftClickPacket.class, LeftClickPacket::encode, LeftClickPacket::decode,
				makeServerBoundHandler(LeftClickPacket::handle));

		// Clientbound
		CHANNEL.registerMessage(i++, AvatarSkiesRodPacket.class, AvatarSkiesRodPacket::encode, AvatarSkiesRodPacket::decode,
				makeClientBoundHandler(AvatarSkiesRodPacket.Handler::handle));
		CHANNEL.registerMessage(i++, BotaniaEffectPacket.class, BotaniaEffectPacket::encode, BotaniaEffectPacket::decode,
				makeClientBoundHandler(BotaniaEffectPacket.Handler::handle));
		CHANNEL.registerMessage(i++, GogWorldPacket.class, GogWorldPacket::encode, GogWorldPacket::decode,
				makeClientBoundHandler(GogWorldPacket.Handler::handle));
		CHANNEL.registerMessage(i++, ItemAgePacket.class, ItemAgePacket::encode, ItemAgePacket::decode,
				makeClientBoundHandler(ItemAgePacket.Handler::handle));
		CHANNEL.registerMessage(i++, SpawnGaiaGuardianPacket.class, SpawnGaiaGuardianPacket::encode, SpawnGaiaGuardianPacket::decode,
				makeClientBoundHandler(SpawnGaiaGuardianPacket.Handler::handle));
		CHANNEL.registerMessage(i++, UpdateItemsRemainingPacket.class, UpdateItemsRemainingPacket::encode, UpdateItemsRemainingPacket::decode,
				makeClientBoundHandler(UpdateItemsRemainingPacket.Handler::handle));
	}

	private static <T> BiConsumer<T, Supplier<NetworkEvent.Context>> makeServerBoundHandler(TriConsumer<T, MinecraftServer, ServerPlayer> handler) {
		return (m, ctx) -> {
			handler.accept(m, ctx.get().getSender().getServer(), ctx.get().getSender());
			ctx.get().setPacketHandled(true);
		};
	}

	private static <T> BiConsumer<T, Supplier<NetworkEvent.Context>> makeClientBoundHandler(Consumer<T> consumer) {
		return (m, ctx) -> {
			consumer.accept(m);
			ctx.get().setPacketHandled(true);
		};
	}
}
