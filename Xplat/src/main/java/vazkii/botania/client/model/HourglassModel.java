/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.RenderType;

import vazkii.botania.common.helper.VecHelper;

public class HourglassModel extends Model {

	private final ModelPart top;
	private final ModelPart glassT;
	private final ModelPart ring;
	private final ModelPart glassB;
	private final ModelPart bottom;

	private final ModelPart sandT;
	private final ModelPart sandB;

	public HourglassModel(ModelPart root) {
		super(RenderType::entityTranslucent);

		top = root.getChild("top");
		glassT = root.getChild("glass_top");
		ring = root.getChild("ring");
		glassB = root.getChild("glass_bottom");
		bottom = root.getChild("bottom");
		sandT = root.getChild("sand_top");
		sandB = root.getChild("sand_bottom");
	}

	public static MeshDefinition createMesh() {
		var mesh = new MeshDefinition();
		var root = mesh.getRoot();
		root.addOrReplaceChild("top", CubeListBuilder.create().texOffs(20, 0)
				.addBox(-3.0F, -6.5F, -3.0F, 6, 1, 6), PartPose.ZERO);
		root.addOrReplaceChild("glass_top", CubeListBuilder.create()
				.addBox(-2.5F, -5.5F, -2.5F, 5, 5, 5), PartPose.ZERO);
		root.addOrReplaceChild("ring", CubeListBuilder.create().texOffs(0, 20)
				.addBox(-1.5F, -0.5F, -1.5F, 3, 1, 3), PartPose.ZERO);
		root.addOrReplaceChild("glass_bottom", CubeListBuilder.create().texOffs(0, 10)
				.addBox(-2.5F, 0.5F, -2.5F, 5, 5, 5), PartPose.ZERO);
		root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(20, 7)
				.addBox(-3.0F, 5.5F, -3.0F, 6, 1, 6), PartPose.ZERO);
		root.addOrReplaceChild("sand_top", CubeListBuilder.create().texOffs(20, 14)
				.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4), PartPose.ZERO);
		root.addOrReplaceChild("sand_bottom", CubeListBuilder.create().texOffs(20, 14)
				.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4), PartPose.ZERO);
		return mesh;
	}

	@Override
	public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int light, int overlay, int color) {
		render(ms, buffer, light, overlay, color, 0, 1, false);
	}

	public void render(PoseStack ms, VertexConsumer buffer, int light, int overlay, int color, float fract1, float fract2, boolean flip) {
		if (flip) {
			float tmp = fract1;
			fract1 = fract2;
			fract2 = tmp;
		}

		float f = 1F / 16F;
		int alphaWhite = color | 0xffffff;
		ring.render(ms, buffer, light, overlay, alphaWhite);
		top.render(ms, buffer, light, overlay, alphaWhite);
		bottom.render(ms, buffer, light, overlay, alphaWhite);

		if (fract1 > 0) {
			ms.pushPose();
			if (flip) {
				ms.translate(-2.0F * f, 1.0F * f, -2.0F * f);
			} else {
				ms.mulPose(VecHelper.rotateZ(180F));
				ms.translate(-2.0F * f, -5.0F * f, -2.0F * f);
			}
			ms.scale(1F, fract1, 1F);
			sandT.render(ms, buffer, light, overlay, color);
			ms.popPose();
		}

		if (fract2 > 0) {
			ms.pushPose();
			if (flip) {
				ms.translate(-2.0F * f, -5.0F * f, -2.0F * f);
			} else {
				ms.mulPose(VecHelper.rotateZ(180F));
				ms.translate(-2.0F * f, 1.0F * f, -2.0F * f);
			}
			ms.scale(1F, fract2, 1F);
			sandB.render(ms, buffer, light, overlay, color);
			ms.popPose();
		}

		glassT.render(ms, buffer, light, overlay, alphaWhite);
		glassB.render(ms, buffer, light, overlay, alphaWhite);
	}

}
