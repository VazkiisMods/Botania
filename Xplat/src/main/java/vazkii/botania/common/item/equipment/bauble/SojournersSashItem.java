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
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.core.helper.AccessoryRenderHelper;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.client.render.AccessoryRenderRegistry;
import vazkii.botania.client.render.AccessoryRenderer;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.proxy.Proxy;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.UUID;

public class SojournersSashItem extends BaubleItem {

	private static final UUID STEP_BOOST_UUID = UUID.fromString("8511cd62-2650-4078-8d69-9ebe80b21eb5");
	private static final AttributeModifier STEP_BOOST = new AttributeModifier(
			STEP_BOOST_UUID,
			"botania:travel_belt",
			0.65, AttributeModifier.Operation.ADDITION);

	private static final ResourceLocation texture = new ResourceLocation(ResourcesLib.MODEL_TRAVEL_BELT);

	private static final int COST = 1;
	private static final int COST_INTERVAL = 10;

	public final float speed;
	public final float jump;
	public final float fallBuffer;

	public SojournersSashItem(Properties props) {
		this(props, 0.035F, 0.2F, 2F);
	}

	public static float onPlayerFall(Player entity, float dist) {
		boolean pendantJump = CirrusAmuletItem.popJumping(entity);
		ItemStack stack = EquipmentHandler.findOrEmpty(s -> s.getItem() instanceof SojournersSashItem, entity);

		if (!stack.isEmpty()) {
			float fallBuffer = ((SojournersSashItem) stack.getItem()).fallBuffer;

			if (pendantJump) {
				ItemStack amulet = EquipmentHandler.findOrEmpty(s -> s.getItem() instanceof CirrusAmuletItem, entity);
				if (!amulet.isEmpty()) {
					fallBuffer *= ((CirrusAmuletItem) amulet.getItem()).getMaxAllowedJumps();
				}
			}

			return Math.max(0, dist - fallBuffer);
		}
		return dist;
	}

	public SojournersSashItem(Properties props, float speed, float jump, float fallBuffer) {
		super(props);
		this.speed = speed;
		this.jump = jump;
		this.fallBuffer = fallBuffer;
		Proxy.INSTANCE.runOnClient(() -> () -> AccessoryRenderRegistry.register(this, new Renderer()));
	}

	public static void tickBelt(Player player) {
		ItemStack belt = EquipmentHandler.findOrEmpty(s -> s.getItem() instanceof SojournersSashItem, player);

		var stepHeight = XplatAbstractions.INSTANCE.getStepHeightAttribute();
		AttributeInstance attrib = player.getAttribute(stepHeight);
		boolean hasBoost = attrib.hasModifier(STEP_BOOST);

		if (tryConsumeMana(player)) {
			if (player.level().isClientSide) {
				SojournersSashItem beltItem = (SojournersSashItem) belt.getItem();
				if ((player.onGround() || player.getAbilities().flying) && player.zza > 0F && !player.isInWaterOrBubble()) {
					float speed = beltItem.getSpeed(belt);
					player.moveRelative(player.getAbilities().flying ? speed : speed, new Vec3(0, 0, 1));
					beltItem.onMovedTick(belt, player);

					if (player.tickCount % COST_INTERVAL == 0) {
						ManaItemHandler.instance().requestManaExact(belt, player, COST, true);
					}
				} else {
					beltItem.onNotMovingTick(belt, player);
				}
			} else {
				if (player.isShiftKeyDown()) {
					if (hasBoost) {
						attrib.removeModifier(STEP_BOOST_UUID);
					}
				} else {
					if (!hasBoost) {
						attrib.addTransientModifier(STEP_BOOST);
					}
				}
			}
		} else if (!player.level().isClientSide && hasBoost) {
			attrib.removeModifier(STEP_BOOST_UUID);
		}
	}

	public float getSpeed(ItemStack stack) {
		return speed;
	}

	public void onMovedTick(ItemStack stack, Player player) {}

	public void onNotMovingTick(ItemStack stack, Player player) {}

	public static void onPlayerJump(LivingEntity living) {
		if (living instanceof Player player) {
			ItemStack belt = EquipmentHandler.findOrEmpty(s -> s.getItem() instanceof SojournersSashItem, player);

			if (!belt.isEmpty() && ManaItemHandler.instance().requestManaExact(belt, player, COST, false)) {
				player.setDeltaMovement(player.getDeltaMovement().add(0, ((SojournersSashItem) belt.getItem()).jump, 0));
			}
		}
	}

	private static boolean tryConsumeMana(Player player) {
		ItemStack result = EquipmentHandler.findOrEmpty(s -> s.getItem() instanceof SojournersSashItem, player);
		return !result.isEmpty() && ManaItemHandler.instance().requestManaExact(result, player, COST, false);
	}

	ResourceLocation getRenderTexture() {
		return texture;
	}

	public static class Renderer implements AccessoryRenderer {
		private static HumanoidModel<LivingEntity> model = null;

		@Override
		public void doRender(HumanoidModel<?> bipedModel, ItemStack stack, LivingEntity living, PoseStack ms, MultiBufferSource buffers, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
			AccessoryRenderHelper.rotateIfSneaking(ms, living);

			float s = 1.15F;
			ms.scale(s, s, s);
			if (model == null) {
				model = new HumanoidModel<>(Minecraft.getInstance()
						.getEntityModels().bakeLayer(ModelLayers.PLAYER));
			}

			ResourceLocation texture = ((SojournersSashItem) stack.getItem()).getRenderTexture();
			VertexConsumer buffer = buffers.getBuffer(model.renderType(texture));
			model.body.render(ms, buffer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
		}
	}

}
