/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.forge.integration.curios;

import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import org.jetbrains.annotations.NotNull;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.event.DropRulesEvent;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import vazkii.botania.client.render.AccessoryRenderRegistry;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.item.ResoluteIvyItem;
import vazkii.botania.common.item.equipment.bauble.BaubleItem;
import vazkii.botania.common.proxy.Proxy;
import vazkii.botania.forge.CapabilityUtil;

import java.util.UUID;
import java.util.function.Predicate;

public class CurioIntegration extends EquipmentHandler {
	public static void init() {
		NeoForge.EVENT_BUS.addListener(CurioIntegration::keepCurioDrops);
	}

	public static void keepCurioDrops(DropRulesEvent event) { //TODO make this less hacky
		event.addOverride(stack -> {
			if (ResoluteIvyItem.hasIvy(stack)) {
				stack.removeTagKey(ResoluteIvyItem.TAG_KEEP);
				return true;
			}
			return false;
		}, DropRule.ALWAYS_KEEP);
	}

	@Override
	protected Container getAllWornItems(LivingEntity living) {
		return CuriosApi.getCuriosInventory(living)
				.map(ICuriosItemHandler::getEquippedCurios)
				.<Container>map(RecipeWrapper::new)
				.orElseGet(() -> new SimpleContainer(0));
	}

	@Override
	protected ItemStack findItem(Item item, LivingEntity living) {
		return CuriosApi.getCuriosInventory(living)
				.map(i -> i.findFirstCurio(item).map(SlotResult::stack).orElse(ItemStack.EMPTY))
				.orElse(ItemStack.EMPTY);
	}

	@Override
	protected ItemStack findItem(Predicate<ItemStack> pred, LivingEntity living) {
		return CuriosApi.getCuriosInventory(living)
				.map(i -> i.findFirstCurio(pred).map(SlotResult::stack).orElse(ItemStack.EMPTY))
				.orElse(ItemStack.EMPTY);
	}

	public ICapabilityProvider initCapability(ItemStack stack) {
		return CapabilityUtil.makeProvider(CuriosCapability.ITEM, new Wrapper(stack));
	}

	@Override
	public void onInit(Item item) {
		Proxy.INSTANCE.runOnClient(() -> () -> CuriosRendererRegistry.register(item, () -> Renderer.INSTANCE));
	}

	@Override
	public boolean isAccessory(ItemStack stack) {
		return super.isAccessory(stack) || stack.getCapability(CuriosCapability.ITEM).isPresent();
	}

	public static class Wrapper implements ICurio {
		private static final SoundInfo SOUND_INFO = new SoundInfo(BotaniaSounds.equipBauble, 0.1F, 1.3F);

		private final ItemStack stack;

		Wrapper(ItemStack stack) {
			this.stack = stack;
		}

		private BaubleItem getItem() {
			return (BaubleItem) stack.getItem();
		}

		@Override
		public ItemStack getStack() {
			return stack;
		}

		@Override
		public void curioTick(SlotContext slotContext) {
			getItem().onWornTick(stack, slotContext.entity());
		}

		@Override
		public void onEquip(SlotContext slotContext, ItemStack previousStack) {
			getItem().onEquipped(stack, slotContext.entity());
		}

		@Override
		public void onUnequip(SlotContext slotContext, ItemStack newStack) {
			getItem().onUnequipped(stack, slotContext.entity());
		}

		@Override
		public boolean canEquip(SlotContext slotContext) {
			return getItem().canEquip(stack, slotContext.entity());
		}

		@Override
		public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid) {
			return getItem().getEquippedAttributeModifiers(stack);
		}

		@Override
		public boolean canSync(SlotContext slotContext) {
			return true;
		}

		@NotNull
		@Override
		public SoundInfo getEquipSound(SlotContext slotContext) {
			return SOUND_INFO;
		}

		@Override
		public boolean canEquipFromUse(SlotContext slotContext) {
			return true;
		}
	}

	private static class Renderer implements ICurioRenderer {
		private static final Renderer INSTANCE = new Renderer();

		@Override
		public <T extends LivingEntity, M extends EntityModel<T>>
				void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack,
						RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer,
						int light, float limbSwing, float limbSwingAmount, float partialTicks,
						float ageInTicks, float netHeadYaw, float headPitch) {
			LivingEntity livingEntity = slotContext.entity();
			M contextModel = renderLayerParent.getModel();
			BaubleItem item = (BaubleItem) stack.getItem();

			if (!item.hasRender(stack, livingEntity)) {
				return;
			}

			if (!(contextModel instanceof HumanoidModel<?>)) {
				return;
			}

			matrixStack.pushPose();
			ItemStack cosmetic = item.getCosmeticItem(stack);
			if (!cosmetic.isEmpty()) {
				CuriosRendererRegistry.getRenderer(item)
						.ifPresent(curio -> curio.render(cosmetic, slotContext, matrixStack, renderLayerParent,
								renderTypeBuffer, light, limbSwing, limbSwingAmount,
								partialTicks, ageInTicks, netHeadYaw, headPitch));
			} else {
				var renderer = AccessoryRenderRegistry.get(stack);
				if (renderer != null) {
					renderer.doRender((HumanoidModel<?>) contextModel, stack, livingEntity, matrixStack,
							renderTypeBuffer, light, limbSwing, limbSwingAmount,
							partialTicks, ageInTicks, netHeadYaw, headPitch);
				}
			}
			matrixStack.popPose();
		}
	}

}
