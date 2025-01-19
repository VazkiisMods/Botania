/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.render.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import vazkii.botania.api.block.PetalApothecary;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.common.block.block_entity.PetalApothecaryBlockEntity;
import vazkii.botania.common.helper.VecHelper;

public class PetalApothecaryBlockEntityRenderer implements BlockEntityRenderer<PetalApothecaryBlockEntity> {
	private final BlockRenderDispatcher blockRenderDispatcher;

	public PetalApothecaryBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.blockRenderDispatcher = context.getBlockRenderDispatcher();
	}

	@Override
	public void render(@NotNull PetalApothecaryBlockEntity altar, float pticks, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		ms.pushPose();
		ms.translate(0.5, 1.25, 0.5);

		boolean water = altar.getFluid() == PetalApothecary.State.WATER;
		boolean lava = altar.getFluid() == PetalApothecary.State.LAVA;
		if (water || lava) {
			ms.pushPose();
			float v = 1F / 8F;

			if (water) {
				int petals = 0;
				for (int i = 0; i < altar.inventorySize(); i++) {
					if (!altar.getItemHandler().getItem(i).isEmpty()) {
						petals++;
					} else {
						break;
					}
				}

				if (petals > 0) {
					final float modifier = 6F;
					final float rotationModifier = 0.25F;
					final float radiusBase = 1.2F;
					final float radiusMod = 0.1F;

					double ticks = (ClientTickHandler.ticksInGame + pticks) * 0.5;
					float offsetPerPetal = 360F / petals;

					ms.pushPose();
					ms.translate(-0.05F, -0.38F, 0F);
					ms.scale(v, v, v);
					for (int i = 0; i < petals; i++) {
						float offset = offsetPerPetal * i;
						float deg = (int) (ticks / rotationModifier % 360F + offset);
						float rad = VecHelper.toRadians(deg);
						float radiusX = (float) (radiusBase + radiusMod * Math.sin(ticks / modifier));
						float radiusZ = (float) (radiusBase + radiusMod * Math.cos(ticks / modifier));
						float x = (float) (radiusX * Math.cos(rad));
						float z = (float) (radiusZ * Math.sin(rad));
						float y = (float) Math.cos((ticks + 50 * i) / 5F) / 10F;

						ms.pushPose();
						ms.translate(x, y, z);
						float xRotate = (float) Math.sin(ticks * rotationModifier) / 2F;
						float yRotate = (float) Math.max(0.6F, Math.sin(ticks * 0.1F) / 2F + 0.5F);
						float zRotate = (float) Math.cos(ticks * rotationModifier) / 2F;

						v /= 2F;
						ms.translate(v, v, v);
						ms.mulPose(new Quaternionf().rotateAxis(rad, xRotate, yRotate, zRotate));
						ms.translate(-v, -v, -v);
						v *= 2F;

						ItemStack stack = altar.getItemHandler().getItem(i);
						Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND,
								light, overlay, ms, buffers, altar.getLevel(), 0);
						ms.popPose();
					}

					ms.popPose();
				}
			}

			float alpha = lava ? 1F : 0.7F;

			ms.translate(-8 / 16F, -0.3125F, -8 / 16F);
			ms.mulPose(VecHelper.rotateX(90));
			ms.scale(1 / 16F, 1 / 16F, 1 / 16F);

			TextureAtlasSprite sprite = lava ? this.blockRenderDispatcher.getBlockModel(Blocks.LAVA.defaultBlockState()).getParticleIcon()
					: this.blockRenderDispatcher.getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon();
			int color = lava ? -1
					: BiomeColors.getAverageWaterColor(altar.getLevel(), altar.getBlockPos());
			VertexConsumer buffer = buffers.getBuffer(Sheets.translucentCullBlockSheet());
			renderIcon(ms, buffer, sprite, color, alpha, overlay, lava ? 0xF000F0 : light);
			ms.popPose();
		}
		ms.popPose();
	}

	private void renderIcon(PoseStack ms, VertexConsumer builder, TextureAtlasSprite sprite, int color, float alpha, int overlay, int light) {
		int red = ((color >> 16) & 0xFF);
		int green = ((color >> 8) & 0xFF);
		int blue = (color & 0xFF);
		Matrix4f mat = ms.last().pose();
		int start = 3;
		int end = 13;
		builder.addVertex(mat, start, end, 0).setColor(red, green, blue, (int) (alpha * 255F))
				.setUv(sprite.getU(start), sprite.getV(end)).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
		builder.addVertex(mat, end, end, 0).setColor(red, green, blue, (int) (alpha * 255F))
				.setUv(sprite.getU(end), sprite.getV(end)).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
		builder.addVertex(mat, end, start, 0).setColor(red, green, blue, (int) (alpha * 255F))
				.setUv(sprite.getU(end), sprite.getV(start)).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
		builder.addVertex(mat, start, start, 0).setColor(red, green, blue, (int) (alpha * 255F))
				.setUv(sprite.getU(start), sprite.getV(start)).setOverlay(overlay).setLight(light).setNormal(0, 0, 1);
	}

}
