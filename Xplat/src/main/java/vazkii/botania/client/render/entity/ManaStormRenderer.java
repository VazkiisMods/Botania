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
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.entity.ManaStormEntity;

public class ManaStormRenderer extends EntityRenderer<ManaStormEntity> {

	public ManaStormRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public void render(ManaStormEntity storm, float yaw, float partialTick, PoseStack ms, MultiBufferSource buffers, int light) {
		ms.pushPose();
		float maxScale = 1.95F;
		float scale = 0.05F + ((float) storm.burstsFired / ManaStormEntity.TOTAL_BURSTS - (storm.deathTime == 0 ? 0 : storm.deathTime + partialTick) / ManaStormEntity.DEATH_TIME) * maxScale;
		RenderHelper.renderStar(ms, buffers, 0x00FF00, scale, scale, scale, storm.getUUID().getMostSignificantBits(), partialTick);
		ms.popPose();
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(@NotNull ManaStormEntity entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}

}
