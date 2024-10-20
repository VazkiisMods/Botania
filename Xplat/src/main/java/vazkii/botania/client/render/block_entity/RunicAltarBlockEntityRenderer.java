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
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.block.block_entity.RunicAltarBlockEntity;
import vazkii.botania.common.helper.VecHelper;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class RunicAltarBlockEntityRenderer implements BlockEntityRenderer<RunicAltarBlockEntity> {
	private final ModelPart spinningCube;
	private static final ResourceLocation cubeTex = botaniaRL("textures/block/runic_altar_cube.png");

	public RunicAltarBlockEntityRenderer(BlockEntityRendererProvider.Context manager) {
		var mesh = new MeshDefinition();
		// todo 1.17 this doesn't properly map the full texture onto the cube. Needs more param fiddling.
		mesh.getRoot().addOrReplaceChild("cube", CubeListBuilder.create().addBox(0, 0, 0, 1, 1, 1), PartPose.ZERO);
		spinningCube = LayerDefinition.create(mesh, 16, 16).bakeRoot();
	}

	@Override
	public void render(@NotNull RunicAltarBlockEntity altar, float partticks, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		ms.pushPose();

		int items = 0;
		for (int i = 0; i < altar.inventorySize(); i++) {
			if (altar.getItemHandler().getItem(i).isEmpty()) {
				break;
			} else {
				items++;
			}
		}
		float[] angles = new float[altar.inventorySize()];

		float anglePer = 360F / items;
		float totalAngle = 0F;
		for (int i = 0; i < angles.length; i++) {
			angles[i] = totalAngle += anglePer;
		}

		double time = ClientTickHandler.ticksInGame + partticks;

		for (int i = 0; i < altar.inventorySize(); i++) {
			ms.pushPose();
			ms.translate(0.5F, 1.25F, 0.5F);
			ms.mulPose(VecHelper.rotateY(angles[i] + (float) time));
			ms.translate(1.125F, 0F, 0.25F);
			ms.mulPose(VecHelper.rotateY(90F));
			ms.translate(0D, 0.075 * Math.sin((time + i * 10) / 5D), 0F);
			ItemStack stack = altar.getItemHandler().getItem(i);
			Minecraft mc = Minecraft.getInstance();
			if (!stack.isEmpty()) {
				mc.getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND,
						light, overlay, ms, buffers, altar.getLevel(), 0);
			}
			ms.popPose();
		}

		ms.pushPose();
		ms.translate(0.5F, 0.5F, 0.5F);
		renderSpinningCubes(ms, buffers, overlay, 2, 15);
		ms.popPose();

		ms.translate(0F, 0.2F, 0F);
		float scale = altar.getTargetMana() == 0 ? 0 : (float) altar.getCurrentMana() / (float) altar.getTargetMana() / 75F;

		if (scale != 0) {
			int seed = altar.getBlockPos().getX() ^ altar.getBlockPos().getY() ^ altar.getBlockPos().getZ();
			ms.translate(0.5F, 0.7F, 0.5F);
			RenderHelper.renderStar(ms, buffers, 0x00E4D7, scale, scale, scale, seed);
		}

		ms.popPose();
	}

	private void renderSpinningCubes(PoseStack ms, MultiBufferSource buffers, int overlay, int cubes, int iters) {
		for (int curIter = iters; curIter > 0; curIter--) {
			final float modifier = 6F;
			final float rotationModifier = 0.2F;
			final float radiusBase = 0.35F;
			final float radiusMod = 0.05F;

			double ticks = ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks - 1.3 * (iters - curIter);
			float offsetPerCube = 360 / cubes;

			ms.pushPose();
			ms.translate(-0.025F, 0.85F, -0.025F);
			for (int i = 0; i < cubes; i++) {
				float offset = offsetPerCube * i;
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

				ms.mulPose(new Quaternionf().rotateAxis(rad, xRotate, yRotate, zRotate));
				float alpha = 1;
				if (curIter < iters) {
					alpha = (float) curIter / (float) iters * 0.4F;
				}

				VertexConsumer buffer = buffers.getBuffer(curIter < iters ? RenderType.entityTranslucentCull(cubeTex) : RenderType.entitySolid(cubeTex));
				spinningCube.render(ms, buffer, 0xF000F0, overlay, 1, 1, 1, alpha);

				ms.popPose();
			}
			ms.popPose();
		}
	}
}
