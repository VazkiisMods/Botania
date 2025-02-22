/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.entity.MagicLandmineEntity;

public class MagicLandmineRenderer extends EntityRenderer<MagicLandmineEntity> {

	public MagicLandmineRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public void render(MagicLandmineEntity e, float entityYaw, float partialTicks, PoseStack ms, MultiBufferSource buffers, int light) {
		super.render(e, entityYaw, partialTicks, ms, buffers, light);

		ms.pushPose();
		AABB aabb = e.getBoundingBox().move(e.position().scale(-1));

		float gs = (float) (Math.sin((ClientTickHandler.ticksInGame + partialTicks) / 20) + 1) * 0.2F + 0.6F;
		int r = (int) (105 * gs);
		int g = (int) (25 * gs);
		int b = (int) (145 * gs);

		int alpha = 32;
		if (e.tickCount < 8) {
			alpha *= Math.min((e.tickCount + partialTicks) / 8F, 1F);
		} else if (e.tickCount > 47) {
			alpha *= Math.min(1F - (e.tickCount - 47 + partialTicks) / 8F, 1F);
		}

		ms.translate(aabb.minX, aabb.minY + RenderHelper.getOffY(), aabb.minZ);

		float f = 1F / 16F;
		float x = (float) (aabb.getXsize() - f);
		float z = (float) (aabb.getZsize() - f);

		VertexConsumer buffer = buffers.getBuffer(RenderHelper.RECTANGLE);
		Matrix4f mat = ms.last().pose();
		RenderHelper.flatRectangle(buffer, mat, f, x, 0, f, z, r, g, b, alpha);
		RenderHelper.incrementOffY();
		ms.popPose();
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(@NotNull MagicLandmineEntity entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}
}
