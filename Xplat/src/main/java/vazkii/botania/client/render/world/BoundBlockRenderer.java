/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.render.world;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.item.WireframeCoordinateListProvider;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.mixin.client.LevelRendererAccessor;
import vazkii.botania.xplat.BotaniaConfig;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;
import java.util.SequencedMap;

public final class BoundBlockRenderer {
	private static final MultiBufferSource.BufferSource LINE_BUFFERS = MultiBufferSource.immediateWithBuffers(Util.make(() -> {
		SequencedMap<RenderType, ByteBufferBuilder> ret = new Object2ObjectLinkedOpenHashMap<>();
		ret.put(RenderHelper.LINE_1_NO_DEPTH, new ByteBufferBuilder(RenderHelper.LINE_1_NO_DEPTH.bufferSize()));
		ret.put(RenderHelper.LINE_4_NO_DEPTH, new ByteBufferBuilder(RenderHelper.LINE_4_NO_DEPTH.bufferSize()));
		ret.put(RenderHelper.LINE_5_NO_DEPTH, new ByteBufferBuilder(RenderHelper.LINE_5_NO_DEPTH.bufferSize()));
		ret.put(RenderHelper.LINE_8_NO_DEPTH, new ByteBufferBuilder(RenderHelper.LINE_8_NO_DEPTH.bufferSize()));
		return ret;
	}), new ByteBufferBuilder(786432)); //Todo confirm the capacity is correct. This is just taken from `RenderBuffers`

	private BoundBlockRenderer() {}

	public static void onWorldRenderLast(Camera camera, PoseStack ms, Level level) {
		Player player = Minecraft.getInstance().player;
		if (!BotaniaConfig.client().boundBlockWireframe()
				|| player == null
				|| player.level() != level) {
			return;
		}

		ms.pushPose();

		int color = 0xFF000000 | Mth.hsvToRgb(ClientTickHandler.ticksInGame % 200 / 200F, 0.6F, 1F);

		if (!player.getMainHandItem().isEmpty()) {
			var coordBoundItem = XplatAbstractions.INSTANCE.findCoordBoundItem(player.getMainHandItem());
			if (coordBoundItem != null) {
				BlockPos coords = coordBoundItem.getBinding(player.level());
				if (coords != null) {
					renderBlockOutlineAt(camera, ms, LINE_BUFFERS, player.level(), coords, color);
				}
			}
		}

		if (!player.getOffhandItem().isEmpty()) {
			var coordBoundItem = XplatAbstractions.INSTANCE.findCoordBoundItem(player.getOffhandItem());
			if (coordBoundItem != null) {
				BlockPos coords = coordBoundItem.getBinding(player.level());
				if (coords != null) {
					renderBlockOutlineAt(camera, ms, LINE_BUFFERS, player.level(), coords, color);
				}
			}
		}

		renderWireframeProviders(camera, player.getInventory(), player, ms, color);
		renderWireframeProviders(camera, BotaniaAPI.instance().getAccessoriesInventory(player), player, ms, color);

		ms.popPose();
		RenderSystem.disableDepthTest();
		LINE_BUFFERS.endBatch();
	}

	private static void renderWireframeProviders(Camera camera, Container inv,
			Player player, PoseStack ms, int color) {
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stackInSlot = inv.getItem(i);

			if (!stackInSlot.isEmpty() && stackInSlot.getItem() instanceof WireframeCoordinateListProvider provider) {
				List<BlockPos> coordsList = provider.getWireframesToDraw(player, stackInSlot);
				for (BlockPos coords : coordsList) {
					renderBlockOutlineAt(camera, ms, LINE_BUFFERS, player.level(), coords, color);
				}

				BlockPos coords = provider.getSourceWireframe(player, stackInSlot);
				if (coords != null && coords.getY() != Integer.MIN_VALUE) {
					renderBlockOutlineAt(camera, ms, LINE_BUFFERS, player.level(), coords, color, true);
				}
			}
		}
	}

	private static void renderBlockOutlineAt(Camera camera, PoseStack ms, MultiBufferSource buffers, Level level, BlockPos pos, int color) {
		renderBlockOutlineAt(camera, ms, buffers, level, pos, color, false);
	}

	private static void renderBlockOutlineAt(Camera camera, PoseStack ms, MultiBufferSource buffers, Level level, BlockPos pos, int color, boolean thick) {
		VoxelShape shape = level.getBlockState(pos).getShape(level, pos);

		if (!shape.isEmpty()) {
			double renderPosX = camera.getPosition().x();
			double renderPosY = camera.getPosition().y();
			double renderPosZ = camera.getPosition().z();

			ms.pushPose();
			ms.translate(pos.getX() - renderPosX, pos.getY() - renderPosY, pos.getZ() - renderPosZ);

			VertexConsumer buffer = buffers.getBuffer(thick ? RenderHelper.LINE_5_NO_DEPTH : RenderHelper.LINE_1_NO_DEPTH);
			renderBlockOutline(ms, buffer, shape, color);

			buffer = buffers.getBuffer(thick ? RenderHelper.LINE_8_NO_DEPTH : RenderHelper.LINE_4_NO_DEPTH);
			int alpha = 64;
			color = (color & ~0xff000000) | (alpha << 24);
			renderBlockOutline(ms, buffer, shape, color);

			ms.popPose();
		}
	}

	private static void renderBlockOutline(PoseStack pose, VertexConsumer buffer, VoxelShape shape, int color) {
		float a = ((color >> 24) & 0xFF) / 255.0F;
		float r = ((color >> 16) & 0xFF) / 255.0F;
		float g = ((color >> 8) & 0xFF) / 255.0F;
		float b = (color & 0xFF) / 255.F;
		LevelRendererAccessor.renderShape(pose, buffer, shape, 0.0, 0.0, 0.0, r, g, b, a);
	}
}
