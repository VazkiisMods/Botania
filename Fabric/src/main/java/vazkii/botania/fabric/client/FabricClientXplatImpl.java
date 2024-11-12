package vazkii.botania.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.BotaniaFabricClientCapabilities;
import vazkii.botania.api.block.WandHUD;
import vazkii.botania.api.item.TinyPotatoRenderCallback;
import vazkii.botania.xplat.ClientXplatAbstractions;

public class FabricClientXplatImpl implements ClientXplatAbstractions {
	@Override
	public void fireRenderTinyPotato(BlockEntity potato, Component name, float tickDelta, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		TinyPotatoRenderCallback.EVENT.invoker().onRender(potato, name, tickDelta, ms, buffers, light, overlay);
	}

	@Override
	public void sendToServer(CustomPacketPayload packet) {
		ClientPlayNetworking.send(packet.getFabricId(), packet.toBuf());
	}

	@Nullable
	@Override
	public WandHUD findWandHud(Level level, BlockPos pos, BlockState state, BlockEntity be) {
		return BotaniaFabricClientCapabilities.WAND_HUD.find(level, pos, state, be, Unit.INSTANCE);
	}

	@Nullable
	@Override
	public WandHUD findWandHud(Entity entity) {
		return BotaniaFabricClientCapabilities.ENTITY_WAND_HUD.find(entity, Unit.INSTANCE);
	}

	@Override
	public BakedModel wrapPlatformModel(BakedModel original) {
		return new FabricPlatformModel(original);
	}

	@Override
	public void setFilterSave(AbstractTexture texture, boolean filter, boolean mipmap) {
		((ExtendedTexture) texture).setFilterSave(filter, mipmap);
	}

	@Override
	public void restoreLastFilter(AbstractTexture texture) {
		((ExtendedTexture) texture).restoreLastFilter();
	}

	@Override
	public void tessellateBlock(Level level, BlockState state, BlockPos pos, PoseStack ps, MultiBufferSource buffers, int overlay) {
		var brd = Minecraft.getInstance().getBlockRenderer();
		var buffer = buffers.getBuffer(ItemBlockRenderTypes.getRenderType(state, false));
		brd.getModelRenderer().tesselateBlock(level, brd.getBlockModel(state), state, pos, ps,
				buffer, true, RandomSource.create(), state.getSeed(pos), overlay);
	}
}
