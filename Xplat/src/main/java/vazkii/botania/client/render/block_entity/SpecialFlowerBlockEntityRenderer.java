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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.api.block_entity.SpecialFlowerBlockEntity;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.item.WandOfTheForestItem;
import vazkii.botania.common.item.equipment.bauble.ManaseerMonocleItem;

public class SpecialFlowerBlockEntityRenderer<T extends SpecialFlowerBlockEntity> implements BlockEntityRenderer<T> {

	public static final int INNER_ALPHA = 32;
	public static final int OUTER_ALPHA = 64;
	public static final float FRAME_WIDTH = 1F / 16F;
	public static final float Y_OFFSET_INNER = 1F / 16F;
	public static final float Y_OFFSET_OUTER = FRAME_WIDTH + FRAME_WIDTH / 4F;
	public static final int TOTAL_ANGLES = 360;
	public static final double DEGREES_TO_RADIAN = (Math.PI / (double) (TOTAL_ANGLES / 2));

	public SpecialFlowerBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(SpecialFlowerBlockEntity tile, float partialTicks, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		if (tile.isFloating()) {
			FloatingFlowerBlockEntityRenderer.renderFloatingIsland(tile, partialTicks, ms, buffers, overlay);
		}
		if (!(Minecraft.getInstance().cameraEntity instanceof LivingEntity view)) {
			return;
		}

		if (!ManaseerMonocleItem.hasMonocle(view)) {
			return;
		}
		BlockPos pos = null;
		HitResult ray = Minecraft.getInstance().hitResult;
		if (ray != null && ray.getType() == HitResult.Type.BLOCK) {
			pos = ((BlockHitResult) ray).getBlockPos();
		}
		boolean hasBindingAttempt = hasBindingAttempt(view, tile.getBlockPos());

		if (hasBindingAttempt || tile.getBlockPos().equals(pos)) {
			SpecialFlowerBlockEntity flower = tile;
			ms.pushPose();
			if (hasBindingAttempt) {
				ms.translate(0, 0.005, 0);
			}
			renderRadius(tile, ms, buffers, flower.getRadius());
			ms.translate(0, 0.002, 0);
			renderRadius(tile, ms, buffers, flower.getSecondaryRadius());
			ms.popPose();

		}
	}

	public static void renderRadius(BlockEntity tile, PoseStack ms, MultiBufferSource buffers, @Nullable RadiusDescriptor descriptor) {
		if (descriptor != null) {
			ms.pushPose();
			ms.translate(0, RenderHelper.getOffY(), 0);
			if (descriptor instanceof RadiusDescriptor.Circle circle) {
				renderCircle(ms, buffers, tile.getBlockPos(), circle.subtileCoords(), circle.radius());
			} else if (descriptor instanceof RadiusDescriptor.Rectangle rectangle) {
				renderRectangle(ms, buffers, tile.getBlockPos(), rectangle.aabb());
			}
			RenderHelper.incrementOffY();
			ms.popPose();
		}
	}

	public static boolean hasBindingAttempt(LivingEntity view, BlockPos tilePos) {
		ItemStack stackHeld = PlayerHelper.getFirstHeldItemClass(view, WandOfTheForestItem.class);
		if (!stackHeld.isEmpty() && WandOfTheForestItem.getBindMode(stackHeld)) {
			return WandOfTheForestItem.getBindingAttempt(stackHeld).filter(tilePos::equals).isPresent();
		}
		return false;
	}

	public static void renderCircle(PoseStack ms, MultiBufferSource buffers, BlockPos tilePos, BlockPos center, double radius) {
		ms.pushPose();
		ms.translate(center.getX() - tilePos.getX() + 0.5, center.getY() - tilePos.getY(), center.getZ() - tilePos.getZ() + 0.5);

		int color = Mth.hsvToRgb(ClientTickHandler.ticksInGame % 200 / 200F, 0.6F, 1F);
		int r = (color >> 16 & 0xFF);
		int g = (color >> 8 & 0xFF);
		int b = (color & 0xFF);

		VertexConsumer buffer = buffers.getBuffer(RenderHelper.CIRCLE);
		Matrix4f mat = ms.last().pose();

		double innerRadius = radius - FRAME_WIDTH;
		Runnable centerFuncInner = () -> buffer.addVertex(mat, 0, Y_OFFSET_INNER, 0).setColor(r, g, b, INNER_ALPHA);
		Runnable centerFuncOuter = () -> buffer.addVertex(mat, 0, Y_OFFSET_OUTER, 0).setColor(r, g, b, OUTER_ALPHA);
		Runnable[] vertexFuncsInner = new Runnable[TOTAL_ANGLES + 1];
		Runnable[] vertexFuncsOuter = new Runnable[TOTAL_ANGLES + 1];

		for (int i = 0; i < TOTAL_ANGLES; i++) {
			double rad = (TOTAL_ANGLES - i) * DEGREES_TO_RADIAN;
			double cos = Math.cos(rad);
			double sin = Math.sin(rad);

			float xpInner = (float) (cos * innerRadius);
			float zpInner = (float) (sin * innerRadius);
			vertexFuncsInner[i] = (() -> buffer.addVertex(mat, xpInner, Y_OFFSET_INNER, zpInner).setColor(r, g, b, INNER_ALPHA));

			float xpOuter = (float) (Math.cos(rad) * radius);
			float zpOuter = (float) (Math.sin(rad) * radius);
			vertexFuncsOuter[i] = (() -> buffer.addVertex(mat, xpOuter, Y_OFFSET_OUTER, zpOuter).setColor(r, g, b, OUTER_ALPHA));
		}
		vertexFuncsInner[TOTAL_ANGLES] = vertexFuncsInner[0];
		vertexFuncsOuter[TOTAL_ANGLES] = vertexFuncsOuter[0];

		RenderHelper.triangleFan(centerFuncInner, vertexFuncsInner);
		RenderHelper.triangleFan(centerFuncOuter, vertexFuncsOuter);

		ms.popPose();
	}

	public static void renderRectangle(PoseStack ms, MultiBufferSource buffers, BlockPos tilePos, AABB aabb) {
		ms.pushPose();
		ms.translate(aabb.minX - tilePos.getX(), aabb.minY - tilePos.getY(), aabb.minZ - tilePos.getZ());

		int color = Mth.hsvToRgb(ClientTickHandler.ticksInGame % 200 / 200F, 0.6F, 1F);
		int r = (color >> 16 & 0xFF);
		int g = (color >> 8 & 0xFF);
		int b = (color & 0xFF);

		float xSize = (float) aabb.getXsize();
		float zSize = (float) aabb.getZsize();
		float xSizeInner = xSize - FRAME_WIDTH;
		float zSizeInner = zSize - FRAME_WIDTH;

		VertexConsumer buffer = buffers.getBuffer(RenderHelper.RECTANGLE);
		Matrix4f mat = ms.last().pose();
		RenderHelper.flatRectangle(buffer, mat, FRAME_WIDTH, xSizeInner, Y_OFFSET_INNER, FRAME_WIDTH, zSizeInner,
				r, g, b, INNER_ALPHA);
		RenderHelper.flatRectangle(buffer, mat, 0, xSize, Y_OFFSET_OUTER, 0, zSize, r, g, b, OUTER_ALPHA);

		ms.popPose();
	}

}
