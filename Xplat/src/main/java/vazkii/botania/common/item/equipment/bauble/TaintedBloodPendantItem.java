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

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.brew.Brew;
import vazkii.botania.api.brew.BrewContainer;
import vazkii.botania.api.brew.BrewItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.core.handler.MiscellaneousModels;
import vazkii.botania.client.render.AccessoryRenderRegistry;
import vazkii.botania.client.render.AccessoryRenderer;
import vazkii.botania.common.brew.BotaniaBrews;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.CustomCreativeTabContents;
import vazkii.botania.common.proxy.Proxy;
import vazkii.botania.mixin.client.MinecraftAccessor;

import java.util.List;

public class TaintedBloodPendantItem extends BaubleItem implements BrewContainer, BrewItem, CustomCreativeTabContents {

	private static final String TAG_BREW_KEY = "brewKey";

	public TaintedBloodPendantItem(Properties props) {
		super(props);
		Proxy.INSTANCE.runOnClient(() -> () -> AccessoryRenderRegistry.register(this, new Renderer()));
	}

	@Override
	public void addToCreativeTab(Item me, CreativeModeTab.Output output) {
		output.accept(this);
		for (Brew brew : BotaniaAPI.instance().getBrewRegistry()) {
			ItemStack brewStack = getItemForBrew(brew, new ItemStack(this));
			if (!brewStack.isEmpty()) {
				output.accept(brewStack);
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag adv) {
		super.appendHoverText(stack, context, tooltip, adv);

		Brew brew = getBrew(stack);
		if (brew == BotaniaBrews.fallbackBrew) {
			tooltip.add(Component.translatable("botaniamisc.notInfused").withStyle(ChatFormatting.LIGHT_PURPLE));
			return;
		}

		tooltip.add(Component.translatable("botaniamisc.brewOf", I18n.get(brew.getTranslationKey(stack))).withStyle(ChatFormatting.LIGHT_PURPLE));
		for (MobEffectInstance effect : brew.getPotionEffects(stack)) {
			ChatFormatting format = effect.getEffect().value().getCategory().getTooltipFormatting();
			MutableComponent cmp = Component.translatable(effect.getDescriptionId());
			if (effect.getAmplifier() > 0) {
				cmp.append(" ");
				cmp.append(Component.translatable("botania.roman" + (effect.getAmplifier() + 1)));
			}
			tooltip.add(cmp.withStyle(format));
		}
	}

	@Override
	public void onWornTick(ItemStack stack, LivingEntity living) {
		Brew brew = ((BrewItem) stack.getItem()).getBrew(stack);
		if (brew != BotaniaBrews.fallbackBrew && living instanceof Player player && !living.level().isClientSide) {
			MobEffectInstance effect = brew.getPotionEffects(stack).get(0);
			float cost = (float) brew.getManaCost(stack) / effect.getDuration() / (1 + effect.getAmplifier()) * 2.5F;
			boolean doRand = cost < 1;
			if (ManaItemHandler.instance().requestManaExact(stack, player, (int) Math.ceil(cost), false)) {
				MobEffectInstance currentEffect = living.getEffect(effect.getEffect());
				boolean nightVision = effect.getEffect() == MobEffects.NIGHT_VISION;
				if (currentEffect == null || currentEffect.getDuration() < (nightVision ? 305 : 3)) {
					MobEffectInstance applyEffect = new MobEffectInstance(effect.getEffect(), nightVision ? 385 : 80, effect.getAmplifier(), true, true);
					living.addEffect(applyEffect);
				}

				if (!doRand || Math.random() < cost) {
					ManaItemHandler.instance().requestManaExact(stack, player, (int) Math.ceil(cost), true);
				}
			}
		}
	}

	public static class Renderer implements AccessoryRenderer {
		@Override
		public void doRender(HumanoidModel<?> bipedModel, ItemStack stack, LivingEntity living, PoseStack ms, MultiBufferSource buffers, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
			boolean armor = !living.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
			bipedModel.body.translateAndRotate(ms);
			ms.translate(-0.25, 0.4, armor ? 0.05 : 0.12);
			ms.scale(0.5F, -0.5F, -0.5F);

			BakedModel model = MiscellaneousModels.INSTANCE.bloodPendantChain;
			VertexConsumer buffer = buffers.getBuffer(Sheets.cutoutBlockSheet());
			Minecraft.getInstance().getBlockRenderer().getModelRenderer()
					.renderModel(ms.last(), buffer, null, model, 1, 1, 1, light, OverlayTexture.NO_OVERLAY);

			model = MiscellaneousModels.INSTANCE.bloodPendantGem;
			int color = ((MinecraftAccessor) Minecraft.getInstance()).getItemColors().getColor(stack, 1);
			float r = (color >> 16 & 0xFF) / 255F;
			float g = (color >> 8 & 0xFF) / 255F;
			float b = (color & 0xFF) / 255F;
			Minecraft.getInstance().getBlockRenderer().getModelRenderer()
					.renderModel(ms.last(), buffer, null, model, r, g, b, 0xF000F0, OverlayTexture.NO_OVERLAY);
		}
	}

	@Override
	public Brew getBrew(ItemStack stack) {
		String key = ItemNBTHelper.getString(stack, TAG_BREW_KEY, "");
		return BotaniaAPI.instance().getBrewRegistry().get(ResourceLocation.tryParse(key));
	}

	public static void setBrew(ItemStack stack, Brew brew) {
		setBrew(stack, BotaniaAPI.instance().getBrewRegistry().getKey(brew));
	}

	public static void setBrew(ItemStack stack, ResourceLocation brew) {
		ItemNBTHelper.setString(stack, TAG_BREW_KEY, brew.toString());
	}

	@Override
	public ItemStack getItemForBrew(Brew brew, ItemStack stack) {
		if (!brew.canInfuseBloodPendant() || brew.getPotionEffects(stack).size() != 1 || brew.getPotionEffects(stack).get(0).getEffect().value().isInstantenous()) {
			return ItemStack.EMPTY;
		}

		ItemStack brewStack = new ItemStack(this);
		setBrew(brewStack, brew);
		return brewStack;
	}

	@Override
	public int getManaCost(Brew brew, ItemStack stack) {
		return brew.getManaCost() * 10;
	}
}
