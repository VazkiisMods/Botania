/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.bauble;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import vazkii.botania.api.item.CosmeticAttachable;
import vazkii.botania.api.item.PhantomInkable;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.xplat.BotaniaConfig;

import java.util.List;
import java.util.UUID;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public abstract class BaubleItem extends Item implements CosmeticAttachable, PhantomInkable {

	private static final String TAG_BAUBLE_UUID = "baubleUUID";
	private static final String TAG_COSMETIC_ITEM = "cosmeticItem";
	private static final String TAG_PHANTOM_INK = "phantomInk";

	public BaubleItem(Properties props) {
		super(props);
		EquipmentHandler.instance.onInit(this);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
		ItemStack cosmetic = getCosmeticItem(stack);
		if (!cosmetic.isEmpty()) {
			tooltip.add(Component.translatable("botaniamisc.hasCosmetic", cosmetic.getHoverName()).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
		}

		if (hasPhantomInk(stack)) {
			tooltip.add(Component.translatable("botaniamisc.hasPhantomInk").withStyle(ChatFormatting.AQUA));
		}
	}

	@Override
	public ItemStack getCosmeticItem(ItemStack stack) {
		CompoundTag cmp = ItemNBTHelper.getCompound(stack, TAG_COSMETIC_ITEM, true);
		if (cmp == null) {
			return ItemStack.EMPTY;
		}
		return ItemStack.of(cmp);
	}

	@Override
	public void setCosmeticItem(ItemStack stack, ItemStack cosmetic) {
		CompoundTag cmp = new CompoundTag();
		if (!cosmetic.isEmpty()) {
			cmp = cosmetic.save(cmp);
		}
		ItemNBTHelper.setCompound(stack, TAG_COSMETIC_ITEM, cmp);
	}

	public static UUID getBaubleUUID(ItemStack stack) {
		var tag = stack.getOrCreateTag();

		// Legacy handling
		String tagBaubleUuidMostLegacy = "baubleUUIDMost";
		String tagBaubleUuidLeastLegacy = "baubleUUIDLeast";
		if (tag.contains(tagBaubleUuidMostLegacy) && tag.contains(tagBaubleUuidLeastLegacy)) {
			UUID uuid = new UUID(tag.getLong(tagBaubleUuidMostLegacy), tag.getLong(tagBaubleUuidLeastLegacy));
			tag.putUUID(TAG_BAUBLE_UUID, uuid);
		}

		if (!tag.hasUUID(TAG_BAUBLE_UUID)) {
			UUID uuid = UUID.randomUUID();
			tag.putUUID(TAG_BAUBLE_UUID, uuid);
		}

		return tag.getUUID(TAG_BAUBLE_UUID);
	}

	@Override
	public boolean hasPhantomInk(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_PHANTOM_INK, false);
	}

	@Override
	public void setPhantomInk(ItemStack stack, boolean ink) {
		ItemNBTHelper.setBoolean(stack, TAG_PHANTOM_INK, ink);
	}

	public void onWornTick(ItemStack stack, LivingEntity entity) {}

	public void onEquipped(ItemStack stack, LivingEntity entity) {
		if (!entity.level().isClientSide && entity instanceof ServerPlayer player) {
			PlayerHelper.grantCriterion(player, botaniaRL("main/bauble_wear"), "code_triggered");
		}
	}

	public void onUnequipped(ItemStack stack, LivingEntity entity) {}

	public boolean canEquip(ItemStack stack, LivingEntity entity) {
		return true;
	}

	public Multimap<Holder<Attribute>, AttributeModifier> getEquippedAttributeModifiers(ItemStack stack) {
		return HashMultimap.create();
	}

	public boolean hasRender(ItemStack stack, LivingEntity living) {
		return !hasPhantomInk(stack)
				&& BotaniaConfig.client().renderAccessories()
				&& !living.isInvisible();
	}
}
