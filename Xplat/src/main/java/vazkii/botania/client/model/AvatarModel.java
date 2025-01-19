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

public class AvatarModel extends Model {

	private final ModelPart body;
	private final ModelPart rightarm;
	private final ModelPart leftarm;
	private final ModelPart rightleg;
	private final ModelPart leftleg;
	private final ModelPart head;

	public AvatarModel(ModelPart root) {
		super(RenderType::entitySolid);
		leftleg = root.getChild("left_leg");
		rightarm = root.getChild("right_arm");
		leftarm = root.getChild("left_arm");
		head = root.getChild("head");
		rightleg = root.getChild("right_leg");
		body = root.getChild("body");
	}

	public static MeshDefinition createMesh() {
		var mesh = new MeshDefinition();
		var root = mesh.getRoot();
		root.addOrReplaceChild("head", CubeListBuilder.create()
				.addBox(-3.0F, -6.0F, -3.0F, 6, 6, 6),
				PartPose.offset(0.0F, 14.0F, 0.0F));
		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 12)
				.addBox(-3.0F, 0.0F, -2.0F, 6, 4, 4),
				PartPose.offset(0.0F, 14.0F, 0.0F));
		root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 20).mirror()
				.addBox(0.0F, -1.0F, -1.0F, 2, 6, 3),
				PartPose.offsetAndRotation(3.0F, 15.0F, -1.0F, 0.0F, -0.0F, -0.0873F));
		root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 20)
				.addBox(-2.0F, -1.0F, -1.0F, 2, 6, 3),
				PartPose.offsetAndRotation(-3.0F, 15.0F, -1.0F, 0.0F, -0.0F, 0.0873F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 20).mirror()
				.addBox(-1.5F, 0.0F, -1.5F, 3, 6, 3),
				PartPose.offset(1.5F, 18.0F, -0.5F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 20)
				.addBox(-1.5F, 0.0F, -1.5F, 3, 6, 3),
				PartPose.offset(-1.5F, 18.0F, -0.5F));
		return mesh;
	}

	@Override
	public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int light, int overlay, int color) {
		leftleg.render(ms, buffer, light, overlay, color);
		rightarm.render(ms, buffer, light, overlay, color);
		leftarm.render(ms, buffer, light, overlay, color);
		head.render(ms, buffer, light, overlay, color);
		rightleg.render(ms, buffer, light, overlay, color);
		body.render(ms, buffer, light, overlay, color);
	}

}
