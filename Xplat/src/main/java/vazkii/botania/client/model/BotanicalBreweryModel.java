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

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.common.block.block_entity.BreweryBlockEntity;
import vazkii.botania.common.helper.VecHelper;

public class BotanicalBreweryModel extends Model {
	private static final ResourceLocation TEXTURE = ResourceLocation.parse(ResourcesLib.MODEL_BREWERY);
	final ModelPart top;
	final ModelPart pole;
	final ModelPart bottom;

	final ModelPart plate;

	public BotanicalBreweryModel(ModelPart root) {
		super(RenderType::entitySolid);

		top = root.getChild("top");
		pole = root.getChild("pole");
		bottom = root.getChild("bottom");
		plate = root.getChild("plate");
	}

	public static MeshDefinition createMesh() {
		var mesh = new MeshDefinition();
		var root = mesh.getRoot();
		root.addOrReplaceChild("top", CubeListBuilder.create().texOffs(8, 0)
				.addBox(-2.0F, -7.0F, -2.0F, 4, 1, 4),
				PartPose.offset(0.0F, 16.0F, 0.0F));
		root.addOrReplaceChild("pole", CubeListBuilder.create()
				.addBox(-1.0F, -6.0F, -1.0F, 2, 10, 2),
				PartPose.offset(0.0F, 16.0F, 0.0F));
		root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(8, 5)
				.addBox(-2.0F, 4.0F, -2.0F, 4, 1, 4),
				PartPose.offset(0.0F, 16.0F, 0.0F));
		root.addOrReplaceChild("plate", CubeListBuilder.create().texOffs(8, 5)
				.addBox(5.0F, 0.0F, -2.0F, 4, 1, 4),
				PartPose.offset(0.0F, 17.0F, 0.0F));
		return mesh;
	}

	public void render(@Nullable BreweryBlockEntity brewery, double time, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		float offset = (float) Math.sin(time / 40) * 0.1F + 0.05F;
		boolean hasTile = brewery != null;
		int plates = hasTile ? brewery.inventorySize() - 1 : 7;
		float deg = (float) time / 16F;
		float polerot = -deg * 25F;

		ms.translate(0F, offset, 0F);
		ms.mulPose(VecHelper.rotateY(polerot));
		if (hasTile && !brewery.getItemHandler().getItem(0).isEmpty()) {
			ms.pushPose();
			ms.mulPose(VecHelper.rotateX(180));
			ms.translate(0, -0.45F, 0);
			renderItemStack(brewery.getItemHandler().getItem(0), ms, buffers, brewery.getLevel(), light, overlay);
			ms.popPose();
		}

		RenderType layer = renderType(TEXTURE);
		pole.render(ms, buffers.getBuffer(layer), light, overlay);
		top.render(ms, buffers.getBuffer(layer), light, overlay);
		bottom.render(ms, buffers.getBuffer(layer), light, overlay);
		ms.mulPose(VecHelper.rotateY(-polerot));

		float degper = (float) (2F * Math.PI) / plates;
		for (int i = 0; i < plates; i++) {
			plate.yRot = deg;
			float offset1 = (float) Math.sin(time / 20 + i * 40F) * 0.2F - 0.2F;
			if (time == -1) {
				offset1 = 0F;
			}

			ms.translate(0F, offset1, 0F);
			if (hasTile && !brewery.getItemHandler().getItem(i + 1).isEmpty()) {
				float rot = plate.yRot * 180F / (float) Math.PI;
				float transX = 0.3125F;
				float transY = 1.06F;
				float transZ = 0.1245F;
				ms.pushPose();
				ms.mulPose(VecHelper.rotateY(rot));
				ms.translate(transX, transY, transZ);
				ms.mulPose(VecHelper.rotateX(-90F));
				ms.translate(0.125F, 0.125F, 0);
				renderItemStack(brewery.getItemHandler().getItem(i + 1), ms, buffers, brewery.getLevel(), light, overlay);
				ms.popPose();
			}
			plate.render(ms, buffers.getBuffer(layer), light, overlay);
			ms.translate(0F, -offset1, 0F);

			deg += degper;
		}
		ms.translate(0F, -offset, 0F);
	}

	private void renderItemStack(ItemStack stack, PoseStack ms, MultiBufferSource buffers, @Nullable Level level, int light, int overlay) {
		if (!stack.isEmpty()) {
			Minecraft mc = Minecraft.getInstance();
			ms.pushPose();

			float s = 0.25F;
			ms.scale(s, s, s);
			mc.getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND,
					light, overlay, ms, buffers, level, 0);
			ms.popPose();
		}
	}

	@Override
	public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a) {
		throw new UnsupportedOperationException("unimplemented, call using other render method");
	}
}
