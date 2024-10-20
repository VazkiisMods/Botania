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
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.client.model.BotaniaModelLayers;
import vazkii.botania.client.model.HourglassModel;
import vazkii.botania.common.block.block_entity.HoveringHourglassBlockEntity;
import vazkii.botania.common.helper.VecHelper;

import java.util.Random;

public class HoveringHourglassBlockEntityRenderer implements BlockEntityRenderer<HoveringHourglassBlockEntity> {

	final ResourceLocation texture = ResourceLocation.parse(ResourcesLib.MODEL_HOURGLASS);
	private final HourglassModel model;

	public HoveringHourglassBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		model = new HourglassModel(ctx.bakeLayer(BotaniaModelLayers.HOURGLASS));
	}

	@Override
	public void render(@Nullable HoveringHourglassBlockEntity hourglass, float ticks, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		ms.pushPose();
		boolean hasWorld = hourglass != null && hourglass.getLevel() != null;
		int wtime = !hasWorld ? 0 : ClientTickHandler.ticksInGame;
		if (wtime != 0) {
			wtime += new Random(hourglass.getBlockPos().hashCode()).nextInt(360);
		}

		float time = wtime == 0 ? 0 : wtime + ticks;
		float x = 0.5F + (float) Math.cos(time * 0.05F) * 0.025F;
		float y = 0.55F + (float) (Math.sin(time * 0.04F) + 1F) * 0.05F;
		float z = 0.5F + (float) Math.sin(time * 0.05F) * 0.025F;
		ItemStack stack = hasWorld ? hourglass.getItemHandler().getItem(0) : ItemStack.EMPTY;

		float activeFraction = stack.isEmpty() ? 0 : hourglass.lastFraction + (hourglass.timeFraction - hourglass.lastFraction) * ticks;
		float fract1 = stack.isEmpty() ? 0 : activeFraction;
		float fract2 = stack.isEmpty() ? 0 : 1F - activeFraction;
		ms.translate(x, y, z);

		float rot = hasWorld && hourglass.flip ? 180F : 1F;
		if (hasWorld && hourglass.flipTicks > 0) {
			rot += (hourglass.flipTicks - ticks) * (180F / 4F);
		}
		ms.mulPose(VecHelper.rotateZ(rot));

		ms.scale(1F, -1F, -1F);
		int color = hasWorld ? hourglass.getColor() : 0;
		float r = (color >> 16) / 255.0F;
		float g = (color >> 8) / 255.0F;
		float b = (color & 0xFF) / 255.0F;
		VertexConsumer buffer = buffers.getBuffer(model.renderType(texture));
		model.render(ms, buffer, light, overlay, r, g, b, 1, fract1, fract2, hasWorld && hourglass.flip);
		ms.popPose();
	}

}
