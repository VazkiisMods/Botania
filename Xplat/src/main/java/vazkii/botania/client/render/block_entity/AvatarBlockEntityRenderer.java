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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.item.AvatarWieldable;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.client.model.AvatarModel;
import vazkii.botania.client.model.BotaniaModelLayers;
import vazkii.botania.common.block.block_entity.AvatarBlockEntity;
import vazkii.botania.common.helper.VecHelper;
import vazkii.botania.xplat.XplatAbstractions;

public class AvatarBlockEntityRenderer implements BlockEntityRenderer<AvatarBlockEntity> {

	private static final float[] ROTATIONS = new float[] {
			180F, 0F, 90F, 270F
	};

	private static final ResourceLocation texture = ResourceLocation.parse(ResourcesLib.MODEL_AVATAR);
	private final AvatarModel model;

	public AvatarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		model = new AvatarModel(context.bakeLayer(BotaniaModelLayers.AVATAR));
	}

	@Override
	public void render(@Nullable AvatarBlockEntity avatar, float pticks, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		ms.pushPose();
		Direction facing = avatar != null && avatar.getLevel() != null ? avatar.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING) : Direction.SOUTH;

		ms.translate(0.5F, 1.6F, 0.5F);
		ms.scale(1F, -1F, -1F);
		ms.mulPose(VecHelper.rotateY(ROTATIONS[Math.max(Math.min(ROTATIONS.length - 1, facing.get3DDataValue() - 2), 0)]));
		VertexConsumer buffer = buffers.getBuffer(model.renderType(texture));
		model.renderToBuffer(ms, buffer, light, overlay, 0xFFFFFF);

		if (avatar != null) {
			ItemStack stack = avatar.getItemHandler().getItem(0);
			if (!stack.isEmpty()) {
				ms.pushPose();
				float s = 0.6F;
				ms.scale(s, s, s);
				ms.translate(-0.5F, 2F, -0.25F);
				ms.mulPose(VecHelper.rotateX(-70));
				Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
						light, overlay, ms, buffers, avatar.getLevel(), 0);
				ms.popPose();

				AvatarWieldable wieldable = XplatAbstractions.INSTANCE.findAvatarWieldable(stack);
				if (wieldable != null) {
					buffer = buffers.getBuffer(RenderType.entityTranslucent(wieldable.getOverlayResource(avatar)));
					s = 1.01F;

					ms.pushPose();
					ms.scale(s, s, s);
					ms.translate(0F, -0.01F, 0F);
					float alpha = (float) Math.sin(ClientTickHandler.ticksInGame / 20D) / 2F + 0.5F;
					model.renderToBuffer(ms, buffer, 0xF000F0, overlay, FastColor.ARGB32.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F));
					ms.popPose();
				}
			}
		}
		ms.popPose();
	}

}
