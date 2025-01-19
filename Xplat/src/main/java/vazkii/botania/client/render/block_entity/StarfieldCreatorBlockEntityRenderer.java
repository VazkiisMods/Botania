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

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import org.joml.Matrix4f;

import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.block.block_entity.StarfieldCreatorBlockEntity;

public class StarfieldCreatorBlockEntityRenderer implements BlockEntityRenderer<StarfieldCreatorBlockEntity> {
	public StarfieldCreatorBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(StarfieldCreatorBlockEntity tile, float partialTicsk, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
		// [VanillaCopy] Adapted from TheEndPortalRenderer, only renders UP face and sets the offset low in the blockspace
		Matrix4f matrix4f = pose.last().pose();
		float offset = 0.24F;
		VertexConsumer buffer = buffers.getBuffer(RenderHelper.STARFIELD);
		this.renderFace(buffer, matrix4f, 0.0F, 1.0F, offset, offset, 1.0F, 1.0F, 0.0F, 0.0F);
	}

	private void renderFace(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float g, float h, float i, float j, float k, float l, float m) {
		vertexConsumer.addVertex(matrix4f, f, h, j);
		vertexConsumer.addVertex(matrix4f, g, h, k);
		vertexConsumer.addVertex(matrix4f, g, i, l);
		vertexConsumer.addVertex(matrix4f, f, i, m);
	}
}
