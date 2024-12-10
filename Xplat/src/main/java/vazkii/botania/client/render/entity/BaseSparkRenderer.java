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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.entity.SparkBaseEntity;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.common.helper.VecHelper;

import java.util.Objects;
import java.util.Random;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public abstract class BaseSparkRenderer<T extends SparkBaseEntity> extends EntityRenderer<T> {

	private final TextureAtlasSprite starSprite;
	private final TextureAtlasSprite worldSprite;

	public BaseSparkRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
		var atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
		this.starSprite = Objects.requireNonNull(
				atlas.apply(botaniaRL("item/corporea_spark_star"))
		);
		this.worldSprite = Objects.requireNonNull(
				atlas.apply(botaniaRL("item/spark"))
		);
	}

	@Override
	public void render(@NotNull T tEntity, float yaw, float partialTicks, PoseStack ms, MultiBufferSource buffers, int light) {
		TextureAtlasSprite iicon = getBaseIcon(tEntity);

		ms.pushPose();

		double time = (tEntity.level().getGameTime() % 24000) + partialTicks + new Random(tEntity.getId()).nextInt(200);
		float a = 0.1F + (tEntity.isInvisible() ? 0 : 1) * 0.8F;

		int alpha = (int) ((0.7 + 0.3 * (Math.sin(time / 5.0) + 0.5) * 2) * a * 255.0);
		int iconColor = 0xFFFFFF | (alpha << 24);

		float scale = 0.75F + 0.1F * (float) Math.sin(time / 10);
		ms.scale(scale, scale, scale);

		VertexConsumer buffer = buffers.getBuffer(RenderHelper.SPARK);
		ms.pushPose();
		ms.mulPose(entityRenderDispatcher.cameraOrientation());
		ms.mulPose(VecHelper.rotateY(180));
		renderIcon(ms, buffer, iicon, iconColor);

		ms.pushPose();
		ms.translate(-0.02 + Math.sin(time / 20) * 0.2, 0.24 + Math.cos(time / 20) * 0.2, 0.005);
		ms.scale(0.2F, 0.2F, 0.2F);
		int starColor = ColorHelper.getColorValue(tEntity.getNetwork()) | ((int) (a * 255.0F) << 24);
		renderIcon(ms, buffer, this.starSprite, starColor);
		ms.popPose();

		TextureAtlasSprite spinningIcon = getSpinningIcon(tEntity);
		if (spinningIcon != null) {
			ms.translate(-0.02 + Math.sin(time / 20) * -0.2, 0.24 + Math.cos(time / 20) * -0.2, 0.005);
			ms.scale(0.2F, 0.2F, 0.2F);
			renderIcon(ms, buffer, spinningIcon, iconColor);
		}
		ms.popPose();

		ms.popPose();
	}

	protected TextureAtlasSprite getBaseIcon(T entity) {
		return this.worldSprite;
	}

	@Nullable
	protected TextureAtlasSprite getSpinningIcon(T entity) {
		return null;
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(@NotNull SparkBaseEntity entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}

	private void renderIcon(PoseStack ms, VertexConsumer buffer, TextureAtlasSprite icon, int color) {
		float f = icon.getU0();
		float f1 = icon.getU1();
		float f2 = icon.getV0();
		float f3 = icon.getV1();
		float f4 = 1.0F;
		float f5 = 0.5F;
		float f6 = 0.25F;
		int fullbright = 0xF000F0;
		int a = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = color & 0xFF;
		Matrix4f mat = ms.last().pose();
		buffer.addVertex(mat, 0.0F - f5, 0.0F - f6, 0.0F).setColor(r, g, b, a).setUv(f, f3).uv2(fullbright);
		buffer.addVertex(mat, f4 - f5, 0.0F - f6, 0.0F).setColor(r, g, b, a).setUv(f1, f3).uv2(fullbright);
		buffer.addVertex(mat, f4 - f5, f4 - f6, 0.0F).setColor(r, g, b, a).setUv(f1, f2).uv2(fullbright);
		buffer.addVertex(mat, 0.0F - f5, f4 - f6, 0.0F).setColor(r, g, b, a).setUv(f, f2).uv2(fullbright);
	}

}
