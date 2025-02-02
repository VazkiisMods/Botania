/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.render.world;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.common.helper.VecHelper;

import java.util.Random;

public class SkyblockSkyRenderer {

	private static final ResourceLocation textureSkybox = ResourceLocation.parse(ResourcesLib.MISC_SKYBOX);
	private static final ResourceLocation textureRainbow = ResourceLocation.parse(ResourcesLib.MISC_RAINBOW);
	private static final ResourceLocation[] planetTextures = new ResourceLocation[] {
			ResourceLocation.parse(ResourcesLib.MISC_PLANET + "0.png"),
			ResourceLocation.parse(ResourcesLib.MISC_PLANET + "1.png"),
			ResourceLocation.parse(ResourcesLib.MISC_PLANET + "2.png"),
			ResourceLocation.parse(ResourcesLib.MISC_PLANET + "3.png"),
			ResourceLocation.parse(ResourcesLib.MISC_PLANET + "4.png"),
			ResourceLocation.parse(ResourcesLib.MISC_PLANET + "5.png")
	};

	public static void renderExtra(PoseStack ms, ClientLevel world, float partialTicks, float insideVoid) {
		// Botania - Begin extra stuff
		Tesselator tessellator = Tesselator.getInstance();
		float rain = 1.0F - world.getRainLevel(partialTicks);
		float celAng = world.getTimeOfDay(partialTicks);
		float effCelAng = celAng;
		if (celAng > 0.5) {
			effCelAng = 0.5F - (celAng - 0.5F);
		}

		// === Planets
		float scale = 20F;
		float lowA = Math.max(0F, effCelAng - 0.3F) * rain;
		float a = Math.max(0.1F, lowA);

		RenderSystem.blendFuncSeparate(770, 771, 1, 0);
		ms.pushPose();
		RenderSystem.setShaderColor(1F, 1F, 1F, a * 4 * (1F - insideVoid));
		ms.mulPose(new Quaternionf().rotateAxis(VecHelper.toRadians(90), 0.5F, 0.5F, 0F));
		for (int p = 0; p < planetTextures.length; p++) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, planetTextures[p]);
			Matrix4f mat = ms.last().pose();
			tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX).addVertex(mat, -scale, 100, -scale).setUv(0.0F, 0.0F);
			tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX).addVertex(mat, scale, 100, -scale).setUv(1.0F, 0.0F);
			tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX).addVertex(mat, scale, 100, scale).setUv(1.0F, 1.0F);
			tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX).addVertex(mat, -scale, 100, scale).setUv(0.0F, 1.0F);
			tessellator.clear();

			switch (p) {
				case 0 -> {
					ms.mulPose(VecHelper.rotateX(70));
					scale = 12F;
				}
				case 1 -> {
					ms.mulPose(VecHelper.rotateZ(120));
					scale = 15F;
				}
				case 2 -> {
					ms.mulPose(new Quaternionf().rotateAxis(VecHelper.toRadians(80), 1, 0, 1));
					scale = 25F;
				}
				case 3 -> {
					ms.mulPose(VecHelper.rotateZ(100));
					scale = 10F;
				}
				case 4 -> {
					ms.mulPose(new Quaternionf().rotateAxis(VecHelper.toRadians(-60), 1, 0, 0.5F));
					scale = 40F;
				}
			}
		}
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		ms.popPose();

		// === Rays
		RenderSystem.setShaderTexture(0, textureSkybox);

		scale = 20F;
		a = lowA;
		ms.pushPose();
		RenderSystem.blendFuncSeparate(770, 1, 1, 0);
		ms.translate(0, -1, 0);
		ms.mulPose(VecHelper.rotateX(220));
		RenderSystem.setShaderColor(1F, 1F, 1F, a);
		int angles = 90;
		float y = 2F;
		float y0 = 0F;
		float uPer = 1F / 360F;
		float anglePer = 360F / angles;
		double fuzzPer = Math.PI * 10 / angles;
		float rotSpeed = 1F;
		float rotSpeedMod = 0.4F;

		for (int p = 0; p < 3; p++) {
			float baseAngle = rotSpeed * rotSpeedMod * (ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks);
			ms.mulPose(VecHelper.rotateY((ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks) * 0.25F * rotSpeed * rotSpeedMod));

			Matrix4f mat = ms.last().pose();
			tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			for (int i = 0; i < angles; i++) {
				int j = i;
				if (i % 2 == 0) {
					j--;
				}

				float ang = j * anglePer + baseAngle;
				float xp = (float) Math.cos(ang * Math.PI / 180F) * scale;
				float zp = (float) Math.sin(ang * Math.PI / 180F) * scale;
				float yo = (float) Math.sin(fuzzPer * j) * 1;

				float ut = ang * uPer;
				if (i % 2 == 0) {
					tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX).addVertex(mat, xp, yo + y0 + y, zp).setUv(ut, 1F);
					tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX).addVertex(mat, xp, yo + y0, zp).setUv(ut, 0);
				} else {
					tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX).addVertex(mat, xp, yo + y0, zp).setUv(ut, 0);
					tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX).addVertex(mat, xp, yo + y0 + y, zp).setUv(ut, 1F);
				}

			}
			tessellator.clear();

			switch (p) {
				case 0 -> {
					ms.mulPose(VecHelper.rotateX(20));
					RenderSystem.setShaderColor(1F, 0.4F, 0.4F, a);
					fuzzPer = Math.PI * 14 / angles;
					rotSpeed = 0.2F;
				}
				case 1 -> {
					ms.mulPose(VecHelper.rotateX(50));
					RenderSystem.setShaderColor(0.4F, 1F, 0.7F, a);
					fuzzPer = Math.PI * 6 / angles;
					rotSpeed = 2F;
				}
			}
		}
		ms.popPose();

		// === Rainbow
		ms.pushPose();
		GlStateManager._blendFuncSeparate(770, 771, 1, 0);
		RenderSystem.setShaderTexture(0, textureRainbow);
		scale = 10F;
		float effCelAng1 = celAng;
		if (effCelAng1 > 0.25F) {
			effCelAng1 = 1F - effCelAng1;
		}
		effCelAng1 = 0.25F - Math.min(0.25F, effCelAng1);

		long time = world.getDayTime() + 1000;
		int day = (int) (time / 24000L);
		Random rand = new Random(day * 0xFF);
		float angle1 = rand.nextFloat() * 360F;
		float angle2 = rand.nextFloat() * 360F;
		RenderSystem.setShaderColor(1F, 1F, 1F, effCelAng1 * (1F - insideVoid));
		ms.mulPose(VecHelper.rotateY(angle1));
		ms.mulPose(VecHelper.rotateZ(angle2));

		Matrix4f mat = ms.last().pose();
		tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		for (int i = 0; i < angles; i++) {
			int j = i;
			if (i % 2 == 0) {
				j--;
			}

			float ang = j * anglePer;
			float xp = (float) Math.cos(ang * Math.PI / 180F) * scale;
			float zp = (float) Math.sin(ang * Math.PI / 180F) * scale;
			float yo = 0;

			float ut = ang * uPer;
			if (i % 2 == 0) {
				tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX).addVertex(mat, xp, yo + y0 + y, zp).setUv(ut, 1F);
				tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX).addVertex(mat, xp, yo + y0, zp).setUv(ut, 0);
			} else {
				tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX).addVertex(mat, xp, yo + y0, zp).setUv(ut, 0);
				tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX).addVertex(mat, xp, yo + y0 + y, zp).setUv(ut, 1F);
			}

		}
		tessellator.clear();
		ms.popPose();
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F - insideVoid);
		GlStateManager._blendFuncSeparate(770, 1, 1, 0);
	}

	public static void renderStars(VertexBuffer starVBO, PoseStack ms, Matrix4f projMat, float partialTicks, Runnable resetFog) {
		FogRenderer.setupNoFog();
		Minecraft mc = Minecraft.getInstance();
		ShaderInstance shader = GameRenderer.getPositionShader();
		float rain = 1.0F - mc.level.getRainLevel(partialTicks);
		float celAng = mc.level.getTimeOfDay(partialTicks);
		float effCelAng = celAng;
		if (celAng > 0.5) {
			effCelAng = 0.5F - (celAng - 0.5F);
		}
		float alpha = rain * Math.max(0.1F, effCelAng * 2);

		float t = (ClientTickHandler.ticksInGame + partialTicks + 2000) * 0.005F;
		ms.pushPose();
		starVBO.bind();

		ms.pushPose();
		ms.mulPose(VecHelper.rotateY(t * 3));
		RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
		starVBO.drawWithShader(ms.last().pose(), projMat, shader);
		ms.popPose();

		ms.pushPose();
		ms.mulPose(VecHelper.rotateY(t));
		RenderSystem.setShaderColor(0.5F, 1F, 1F, alpha);
		starVBO.drawWithShader(ms.last().pose(), projMat, shader);
		ms.popPose();

		ms.pushPose();
		ms.mulPose(VecHelper.rotateY(t * 2));
		RenderSystem.setShaderColor(1F, 0.75F, 0.75F, alpha);
		starVBO.drawWithShader(ms.last().pose(), projMat, shader);
		ms.popPose();

		ms.pushPose();
		ms.mulPose(VecHelper.rotateZ(t * 3));
		RenderSystem.setShaderColor(1F, 1F, 1F, 0.25F * alpha);
		starVBO.drawWithShader(ms.last().pose(), projMat, shader);
		ms.popPose();

		ms.pushPose();
		ms.mulPose(VecHelper.rotateZ(t));
		RenderSystem.setShaderColor(0.5F, 1F, 1F, 0.25F * alpha);
		starVBO.drawWithShader(ms.last().pose(), projMat, shader);
		ms.popPose();

		ms.pushPose();
		ms.mulPose(VecHelper.rotateZ(t * 2));
		RenderSystem.setShaderColor(1F, 0.75F, 0.75F, 0.25F * alpha);
		starVBO.drawWithShader(ms.last().pose(), projMat, shader);
		ms.popPose();

		ms.popPose();
		VertexBuffer.unbind();
		resetFog.run();
	}

}
