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
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.RenderType;

/**
 * @author wiiv
 */
public class CloakModel extends Model {

	private final ModelPart collar;
	private final ModelPart sideL;
	private final ModelPart sideR;

	public CloakModel(ModelPart root) {
		super(RenderType::entityCutout);

		collar = root.getChild("collar");
		sideL = root.getChild("sideL");
		sideR = root.getChild("sideR");
	}

	public static MeshDefinition createMesh() {
		var mesh = new MeshDefinition();
		var root = mesh.getRoot();
		var deformation = new CubeDeformation(0.01F);
		root.addOrReplaceChild("collar", CubeListBuilder.create()
				.addBox(-5.5F, 0.0F, -1.5F, 11, 5, 11, deformation),
				PartPose.offsetAndRotation(0.0F, -3.0F, -4.5F, 0.0873F, 0.0F, 0.0F));
		root.addOrReplaceChild("sideL", CubeListBuilder.create().texOffs(0, 16).mirror()
				.addBox(-0.5F, -0.5F, -5.5F, 11, 21, 10, deformation),
				PartPose.rotation(0.0873F, -0.0873F, -0.1745F));
		root.addOrReplaceChild("sideR", CubeListBuilder.create().texOffs(0, 16)
				.addBox(-10.5F, -0.5F, -5.5F, 11, 21, 10, deformation),
				PartPose.rotation(0.0873F, 0.0873F, 0.1745F));
		return mesh;
	}

	@Override
	public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int light, int overlay, int color) {
		collar.render(ms, buffer, light, overlay, color);
		sideL.render(ms, buffer, light, overlay, color);
		sideR.render(ms, buffer, light, overlay, color);
	}
}
