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

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.client.model.BotaniaModelLayers;
import vazkii.botania.common.block.block_entity.corporea.CorporeaIndexBlockEntity;
import vazkii.botania.common.helper.VecHelper;

public class CorporeaIndexBlockEntityRenderer implements BlockEntityRenderer<CorporeaIndexBlockEntity> {
	private static final RenderType LAYER = RenderType.entityCutoutNoCull(ResourceLocation.parse(ResourcesLib.MODEL_CORPOREA_INDEX));
	private static final float ANGLE = (float) Math.sin(Math.toRadians(45));
	private final ModelPart ring;
	private final ModelPart cube;

	public CorporeaIndexBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		ModelPart root = ctx.bakeLayer(BotaniaModelLayers.CORPOREA_INDEX);
		this.ring = root.getChild("ring");
		this.cube = root.getChild("cube");
	}

	public static MeshDefinition createMesh() {
		var mesh = new MeshDefinition();
		var root = mesh.getRoot();
		root.addOrReplaceChild("ring", CubeListBuilder.create()
				.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F),
				PartPose.ZERO);
		root.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(32, 0)
				.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F),
				PartPose.ZERO);
		return mesh;
	}

	@Override
	public boolean shouldRenderOffScreen(@NotNull CorporeaIndexBlockEntity blockEntity) {
		return true;
	}

	@Override
	public void render(@Nullable CorporeaIndexBlockEntity index, float partialTicks, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		ms.pushPose();
		ms.translate(0.5, 0, 0.5);

		float rotation = (ClientTickHandler.ticksInGame + partialTicks) * 2;
		float translation;
		if (index == null) { // Item render
			ms.scale(1.3f, 1.3f, 1.3f);
			ms.translate(0, -0.1, 0);
			translation = 0;
		} else {
			translation = (float) ((Math.cos((index.ticksWithCloseby + (index.hasCloseby ? partialTicks : 0)) / 10F) * 0.5 + 0.5) * 0.25);
		}

		VertexConsumer buffer = buffers.getBuffer(LAYER);
		ms.pushPose();
		ms.translate(0.0D, -1, 0.0D);

		ms.mulPose(VecHelper.rotateY(rotation));
		ms.translate(0.0D, 1.5F + translation / 2.0F, 0.0D);
		ms.mulPose(new Quaternionf().rotateAxis(VecHelper.toRadians(60.0F), ANGLE, 0.0F, ANGLE));
		this.ring.render(ms, buffer, light, overlay);
		ms.scale(0.875F, 0.875F, 0.875F);
		ms.mulPose(new Quaternionf().rotateAxis(VecHelper.toRadians(60.0F), ANGLE, 0.0F, ANGLE));
		ms.mulPose(VecHelper.rotateY(rotation));
		this.ring.render(ms, buffer, light, overlay);
		ms.scale(0.875F, 0.875F, 0.875F);
		ms.mulPose(new Quaternionf().rotateAxis(VecHelper.toRadians(60.0F), ANGLE, 0.0F, ANGLE));
		ms.mulPose(VecHelper.rotateY(rotation));
		this.cube.render(ms, buffer, light, overlay);
		ms.popPose();

		if (index != null && index.closeby > 0F) {
			float starScale = 0.02F;
			float starRadius = (float) CorporeaIndexBlockEntity.RADIUS * index.closeby + (index.closeby == 1F ? 0F : index.hasCloseby ? partialTicks : -partialTicks) * 0.2F;
			double rads = (index.ticksWithCloseby + partialTicks) * 2 * Math.PI / 180;
			double starX = Math.cos(rads) * starRadius;
			double starZ = Math.sin(rads) * starRadius;
			int color = 0xFF00FF;
			int seed = index.getBlockPos().getX() ^ index.getBlockPos().getY() ^ index.getBlockPos().getZ();

			ms.translate(starX, 0.3, starZ);
			RenderHelper.renderStar(ms, buffers, color, starScale, starScale, starScale, seed, partialTicks);
			ms.translate(-starX * 2, 0, -starZ * 2);
			RenderHelper.renderStar(ms, buffers, color, starScale, starScale, starScale, seed, partialTicks);
			ms.translate(starX, 0, starZ);

			rads = -rads;
			starX = Math.cos(rads) * starRadius;
			starZ = Math.sin(rads) * starRadius;
			ms.translate(starX, 0, starZ);
			RenderHelper.renderStar(ms, buffers, color, starScale, starScale, starScale, seed, partialTicks);
			ms.translate(-starX * 2, 0, -starZ * 2);
			RenderHelper.renderStar(ms, buffers, color, starScale, starScale, starScale, seed, partialTicks);
			ms.translate(starX, 0, starZ);
		}
		ms.popPose();
	}

}
