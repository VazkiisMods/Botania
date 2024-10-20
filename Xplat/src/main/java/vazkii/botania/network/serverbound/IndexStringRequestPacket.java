package vazkii.botania.network.serverbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import vazkii.botania.common.block.block_entity.corporea.CorporeaIndexBlockEntity;
import vazkii.botania.network.BotaniaPacket;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public record IndexStringRequestPacket(String message) implements BotaniaPacket {
	public static final ResourceLocation ID = botaniaRL("idxs");

	public static IndexStringRequestPacket decode(FriendlyByteBuf buf) {
		return new IndexStringRequestPacket(buf.readUtf());
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(message);
	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	public void handle(MinecraftServer server, ServerPlayer player) {
		server.execute(() -> CorporeaIndexBlockEntity.onChatMessage(player, message()));
	}
}
