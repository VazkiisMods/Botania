package vazkii.botania.forge.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.MinecraftForge;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.BotaniaForgeClientCapabilities;
import vazkii.botania.api.block.WandHUD;
import vazkii.botania.api.item.TinyPotatoRenderEvent;
import vazkii.botania.forge.CapabilityUtil;
import vazkii.botania.forge.network.ForgePacketHandler;
import vazkii.botania.network.BotaniaPacket;
import vazkii.botania.xplat.ClientXplatAbstractions;

public class ForgeClientXplatImpl implements ClientXplatAbstractions {
	@Override
	public void fireRenderTinyPotato(BlockEntity potato, Component name, float tickDelta, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		MinecraftForge.EVENT_BUS.post(new TinyPotatoRenderEvent(potato, name, tickDelta, ms, buffers, light, overlay));
	}

	@Override
	public void sendToServer(BotaniaPacket packet) {
		ForgePacketHandler.CHANNEL.sendToServer(packet);
	}

	@Nullable
	@Override
	public WandHUD findWandHud(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
		return CapabilityUtil.findCapability(BotaniaForgeClientCapabilities.WAND_HUD, level, pos, state, be);
	}

	@Nullable
	@Override
	public WandHUD findWandHud(Entity entity) {
		return entity.getCapability(BotaniaForgeClientCapabilities.WAND_HUD).orElse(null);
	}

	@Override
	public BakedModel wrapPlatformModel(BakedModel original) {
		return new ForgePlatformModel(original);
	}

	@Override
	public void setFilterSave(AbstractTexture texture, boolean filter, boolean mipmap) {
		texture.setBlurMipmap(filter, mipmap);
	}

	@Override
	public void restoreLastFilter(AbstractTexture texture) {
		texture.restoreLastBlurMipmap();
	}

	@Override
	public void tessellateBlock(Level level, BlockState state, BlockPos pos, PoseStack ps, MultiBufferSource buffers, int overlay) {
		var renderer = Minecraft.getInstance().getBlockRenderer();
		var model = renderer.getBlockModel(state);
		var modelData = model.getModelData(level, pos, state, ModelData.EMPTY);
		var rand = RandomSource.create();
		for (RenderType type : model.getRenderTypes(state, rand, modelData)) {
			renderer.getModelRenderer().tesselateBlock(level, model,
					state, pos, ps, buffers.getBuffer(type), false, rand,
					state.getSeed(pos), overlay, modelData, type);
		}
	}
}
