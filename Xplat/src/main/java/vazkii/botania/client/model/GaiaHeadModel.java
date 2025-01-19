/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.SkullModelBase;

import vazkii.botania.client.render.block_entity.GaiaHeadBlockEntityRenderer;

public class GaiaHeadModel extends SkullModelBase {
	@Override
	public void setupAnim(float animationProgress, float yRot, float xRot) {
		var type = GaiaHeadBlockEntityRenderer.getViewType();
		var model = GaiaHeadBlockEntityRenderer.models.get(type);
		if (model != null) {
			model.setupAnim(animationProgress, yRot, xRot);
		}
	}

	@Override
	public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int light, int overlay, int color) {
		var type = GaiaHeadBlockEntityRenderer.getViewType();
		var model = GaiaHeadBlockEntityRenderer.models.get(type);
		if (model != null) {
			model.renderToBuffer(ms, buffer, light, overlay, color);
		}
	}
}
