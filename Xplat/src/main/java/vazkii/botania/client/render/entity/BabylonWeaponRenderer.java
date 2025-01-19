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
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import vazkii.botania.client.core.handler.MiscellaneousModels;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.entity.BabylonWeaponEntity;
import vazkii.botania.common.helper.VecHelper;

import java.util.Random;

public class BabylonWeaponRenderer extends EntityRenderer<BabylonWeaponEntity> {

	public BabylonWeaponRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public void render(@NotNull BabylonWeaponEntity weapon, float yaw, float partialTicks, PoseStack ms, MultiBufferSource buffers, int light) {
		ms.pushPose();
		ms.mulPose(VecHelper.rotateY(weapon.getRotation()));

		int live = weapon.getLiveTicks();
		int delay = weapon.getDelay();
		float charge = Math.min(10F, Math.max(live, weapon.getChargeTicks()) + partialTicks);
		float chargeMul = charge / 10F;

		ms.pushPose();
		ms.translate(-0.75, 0, 1); // X shifts the weapon hilt to the center of the circle, Z makes it intersect it.
		float s = 1.5F;
		ms.scale(s, s, s);
		ms.mulPose(VecHelper.rotateY(90F)); // Rotate to make it match facing, instead of perpendicular to the circle
		ms.mulPose(VecHelper.rotateZ(-45F)); // Perpendicular to the ground, instead of the rising 45 deg of the sprite

		BakedModel model = MiscellaneousModels.INSTANCE.kingKeyWeaponModels[weapon.getVariety()];
		Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(ms.last(), buffers.getBuffer(Sheets.translucentItemSheet()), null, model, 1, 1, 1, 0xF000F0, OverlayTexture.NO_OVERLAY);
		ms.popPose();

		Random rand = new Random(weapon.getUUID().getMostSignificantBits());
		ms.mulPose(VecHelper.rotateX(-90F)); // Lay the circle horizontally
		ms.translate(0F, -0.3F + rand.nextFloat() * 0.1F, 0F); // Randomly offset how deep the item is in the circle

		s = chargeMul;
		if (live > delay) {
			s -= Math.min(1F, (live - delay + partialTicks) * 0.2F);
		}
		s *= 2F;
		ms.scale(s, s, s);

		ms.mulPose(VecHelper.rotateY(charge * 9F + (weapon.tickCount + partialTicks) * 0.5F + rand.nextFloat() * 360F));

		VertexConsumer buffer = buffers.getBuffer(RenderHelper.BABYLON_ICON);
		Matrix4f mat = ms.last().pose();
		buffer.addVertex(mat, -1, 0, -1).setColor(1, 1, 1, chargeMul).setUv(0, 0);
		buffer.addVertex(mat, -1, 0, 1).setColor(1, 1, 1, chargeMul).setUv(0, 1);
		buffer.addVertex(mat, 1, 0, 1).setColor(1, 1, 1, chargeMul).setUv(1, 1);
		buffer.addVertex(mat, 1, 0, -1).setColor(1, 1, 1, chargeMul).setUv(1, 0);

		ms.popPose();
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(@NotNull BabylonWeaponEntity entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}

}
