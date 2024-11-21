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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.block.block_entity.red_string.RedStringBlockEntity;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.item.WandOfTheForestItem;

import java.util.Random;

public class RedStringBlockEntityRenderer<T extends RedStringBlockEntity> implements BlockEntityRenderer<T> {
	// 0 -> none, 10 -> full
	private static int transparency = 0;

	public static void tick() {
		Player player = Minecraft.getInstance().player;
		boolean hasWand = player != null && PlayerHelper.hasHeldItemClass(player, WandOfTheForestItem.class);
		if (transparency > 0 && !hasWand) {
			transparency--;
		} else if (transparency < 10 && hasWand) {
			transparency++;
		}
	}

	public RedStringBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(RedStringBlockEntity tile, float partialTicks, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		if (transparency <= 0) {
			return;
		}

		float sizeAlpha = transparency / 10.0F;
		int color = 0xFF0000 | ((int) (sizeAlpha * 255) << 24);

		Direction dir = tile.getOrientation();
		BlockPos bind = tile.getBinding();

		if (bind != null) {
			ms.pushPose();
			ms.translate(0.5, 0.5, 0.5);
			Vec3 span = new Vec3(bind.getX() - tile.getBlockPos().getX(), bind.getY() - tile.getBlockPos().getY(), bind.getZ() - tile.getBlockPos().getZ());
			Vec3 step = span.normalize().scale(0.025);
			Vec3 cur = step;

			int stepCount = (int) (span.length() / step.length());

			double len = (double) -ClientTickHandler.ticksInGame / 100F + new Random(dir.ordinal() ^ tile.getBlockPos().hashCode()).nextInt(10000);
			double add = step.length();
			double rand = Math.random() - 0.5;
			VertexConsumer buffer = buffers.getBuffer(RenderHelper.RED_STRING);
			for (int i = 0; i < stepCount; i++) {
				vertex(ms, buffer, color, dir, cur.x, cur.y, cur.z, rand, len);
				rand = Math.random() - 0.5;
				cur = cur.add(step);
				len += add;
				vertex(ms, buffer, color, dir, cur.x, cur.y, cur.z, rand, len);
			}

			ms.popPose();
		}
	}

	/**
	 * Add a vertex at the given position, but spiraled out perpendicular to {@code dir}
	 */
	private static void vertex(PoseStack ms, VertexConsumer buffer, int color, Direction dir,
			double xpos, double ypos, double zpos,
			double rand, double l) {
		float sizeAlpha = transparency / 10.0F;
		float ampl = (float) (0.15 * (Mth.sin((float) l * 2F) * 0.5 + 0.5) + 0.1) * sizeAlpha;

		float trigInput = (float) (l * 20.0);
		float sin = Mth.sin(trigInput);
		float cos = Mth.cos(trigInput);
		float lastTerm = (float) (rand * 0.05);

		float x = (float) xpos
				+ sin * ampl * killNonZero(dir.getStepX())
				+ lastTerm;
		float y = (float) ypos
				+ cos * ampl * killNonZero(dir.getStepY())
				+ lastTerm;
		float z = (float) zpos
				+ (dir.getStepY() == 0 ? sin : cos) * ampl * killNonZero(dir.getStepZ())
				+ lastTerm;

		int a = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = color & 0xFF;
		buffer.addVertex(ms.last().pose(), x, y, z).setColor(r, g, b, a);
		switch (dir.getAxis().getPlane()) {
			case HORIZONTAL -> buffer.setNormal(ms.last(), 0, 1, 0);
			case VERTICAL -> buffer.setNormal(ms.last(), 1, 0, 0);
		}
	}

	private static int killNonZero(int diff) {
		if (diff != 0) {
			return 0;
		} else {
			return 1;
		}
	}

}
