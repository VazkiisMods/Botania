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

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.entity.PixieEntity;

public class PixieModel extends EntityModel<PixieEntity> {
	private final ModelPart body;
	private final ModelPart leftWingT;
	private final ModelPart leftWingB;
	private final ModelPart rightWingT;
	private final ModelPart rightWingB;

	private static boolean evil = false;

	private static RenderType pixieLayer(ResourceLocation texture) {
		return evil ? RenderHelper.getDopplegangerLayer(texture)
				: RenderType.entityCutoutNoCull(texture);
	}

	public PixieModel(ModelPart root) {
		super(PixieModel::pixieLayer);

		body = root.getChild("body");
		leftWingT = root.getChild("leftWingT");
		leftWingB = root.getChild("leftWingB");
		rightWingT = root.getChild("rightWingT");
		rightWingB = root.getChild("rightWingB");
	}

	public static MeshDefinition createMesh() {
		var mesh = new MeshDefinition();
		var root = mesh.getRoot();
		root.addOrReplaceChild("body", CubeListBuilder.create().addBox(-2.5F, 0.0F, -2.5F, 5, 5, 5), PartPose.offset(0.0F, 16.0F, 0.0F));
		root.addOrReplaceChild("leftWingT", CubeListBuilder.create().texOffs(0, 4)
				.addBox(0.0F, -5.0F, 0.0F, 0, 5, 6),
				PartPose.offsetAndRotation(2.5F, 18.0F, 0.5F, 0.2618F, 0.5236F, 0.2618F));
		root.addOrReplaceChild("leftWingB", CubeListBuilder.create().texOffs(0, 11)
				.addBox(0.0F, 0.0F, 0.0F, 0, 3, 4),
				PartPose.offsetAndRotation(2.5F, 18.0F, 0.5F, -0.2618F, 0.2618F, -0.2618F));
		root.addOrReplaceChild("rightWingT", CubeListBuilder.create().texOffs(0, 4)
				.addBox(0.0F, -5.0F, 0.0F, 0, 5, 6),
				PartPose.offsetAndRotation(-2.5F, 18.0F, 0.5F, 0.2618F, -0.5236F, -0.2618F));
		root.addOrReplaceChild("rightWingB", CubeListBuilder.create().texOffs(0, 11)
				.addBox(0.0F, 0.0F, 0.0F, 0, 3, 4),
				PartPose.offsetAndRotation(-2.5F, 18.0F, 0.5F, -0.2618F, -0.2618F, 0.2618F));
		return mesh;
	}

	@Override
	public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int light, int overlay, int color) {
		body.render(ms, buffer, light, overlay);

		leftWingT.render(ms, buffer, light, overlay);
		leftWingB.render(ms, buffer, light, overlay);
		rightWingT.render(ms, buffer, light, overlay);
		rightWingB.render(ms, buffer, light, overlay);
	}

	@Override
	public void setupAnim(PixieEntity entity, float f, float f1, float f2, float f3, float f4) {
		evil = entity.getPixieType() == 1;
		rightWingT.yRot = -(Mth.cos(f2 * 1.7F) * (float) Math.PI * 0.5F);
		leftWingT.yRot = Mth.cos(f2 * 1.7F) * (float) Math.PI * 0.5F;
		rightWingB.yRot = -(Mth.cos(f2 * 1.7F) * (float) Math.PI * 0.25F);
		leftWingB.yRot = Mth.cos(f2 * 1.7F) * (float) Math.PI * 0.25F;
	}

}
