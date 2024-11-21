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

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import vazkii.botania.api.item.CosmeticBauble;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.render.AccessoryRenderRegistry;
import vazkii.botania.client.render.AccessoryRenderer;
import vazkii.botania.common.helper.VecHelper;
import vazkii.botania.common.proxy.Proxy;

import java.util.List;

public class CosmeticBaubleItem extends BaubleItem implements CosmeticBauble {

	public enum Variant {
		BLACK_BOWTIE, BLACK_TIE, RED_GLASSES(true), PUFFY_SCARF,
		ENGINEER_GOGGLES(true), EYEPATCH(true), WICKED_EYEPATCH(true), RED_RIBBONS(true),
		PINK_FLOWER_BUD(true), POLKA_DOTTED_BOWS(true), BLUE_BUTTERFLY(true), CAT_EARS(true),
		WITCH_PIN, DEVIL_TAIL, KAMUI_EYE, GOOGLY_EYES(true),
		FOUR_LEAF_CLOVER, CLOCK_EYE(true), UNICORN_HORN(true), DEVIL_HORNS(true),
		HYPER_PLUS(true), BOTANIST_EMBLEM, ANCIENT_MASK(true), EERIE_MASK(true),
		ALIEN_ANTENNA(true), ANAGLYPH_GLASSES(true), ORANGE_SHADES(true), GROUCHO_GLASSES(true),
		THICK_EYEBROWS(true), LUSITANIC_SHIELD, TINY_POTATO_MASK(true), QUESTGIVER_MARK(true),
		THINKING_HAND(true);

		private final boolean isHead;

		Variant(boolean isHead) {
			this.isHead = isHead;
		}

		Variant() {
			this(false);
		}
	}

	private final Variant variant;

	public CosmeticBaubleItem(Variant variant, Properties props) {
		super(props);
		this.variant = variant;
		Proxy.INSTANCE.runOnClient(() -> () -> AccessoryRenderRegistry.register(this, new Renderer()));
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
		if (variant == Variant.THINKING_HAND) {
			tooltip.add(Component.translatable("botaniamisc.cosmeticThinking").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
		} else {
			tooltip.add(Component.translatable("botaniamisc.cosmeticBauble").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
		}
		super.appendHoverText(stack, context, tooltip, flags);
	}

	public static class Renderer implements AccessoryRenderer {
		@Override
		public void doRender(HumanoidModel<?> bipedModel, ItemStack stack, LivingEntity living, PoseStack ms, MultiBufferSource buffers, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
			Variant variant = ((CosmeticBaubleItem) stack.getItem()).variant;
			if (variant.isHead) {
				bipedModel.head.translateAndRotate(ms);
				switch (variant) {
					case RED_GLASSES, ENGINEER_GOGGLES, ANAGLYPH_GLASSES -> {
						ms.translate(0, -0.225, -0.3);
						ms.scale(0.7F, -0.7F, -0.7F);
						renderItem(stack, ms, buffers, light);
					}
					case EYEPATCH -> {
						ms.translate(0.125, -0.225, -0.3);
						ms.mulPose(VecHelper.rotateY(180F));
						ms.scale(0.3F, -0.3F, -0.3F);
						renderItem(stack, ms, buffers, light);
					}
					case WICKED_EYEPATCH -> {
						ms.translate(-0.125, -0.225, -0.3);
						ms.scale(0.3F, -0.3F, -0.3F);
						renderItem(stack, ms, buffers, light);
					}
					case RED_RIBBONS -> {
						ms.translate(0, -0.65, 0.2);
						ms.mulPose(VecHelper.rotateY(180F));
						ms.scale(0.5F, -0.5F, -0.5F);
						renderItem(stack, ms, buffers, light);
					}
					case PINK_FLOWER_BUD -> {
						ms.translate(0.275, -0.6, 0);
						ms.mulPose(VecHelper.rotateY(-90F));
						ms.scale(0.5F, -0.5F, -0.5F);
						renderItem(stack, ms, buffers, light);
					}
					case POLKA_DOTTED_BOWS -> {
						ms.pushPose();
						ms.translate(0.275, -0.4, 0);
						ms.mulPose(VecHelper.rotateY(-90F));
						ms.scale(0.5F, -0.5F, -0.5F);
						renderItem(stack, ms, buffers, light);
						ms.popPose();
						ms.translate(-0.275, -0.4, 0);
						ms.mulPose(VecHelper.rotateY(90F));
						ms.scale(0.5F, -0.5F, -0.5F);
						renderItem(stack, ms, buffers, light);
					}
					case BLUE_BUTTERFLY -> {
						ms.pushPose();
						ms.translate(0.275, -0.4, 0);
						ms.mulPose(VecHelper.rotateY(45F));
						ms.scale(0.5F, -0.5F, -0.5F);
						renderItem(stack, ms, buffers, light);
						ms.popPose();
						ms.translate(0.275, -0.4, 0);
						ms.mulPose(VecHelper.rotateY(-45F));
						ms.scale(0.5F, -0.5F, -0.5F);
						renderItem(stack, ms, buffers, light);
					}
					case CAT_EARS -> {
						ms.translate(0F, -0.5F, -0.175F);
						ms.scale(0.5F, -0.5F, -0.5F);
						renderItem(stack, ms, buffers, light);
					}
					case GOOGLY_EYES -> {
						ms.translate(0, -0.225, -0.3);
						ms.scale(0.9F, -0.9F, -0.9F);
						renderItem(stack, ms, buffers, light);
					}
					case CLOCK_EYE -> {
						ms.translate(0.1, -0.225, -0.3F);
						ms.scale(0.4F, -0.4F, -0.4F);
						renderItem(stack, ms, buffers, light);
					}
					case UNICORN_HORN -> {
						ms.translate(0, -0.7, -0.3);
						ms.mulPose(VecHelper.rotateY(-90F));
						ms.scale(0.6F, -0.6F, -0.6F);
						renderItem(stack, ms, buffers, light);
					}
					case DEVIL_HORNS -> {
						ms.translate(0F, -0.4F, -0.175F);
						ms.scale(0.5F, -0.5F, -0.5F);
						renderItem(stack, ms, buffers, light);
					}
					case HYPER_PLUS -> {
						ms.translate(-0.15F, -0.45F, -0.3F);
						ms.scale(0.2F, -0.2F, -0.2F);
						renderItem(stack, ms, buffers, light);
						ms.translate(1.45F, 0F, 0F);
						renderItem(stack, ms, buffers, light);
					}
					case ANCIENT_MASK -> {
						ms.translate(0, -0.3, -0.3);
						ms.scale(0.7F, -0.7F, -0.7F);
						renderItem(stack, ms, buffers, light);
					}
					case EERIE_MASK -> {
						ms.translate(0, -0.25, -0.3);
						ms.scale(0.75F, -0.75F, -0.75F);
						renderItem(stack, ms, buffers, light);
					}
					case ALIEN_ANTENNA -> {
						ms.translate(0, -0.65, 0.2);
						ms.scale(0.5F, -0.5F, -0.5F);
						renderItem(stack, ms, buffers, light);
					}
					case ORANGE_SHADES -> {
						ms.translate(0, -0.3, -0.3);
						ms.scale(0.7F, -0.7F, -0.7F);
						int color = 0xFFFFFF | (178 << 24);
						RenderHelper.renderItemCustomColor(living, stack, color, ms, buffers, light, OverlayTexture.NO_OVERLAY);
					}
					case GROUCHO_GLASSES -> {
						ms.translate(0, -0.1, -0.3);
						ms.scale(0.75F, -0.75F, -0.75F);
						renderItem(stack, ms, buffers, light);
					}
					case THICK_EYEBROWS -> {
						ms.pushPose();
						ms.translate(-0.1, -0.3, -0.3);
						ms.scale(0.3F, -0.3F, -0.3F);
						renderItem(stack, ms, buffers, light);
						ms.popPose();
						ms.translate(0.1, -0.3, -0.3);
						ms.mulPose(VecHelper.rotateY(180F));
						ms.scale(0.3F, -0.3F, -0.3F);
						renderItem(stack, ms, buffers, light);
					}
					case TINY_POTATO_MASK -> {
						ms.translate(0, -0.3, -0.3);
						ms.scale(0.6F, -0.6F, -0.6F);
						renderItem(stack, ms, buffers, light);
					}
					case QUESTGIVER_MARK -> {
						ms.translate(0, -0.8, -0.2);
						ms.scale(0.5F, -0.5F, -0.5F);
						renderItem(stack, ms, buffers, light);
					}
					case THINKING_HAND -> {
						ms.translate(-0.1, 0, -0.3);
						ms.mulPose(VecHelper.rotateZ(-15F));
						ms.scale(0.5F, -0.5F, -0.5F);
						renderItem(stack, ms, buffers, light);
					}
					default -> {}
				}
			} else { // body cosmetics
				bipedModel.body.translateAndRotate(ms);
				switch (variant) {
					case BLACK_BOWTIE -> {
						ms.translate(0, 0.1, -0.13);
						ms.scale(0.6F, -0.6F, -0.6F);
						renderItem(stack, ms, buffers, light);
					}
					case BLACK_TIE, PUFFY_SCARF -> {
						ms.translate(0, 0.25, -0.15);
						ms.scale(0.5F, -0.5F, -0.5F);
						renderItem(stack, ms, buffers, light);
					}
					case WITCH_PIN -> {
						ms.translate(-0.1, 0.15, -0.15);
						ms.scale(0.2F, -0.2F, -0.2F);
						renderItem(stack, ms, buffers, light);
					}
					case DEVIL_TAIL -> {
						ms.translate(0, 0.55, 0.2);
						ms.mulPose(VecHelper.rotateY(-90F));
						ms.scale(0.6F, -0.6F, -0.6F);
						renderItem(stack, ms, buffers, light);
					}
					case KAMUI_EYE -> { // DON'T LOSE YOUR WAAAAAAAAY
						ms.pushPose();
						ms.translate(0.4, 0.1, -0.2);
						ms.scale(0.5F, -0.5F, -0.5F);
						renderItem(stack, ms, buffers, light);
						ms.popPose();
						ms.translate(-0.4, 0.1, -0.2);
						ms.mulPose(VecHelper.rotateY(180F));
						ms.scale(0.5F, -0.5F, -0.5F);
						RenderHelper.renderItemCustomColor(living, stack, 0xFF00004C, ms, buffers, light, OverlayTexture.NO_OVERLAY);
					}
					case FOUR_LEAF_CLOVER -> {
						ms.translate(0.1, 0.1, -0.13);
						ms.scale(0.3F, -0.3F, -0.3F);
						renderItem(stack, ms, buffers, light);
					}
					case BOTANIST_EMBLEM -> {
						ms.translate(0F, 0.375, -0.13);
						ms.scale(0.3F, -0.3F, -0.3F);
						renderItem(stack, ms, buffers, light);
					}
					case LUSITANIC_SHIELD -> {
						ms.translate(0F, 0.35, 0.13);
						ms.mulPose(VecHelper.rotateZ(8F));
						ms.mulPose(VecHelper.rotateY(180F));
						ms.scale(0.6F, -0.6F, -0.6F);
						renderItem(stack, ms, buffers, light);
					}
					default -> {}
				}
			}
		}

		private static void renderItem(ItemStack stack, PoseStack ms, MultiBufferSource buffers, int light) {
			Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE,
					light, OverlayTexture.NO_OVERLAY, ms, buffers, Minecraft.getInstance().level, 0);
		}
	}

}
