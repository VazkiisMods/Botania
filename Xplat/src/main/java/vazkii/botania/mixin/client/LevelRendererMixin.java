/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.botania.client.core.SkyblockWorldInfo;
import vazkii.botania.client.render.world.SkyblockSkyRenderer;
import vazkii.botania.client.render.world.WorldOverlays;
import vazkii.botania.xplat.BotaniaConfig;

/**
 * This Mixin implements the Garden of Glass skybox and some in-world overlays.
 * It needs to be applied before similar mixins from other mods (e.g. Enhanced Celestials, BedrockIfy) to prevent those
 * from removing our injection points. These changes are compatible with those mods, assuming proper sequencing.
 */
@Mixin(value = LevelRenderer.class, priority = 900)
public class LevelRendererMixin {
	@Shadow
	@Nullable
	private VertexBuffer starBuffer;

	@Shadow
	@Final
	private RenderBuffers renderBuffers;
	@Shadow
	@Nullable
	private ClientLevel level;
	@Unique
	private static final Matrix4f SUN_SCALE = new Matrix4f().scale(2F, 1F, 2F);

	@Unique
	private static final Matrix4f MOON_SCALE = new Matrix4f().scale(1.5F, 1F, 1.5F);

	@Unique
	private static boolean isGogSky() {
		Level world = Minecraft.getInstance().level;
		boolean isGog = world.getLevelData() instanceof SkyblockWorldInfo skyblockInfo && skyblockInfo.isGardenOfGlass();
		return BotaniaConfig.client().enableFancySkybox()
				&& world.dimension() == Level.OVERWORLD
				&& (BotaniaConfig.client().enableFancySkyboxInNormalWorlds() || isGog);
	}

	/**
	 * Render planets and other extras, after the first invoke to ms.rotate(Y) after getRainLevel is called
	 */
	@Inject(
		method = "renderSky",
		slice = @Slice(
			from = @At(
				ordinal = 0, value = "INVOKE",
				target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"
			)
		),
		at = @At(
			shift = At.Shift.AFTER,
			ordinal = 0,
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V"
		),
		require = 0
	)
	private void renderExtras(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera,
			boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) {
		if (isGogSky()) {
			PoseStack poseStack = new PoseStack();
			poseStack.mulPose(frustumMatrix);
			SkyblockSkyRenderer.renderExtra(poseStack, Minecraft.getInstance().level, partialTick, 0);
		}
	}

	/**
	 * Make the sun bigger by scaling it to double size
	 */
	@ModifyVariable(
		method = "renderSky",
		slice = @Slice(
			from = @At(ordinal = 1, value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getTimeOfDay(F)F"),
			to = @At(ordinal = 0, value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V")
		),
		at = @At(value = "CONSTANT", args = "floatValue=30.0"),
		ordinal = 1,
		require = 0
	)
	private Matrix4f makeSunBigger(Matrix4f matrix) {
		if (isGogSky()) {
			// copies the matrix
			matrix = new Matrix4f(matrix);
			matrix.mul(SUN_SCALE);
		}
		return matrix;
	}

	/**
	 * Make the moon bigger by scaling it to triple size (the matrix is already scaled on the call above)
	 */
	@ModifyVariable(
		method = "renderSky",
		slice = @Slice(
			from = @At(ordinal = 0, value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"),
			to = @At(ordinal = 1, value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V")
		),
		at = @At(value = "CONSTANT", args = "floatValue=20.0"),
		ordinal = 1,
		require = 0
	)
	private Matrix4f makeMoonBigger(Matrix4f matrix) {
		if (isGogSky()) {
			matrix.mul(MOON_SCALE);
		}
		return matrix;
	}

	/**
	 * Render lots of extra stars
	 */
	@Inject(
		method = "renderSky",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F"),
		require = 0
	)
	private void renderExtraStars(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera,
			boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) {
		if (isGogSky()) {
			PoseStack poseStack = new PoseStack();
			poseStack.mulPose(frustumMatrix);
			SkyblockSkyRenderer.renderStars(starBuffer, poseStack, projectionMatrix, partialTick, skyFogSetup);
		}
	}

	@SuppressWarnings("mapping") // applyModelViewMatrix is unobfuscated - not mapped on Fabric, mapped on Forge
	@Inject(
		method = "renderLevel",
		at = @At(
			shift = At.Shift.AFTER,
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/systems/RenderSystem;applyModelViewMatrix()V",
			ordinal = 0 // after debugRenderer, before a long sequence of endBatch calls
		)
	)
	private void renderOverlays(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera,
			GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
		// Called from our own mixin instead of e.g. Forge's render world last event,
		// because that event is too late for Fabulous mode
		PoseStack poseStack = new PoseStack();
		WorldOverlays.renderWorldLast(camera, deltaTracker.getGameTimeDeltaPartialTick(false), poseStack, renderBuffers, level);
	}
}
