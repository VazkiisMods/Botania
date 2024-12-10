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
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import vazkii.botania.api.state.BotaniaStateProperties;
import vazkii.botania.api.state.enums.AlfheimPortalState;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.common.block.block_entity.AlfheimPortalBlockEntity;
import vazkii.botania.common.helper.VecHelper;

import java.util.Objects;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class AlfheimPortalBlockEntityRenderer implements BlockEntityRenderer<AlfheimPortalBlockEntity> {
	private final TextureAtlasSprite portalSprite;

	public AlfheimPortalBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		this.portalSprite = Objects.requireNonNull(
				Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
						.apply(botaniaRL("block/alfheim_portal_swirl"))
		);
	}

	@Override
	public void render(@NotNull AlfheimPortalBlockEntity portal, float f, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		AlfheimPortalState state = portal.getBlockState().getValue(BotaniaStateProperties.ALFPORTAL_STATE);
		if (state == AlfheimPortalState.OFF) {
			return;
		}

		float alpha = (float) Math.min(1F, (Math.sin((ClientTickHandler.ticksInGame + f) / 8D) + 1D) / 7D + 0.6D) * (Math.min(60, portal.ticksOpen) / 60F) * 0.5F;

		ms.pushPose();
		if (state == AlfheimPortalState.ON_X) {
			ms.translate(0.3125, 1, 2);
			ms.mulPose(VecHelper.rotateY(90));
		} else {
			ms.translate(-1, 1, 0.3125);
		}
		renderIcon(ms, buffers, this.portalSprite, 0, 0, 3, 3, alpha, overlay);
		ms.popPose();

		ms.pushPose();
		if (state == AlfheimPortalState.ON_X) {
			ms.translate(0.6875, 1, -1);
			ms.mulPose(VecHelper.rotateY(90));
		} else {
			ms.translate(2, 1, 0.6875);
		}
		ms.mulPose(VecHelper.rotateY(180));
		renderIcon(ms, buffers, this.portalSprite, 0, 0, 3, 3, alpha, overlay);
		ms.popPose();
	}

	public void renderIcon(PoseStack ms, MultiBufferSource buffers, TextureAtlasSprite icon, int x, int y, int width, int height, float alpha, int overlay) {
		VertexConsumer buffer = buffers.getBuffer(Sheets.translucentItemSheet());
		Matrix4f model = ms.last().pose();
		Matrix3f normal = ms.last().normal();
		buffer.addVertex(model, x, y + height, 0).setColor(1, 1, 1, alpha).setUv(icon.getU0(), icon.getV1()).setOverlay(overlay).uv2(0xF000F0).setNormal(normal, 1, 0, 0);
		buffer.addVertex(model, x + width, y + height, 0).setColor(1, 1, 1, alpha).setUv(icon.getU1(), icon.getV1()).setOverlay(overlay).uv2(0xF000F0).setNormal(normal, 1, 0, 0);
		buffer.addVertex(model, x + width, y, 0).setColor(1, 1, 1, alpha).setUv(icon.getU1(), icon.getV0()).setOverlay(overlay).uv2(0xF000F0).setNormal(normal, 1, 0, 0);
		buffer.addVertex(model, x, y, 0).setColor(1, 1, 1, alpha).setUv(icon.getU0(), icon.getV0()).setOverlay(overlay).uv2(0xF000F0).setNormal(normal, 1, 0, 0);
	}

}
