package vazkii.botania.xplat;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.ServiceUtil;
import vazkii.botania.api.block.WandHUD;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public interface ClientXplatAbstractions {
	ResourceLocation FLOATING_FLOWER_MODEL_LOADER_ID = botaniaRL("floating_flower");
	ResourceLocation MANA_GUN_MODEL_LOADER_ID = botaniaRL("mana_gun");

	// Event firing
	void fireRenderTinyPotato(BlockEntity potato, Component name, float tickDelta, PoseStack ms, MultiBufferSource buffers, int light, int overlay);

	// Networking
	void sendToServer(CustomPacketPayload packet);

	// Capability access
	@Nullable
	WandHUD findWandHud(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be);

	@Nullable
	WandHUD findWandHud(Entity entity);

	// Rendering stuff
	BakedModel wrapPlatformModel(BakedModel original);
	void setFilterSave(AbstractTexture texture, boolean filter, boolean mipmap);
	void restoreLastFilter(AbstractTexture texture);
	void tessellateBlock(Level level, BlockState state, BlockPos pos, PoseStack ps, MultiBufferSource buffers, int overlay);

	ClientXplatAbstractions INSTANCE = ServiceUtil.findService(ClientXplatAbstractions.class, null);
}
