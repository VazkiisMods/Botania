/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.integration.trinkets;

import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import dev.emi.trinkets.api.event.TrinketDropCallback;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import vazkii.botania.client.render.AccessoryRenderRegistry;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.item.ResoluteIvyItem;
import vazkii.botania.common.item.equipment.bauble.BaubleItem;
import vazkii.botania.common.proxy.Proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class TrinketsIntegration extends EquipmentHandler {
	public static void init() {
		TrinketDropCallback.EVENT.register(TrinketsIntegration::keepAccessoryDrops);
		UseItemCallback.EVENT.register(TrinketsIntegration::useBaubleItem);
	}

	private static TrinketEnums.DropRule keepAccessoryDrops(TrinketEnums.DropRule oldRule,
			ItemStack stack, SlotReference ref, LivingEntity livingEntity) {
		//TODO make this less hacky
		if (ResoluteIvyItem.hasIvy(stack)) {
			stack.removeTagKey(ResoluteIvyItem.TAG_KEEP);
			return TrinketEnums.DropRule.KEEP;
		}
		return oldRule;
	}

	private static InteractionResultHolder<ItemStack> useBaubleItem(Player player, Level level, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.getItem() instanceof BaubleItem bi && bi.canEquip(stack, player) && TrinketItem.equipItem(player, stack)) {
			return InteractionResultHolder.success(stack);
		}
		return InteractionResultHolder.pass(stack);
	}

	@Override
	protected Container getAllWornItems(LivingEntity living) {
		return TrinketsApi.getTrinketComponent(living).map(h -> {
			List<ItemStack> list = new ArrayList<>();
			for (var tuple : h.getAllEquipped()) {
				ItemStack stack = tuple.getB();
				list.add(stack);
			}

			return new SimpleContainer(list.toArray(new ItemStack[0]));
		}).orElseGet(() -> new SimpleContainer(0));
	}

	@Override
	protected ItemStack findItem(Item item, LivingEntity living) {
		return TrinketsApi.getTrinketComponent(living).flatMap(h -> {
			var results = h.getEquipped(item);
			if (results.isEmpty()) {
				return Optional.empty();
			} else {
				return Optional.of(results.get(0).getB());
			}
		}).orElse(ItemStack.EMPTY);
	}

	@Override
	protected ItemStack findItem(Predicate<ItemStack> pred, LivingEntity living) {
		return TrinketsApi.getTrinketComponent(living).flatMap(h -> {
			var results = h.getEquipped(pred);
			if (results.isEmpty()) {
				return Optional.empty();
			} else {
				return Optional.of(results.get(0).getB());
			}
		}).orElse(ItemStack.EMPTY);
	}

	@Override
	public void onInit(Item item) {
		TrinketsApi.registerTrinket(item, WRAPPER);
		Proxy.INSTANCE.runOnClient(() -> () -> TrinketRendererRegistry.registerRenderer(item, new RenderWrapper()));
	}

	@Override
	public boolean isAccessory(ItemStack stack) {
		return super.isAccessory(stack)
				|| TrinketsApi.getTrinket(stack.getItem()) != TrinketsApi.getDefaultTrinket();
	}

	public static final Trinket WRAPPER = new Trinket() {
		private BaubleItem getItem(ItemStack stack) {
			return (BaubleItem) stack.getItem();
		}

		@Override
		public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
			if (!stack.isEmpty()) {
				getItem(stack).onWornTick(stack, entity);
			}
		}

		@Override
		public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
			if (!stack.isEmpty()) {
				getItem(stack).onEquipped(stack, entity);
			}
		}

		@Override
		public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
			if (!stack.isEmpty()) {
				getItem(stack).onUnequipped(stack, entity);
			}
		}

		@Override
		public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
			if (!stack.isEmpty()) {
				return getItem(stack).canEquip(stack, entity);
			}
			return false;
		}

		@Override
		public Multimap<Attribute, AttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
			var ret = Trinket.super.getModifiers(stack, slot, entity, uuid);
			if (!stack.isEmpty()) {
				ret.putAll(getItem(stack).getEquippedAttributeModifiers(stack));
			}
			return ret;
		}
	};

	private static class RenderWrapper implements TrinketRenderer {
		@Override
		public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel,
				PoseStack matrices, MultiBufferSource vertexConsumers, int light, LivingEntity livingEntity,
				float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
			BaubleItem item = (BaubleItem) stack.getItem();
			if (!item.hasRender(stack, livingEntity)) {
				return;
			}

			if (!(contextModel instanceof HumanoidModel<?>)) {
				return;
			}

			ItemStack cosmetic = item.getCosmeticItem(stack);
			if (!cosmetic.isEmpty()) {
				TrinketRendererRegistry.getRenderer(cosmetic.getItem())
						.ifPresent(sub -> sub.render(cosmetic, slotReference, contextModel, matrices, vertexConsumers, light, livingEntity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch));
			} else {
				var renderer = AccessoryRenderRegistry.get(stack);
				if (renderer != null) {
					renderer.doRender((HumanoidModel<?>) contextModel, stack, livingEntity, matrices, vertexConsumers, light, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
				}
			}
		}
	}
}
