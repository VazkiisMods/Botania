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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.block.LuminizerBlock;
import vazkii.botania.common.block.block_entity.LuminizerBlockEntity;
import vazkii.botania.common.helper.VecHelper;
import vazkii.botania.common.item.equipment.bauble.ManaseerMonocleItem;

import java.util.Objects;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class LuminizerBlockEntityRenderer implements BlockEntityRenderer<LuminizerBlockEntity> {
	private final TextureAtlasSprite luminizerWorldSprite;
	private final TextureAtlasSprite detectorLuminizerWorldSprite;
	private final TextureAtlasSprite forkLuminizerWorldSprite;
	private final TextureAtlasSprite toggleLuminizerWorldSprite;

	public LuminizerBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		var atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
		this.luminizerWorldSprite = Objects.requireNonNull(atlas.apply(botaniaRL("block/light_relay")));
		this.detectorLuminizerWorldSprite = Objects.requireNonNull(atlas.apply(botaniaRL("block/detector_light_relay")));
		this.forkLuminizerWorldSprite = Objects.requireNonNull(atlas.apply(botaniaRL("block/fork_light_relay")));
		this.toggleLuminizerWorldSprite = Objects.requireNonNull(atlas.apply(botaniaRL("block/toggle_light_relay")));
	}

	@Override
	public void render(@NotNull LuminizerBlockEntity tile, float pticks, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		BlockState state = tile.getBlockState();

		Minecraft mc = Minecraft.getInstance();

		if (mc.getCameraEntity() instanceof LivingEntity view) {
			if (ManaseerMonocleItem.hasMonocle(view) && SpecialFlowerBlockEntityRenderer.hasBindingAttempt(view, tile.getBlockPos())) {
				SpecialFlowerBlockEntityRenderer.renderRadius(tile, ms, buffers, new RadiusDescriptor.Circle(tile.getBlockPos(), LuminizerBlockEntity.MAX_DIST));
			}
		}

		TextureAtlasSprite sprite = switch (((LuminizerBlock) state.getBlock()).variant) {
			case DEFAULT -> this.luminizerWorldSprite;
			case DETECTOR -> this.detectorLuminizerWorldSprite;
			case FORK -> this.forkLuminizerWorldSprite;
			case TOGGLE -> this.toggleLuminizerWorldSprite;
		};

		ms.pushPose();
		ms.translate(0.5, 0.3, 0.5);

		double time = ClientTickHandler.ticksInGame + pticks;

		float scale = 0.75F;
		ms.scale(scale, scale, scale);

		ms.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
		ms.mulPose(VecHelper.rotateY(180.0F));

		float off = 0.25F;
		ms.translate(0F, off, 0F);
		ms.mulPose(VecHelper.rotateZ((float) time));
		ms.translate(0F, -off, 0F);

		VertexConsumer buffer = buffers.getBuffer(RenderHelper.LIGHT_RELAY);
		renderIcon(ms, buffer, sprite);

		ms.popPose();
	}

	private void renderIcon(PoseStack ms, VertexConsumer buffer, TextureAtlasSprite icon) {
		float size = icon.getU1() - icon.getU0();
		float pad = size / 8F;
		float f = icon.getU0() + pad;
		float f1 = icon.getU1() - pad;
		float f2 = icon.getV0() + pad;
		float f3 = icon.getV1() - pad;

		float f4 = 1.0F;
		float f5 = 0.5F;
		float f6 = 0.25F;

		Matrix4f mat = ms.last().pose();
		buffer.vertex(mat, 0.0F - f5, 0.0F - f6, 0.0F).color(1F, 1F, 1F, 1F).uv(f, f3).endVertex();
		buffer.vertex(mat, f4 - f5, 0.0F - f6, 0.0F).color(1F, 1F, 1F, 1F).uv(f1, f3).endVertex();
		buffer.vertex(mat, f4 - f5, f4 - f6, 0.0F).color(1F, 1F, 1F, 1F).uv(f1, f2).endVertex();
		buffer.vertex(mat, 0.0F - f5, f4 - f6, 0.0F).color(1F, 1F, 1F, 1F).uv(f, f2).endVertex();

	}

}
