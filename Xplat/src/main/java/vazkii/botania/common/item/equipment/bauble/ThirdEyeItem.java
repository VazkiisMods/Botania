/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.bauble;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.handler.MiscellaneousModels;
import vazkii.botania.client.render.AccessoryRenderRegistry;
import vazkii.botania.client.render.AccessoryRenderer;
import vazkii.botania.common.entity.MagicMissileEntity;
import vazkii.botania.common.proxy.Proxy;

import java.util.List;

public class ThirdEyeItem extends BaubleItem {

	private static final int COST = 2;

	public ThirdEyeItem(Properties props) {
		super(props);
		Proxy.INSTANCE.runOnClient(() -> () -> AccessoryRenderRegistry.register(this, new Renderer()));
	}

	@Override
	public void onWornTick(ItemStack stack, LivingEntity living) {
		if (!(living instanceof Player eplayer)) {
			return;
		}

		double range = 24;
		AABB aabb = new AABB(living.getX(), living.getY(), living.getZ(), living.getX(), living.getY(), living.getZ()).inflate(range);
		List<LivingEntity> mobs = living.level().getEntitiesOfClass(LivingEntity.class, aabb, MagicMissileEntity.targetPredicate(living));

		for (LivingEntity e : mobs) {
			MobEffectInstance potion = e.getEffect(MobEffects.GLOWING);
			if ((potion == null || potion.getDuration() <= 2) && ManaItemHandler.instance().requestManaExact(stack, eplayer, COST, true)) {
				e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 12, 0));
			}
		}
	}

	public static class Renderer implements AccessoryRenderer {

		public static final int NUM_LAYERS = 3;

		@Override
		public void doRender(HumanoidModel<?> bipedModel, ItemStack stack, LivingEntity living, PoseStack ms, MultiBufferSource buffers, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
			boolean armor = !living.getItemBySlot(EquipmentSlot.CHEST).isEmpty();

			for (int i = 0; i < NUM_LAYERS; i++) {
				ms.pushPose();
				bipedModel.body.translateAndRotate(ms);

				switch (i) {
					case 0:
						break;
					case 1:
						double time = (ClientTickHandler.ticksInGame + partialTicks) * 0.12;
						double dist = 0.05;
						ms.translate(Math.sin(time) * dist, Math.cos(time * 0.5) * dist, 0);

						ms.scale(0.75F, 0.75F, 1F);
						ms.translate(0, 0.1, -0.025);
						break;
					case 2:
						ms.translate(0, 0, -0.05);
						break;
				}

				ms.translate(-0.3, 0.6, armor ? 0.10 : 0.15);
				ms.scale(0.6F, -0.6F, -0.6F);
				BakedModel model = MiscellaneousModels.INSTANCE.thirdEyeLayers[i];
				VertexConsumer buffer = buffers.getBuffer(Sheets.cutoutBlockSheet());
				Minecraft.getInstance().getBlockRenderer().getModelRenderer()
						.renderModel(ms.last(), buffer, null, model, 1, 1, 1, light, OverlayTexture.NO_OVERLAY);
				ms.popPose();
			}
		}
	}

}
