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

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.client.core.helper.CoreShaders;
import vazkii.botania.client.core.proxy.ClientProxy;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.client.model.BotaniaModelLayers;
import vazkii.botania.client.model.PixieModel;
import vazkii.botania.common.entity.PixieEntity;

public class PixieRenderer extends MobRenderer<PixieEntity, PixieModel> {

	public PixieRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new PixieModel(ctx.bakeLayer(BotaniaModelLayers.PIXIE)), 0.0F);
	}

	@Override
	public void render(PixieEntity mob, float yaw, float partialTicks, PoseStack pos, MultiBufferSource buffers, int light) {
		ShaderInstance shader = CoreShaders.doppleganger();
		if (shader != null) {
			shader.safeGetUniform("BotaniaDisfiguration").set(GaiaGuardianRenderer.DEFAULT_DISFIGURATION);
			shader.safeGetUniform("BotaniaGrainIntensity").set(GaiaGuardianRenderer.DEFAULT_GRAIN_INTENSITY);
		}
		super.render(mob, yaw, partialTicks, pos, buffers, light);
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(@NotNull PixieEntity entity) {
		return ClientProxy.dootDoot
				? ResourceLocation.parse(ResourcesLib.MODEL_PIXIE_HALLOWEEN)
				: ResourceLocation.parse(ResourcesLib.MODEL_PIXIE);
	}
}
