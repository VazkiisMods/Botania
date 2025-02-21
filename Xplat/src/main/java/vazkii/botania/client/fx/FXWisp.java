/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.fx;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import vazkii.botania.xplat.ClientXplatAbstractions;

public class FXWisp extends TextureSheetParticle {
	private final boolean depthTest;
	private final float moteParticleScale;
	private final int moteHalfLife;

	public FXWisp(ClientLevel world, double d, double d1, double d2, double xSpeed, double ySpeed, double zSpeed,
			float size, float red, float green, float blue, boolean depthTest, float maxAgeMul, boolean noClip, float g) {
		super(world, d, d1, d2);
		// super applies wiggle to motion so set it here instead
		xd = xSpeed;
		yd = ySpeed;
		zd = zSpeed;
		rCol = red;
		gCol = green;
		bCol = blue;
		alpha = 0.375F;
		gravity = g;
		quadSize = (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F * size;
		moteParticleScale = quadSize;
		lifetime = (int) (28D / (Math.random() * 0.3D + 0.7D) * maxAgeMul);
		this.depthTest = depthTest;

		moteHalfLife = lifetime / 2;
		setSize(0.01F, 0.01F);

		xo = x;
		yo = y;
		zo = z;
		this.hasPhysics = !noClip;
	}

	@Override
	public float getQuadSize(float p_217561_1_) {
		float agescale = (float) age / (float) moteHalfLife;
		if (agescale > 1F) {
			agescale = 2 - agescale;
		}

		quadSize = moteParticleScale * agescale * 0.5F;
		return quadSize;
	}

	@Override
	protected int getLightColor(float partialTicks) {
		return 0xF000F0;
	}

	@NotNull
	@Override
	public ParticleRenderType getRenderType() {
		return depthTest ? NORMAL_RENDER : DIW_RENDER;
	}

	// [VanillaCopy] of super, without drag when onGround is true
	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.age++ >= this.lifetime) {
			this.remove();
		}

		this.yd -= this.gravity;
		this.move(this.xd, this.yd, this.zd);
		if (gravity == 0.0f) {
			this.xd *= 0.98;
			this.yd *= 0.98;
			this.zd *= 0.98;
		}
	}

	public void setGravity(float value) {
		gravity = value;
	}

	private static BufferBuilder beginRenderCommon(Tesselator tesselator, TextureManager textureManager) {
		Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
		AbstractTexture tex = textureManager.getTexture(TextureAtlas.LOCATION_PARTICLES);
		ClientXplatAbstractions.INSTANCE.setFilterSave(tex, true, false);
		return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
	}

	//TODO there's end method in ParticleRenderType anymore. Check where the method below should be used instead
	private static void endRenderCommon() {
		AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_PARTICLES);
		ClientXplatAbstractions.INSTANCE.restoreLastFilter(tex);
		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
	}

	public static final ParticleRenderType NORMAL_RENDER = new ParticleRenderType() {
		@Override
		public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
			RenderSystem.enableDepthTest();
			return beginRenderCommon(tesselator, textureManager);
		}

		/*
		@Override
		public void end(Tesselator tessellator) {
			tessellator.end();
			endRenderCommon();
		}
		
		 */

		@Override
		public String toString() {
			return "botania:wisp";
		}
	};

	public static final ParticleRenderType DIW_RENDER = new ParticleRenderType() {
		@Override
		public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
			RenderSystem.disableDepthTest();
			return beginRenderCommon(tesselator, textureManager);

		}

		/*
		@Override
		public void end(Tesselator tessellator) {
			tessellator.end();
			RenderSystem.enableDepthTest();
			endRenderCommon();
		}
		
		 */

		@Override
		public String toString() {
			return "botania:depth_ignoring_wisp";
		}
	};
}
