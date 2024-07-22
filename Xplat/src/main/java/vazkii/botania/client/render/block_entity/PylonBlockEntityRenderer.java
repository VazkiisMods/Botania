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

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.client.model.*;
import vazkii.botania.common.block.PylonBlock;
import vazkii.botania.common.block.block_entity.PylonBlockEntity;
import vazkii.botania.common.helper.VecHelper;

import java.util.Random;

public class PylonBlockEntityRenderer implements BlockEntityRenderer<PylonBlockEntity> {

	public static final ResourceLocation MANA_TEXTURE = new ResourceLocation(ResourcesLib.MODEL_PYLON_MANA);
	public static final ResourceLocation NATURA_TEXTURE = new ResourceLocation(ResourcesLib.MODEL_PYLON_NATURA);
	public static final ResourceLocation GAIA_TEXTURE = new ResourceLocation(ResourcesLib.MODEL_PYLON_GAIA);

	private final ManaPylonModel manaModel;
	private final NaturaPylonModel naturaModel;
	private final GaiaPylonModel gaiaModel;

	// Overrides for when we call this without an actual pylon
	private static PylonBlock.Variant forceVariant = PylonBlock.Variant.MANA;
	private static ItemDisplayContext forceTransform = ItemDisplayContext.NONE;

	public PylonBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		manaModel = new ManaPylonModel(ctx.bakeLayer(BotaniaModelLayers.PYLON_MANA));
		naturaModel = new NaturaPylonModel(ctx.bakeLayer(BotaniaModelLayers.PYLON_NATURA));
		gaiaModel = new GaiaPylonModel(ctx.bakeLayer(BotaniaModelLayers.PYLON_GAIA));
	}

	@Override
	public void render(@Nullable PylonBlockEntity pylon, float pticks, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		boolean renderingItem = pylon == null;
		boolean direct = renderingItem && (forceTransform == ItemDisplayContext.GUI || forceTransform.firstPerson()); // loosely based off ItemRenderer logic
		PylonBlock.Variant type = renderingItem ? forceVariant : ((PylonBlock) pylon.getBlockState().getBlock()).variant;
		PylonModel model;
		ResourceLocation texture;
		RenderType shaderLayer;
		switch (type) {
			default -> {
				model = manaModel;
				texture = MANA_TEXTURE;
				shaderLayer = direct ? RenderHelper.MANA_PYLON_GLOW_DIRECT : RenderHelper.MANA_PYLON_GLOW;
			}
			case NATURA -> {
				model = naturaModel;
				texture = NATURA_TEXTURE;
				shaderLayer = direct ? RenderHelper.NATURA_PYLON_GLOW_DIRECT : RenderHelper.NATURA_PYLON_GLOW;
			}
			case GAIA -> {
				model = gaiaModel;
				texture = GAIA_TEXTURE;
				shaderLayer = direct ? RenderHelper.GAIA_PYLON_GLOW_DIRECT : RenderHelper.GAIA_PYLON_GLOW;
			}
		}

		ms.pushPose();

		float worldTime = ClientTickHandler.ticksInGame + pticks;

		worldTime += pylon == null ? 0 : new Random(pylon.getBlockPos().hashCode()).nextInt(360);

		ms.translate(0, pylon == null ? 1.35 : 1.5, 0);
		ms.scale(1.0F, -1.0F, -1.0F);

		ms.pushPose();
		ms.translate(0.5F, 0F, -0.5F);
		if (pylon != null) {
			ms.mulPose(VecHelper.rotateY(worldTime * 1.5F));
		}

		RenderType layer = RenderType.entityTranslucent(texture);

		VertexConsumer buffer = buffers.getBuffer(layer);
		model.renderRing(ms, buffer, light, overlay);
		if (pylon != null) {
			ms.translate(0D, Math.sin(worldTime / 20D) / 20 - 0.025, 0D);
		}
		ms.popPose();

		ms.pushPose();
		if (pylon != null) {
			ms.translate(0D, Math.sin(worldTime / 20D) / 17.5, 0D);
		}

		ms.translate(0.5F, 0F, -0.5F);
		if (pylon != null) {
			ms.mulPose(VecHelper.rotateY(-worldTime));
		}

		buffer = buffers.getBuffer(shaderLayer);
		model.renderCrystal(ms, buffer, light, overlay);

		ms.popPose();

		ms.popPose();
	}

	public static class ItemRenderer extends BlockEntityItemRenderer {
		public ItemRenderer(Block block) {
			super(block);
		}

		@Override
		public void render(ItemStack stack, ItemDisplayContext type, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
			if (Block.byItem(stack.getItem()) instanceof PylonBlock pylon) {
				PylonBlockEntityRenderer.forceVariant = pylon.variant;
				PylonBlockEntityRenderer.forceTransform = type;
				super.render(stack, type, ms, buffers, light, overlay);
			}
		}
	}
}
