/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.BotaniaDamageTypes;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.equipment.armor.terrasteel.TerrasteelHelmItem;

import java.util.List;
import java.util.Locale;

/**
 * An item that implements this can have Ancient Wills
 * crafted onto it.
 */
public interface AncientWillContainer {

	String TAG_ANCIENT_WILL = "AncientWill";
	enum AncientWillType {
		AHRIM,
		DHAROK,
		GUTHAN,
		TORAG,
		VERAC,
		KARIL
	}

	static void addAncientWill(ItemStack stack, AncientWillType will) {
		ItemNBTHelper.setBoolean(stack, TAG_ANCIENT_WILL + "_" + will.name().toLowerCase(Locale.ROOT), true);
	}

	static boolean hasAncientWill(ItemStack stack, AncientWillType will) {
		return ItemNBTHelper.getBoolean(stack, TAG_ANCIENT_WILL + "_" + will.name().toLowerCase(Locale.ROOT), false);
	}

	static float getCritDamageMult(Player player) {
		ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
		if (stack.getItem() instanceof AncientWillContainer awc && awc.hasFullArmorSet(player)) {
			if (!stack.isEmpty() && AncientWillContainer.hasAncientWill(stack, AncientWillType.DHAROK)) {
				return 1.0F + (1.0F - player.getHealth() / player.getMaxHealth()) * 0.5F;
			}
		}

		return 1.0F;
	}

	static boolean hasAnyWill(ItemStack stack) {
		for (AncientWillType type : AncientWillType.values()) {
			if (AncientWillContainer.hasAncientWill(stack, type)) {
				return true;
			}
		}

		return false;
	}

	static DamageSource onEntityAttacked(DamageSource source, float amount, Player player, LivingEntity entity) {
		ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
		if (stack.getItem() instanceof AncientWillContainer awc && awc.hasFullArmorSet(player)) {
			if (AncientWillContainer.hasAncientWill(stack, AncientWillType.AHRIM)) {
				entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 1));
			}

			if (AncientWillContainer.hasAncientWill(stack, AncientWillType.GUTHAN)) {
				player.heal(amount * 0.25F);
			}

			if (AncientWillContainer.hasAncientWill(stack, AncientWillType.TORAG)) {
				entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
			}

			if (AncientWillContainer.hasAncientWill(stack, AncientWillType.VERAC)) {
				source = BotaniaDamageTypes.Sources.playerAttackArmorPiercing(player.level().registryAccess(), player);
			}

			if (AncientWillContainer.hasAncientWill(stack, AncientWillType.KARIL)) {
				entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1));
			}
		}

		return source;
	}

	static void addAncientWillDescription(ItemStack stack, List<Component> list){
		for (AncientWillType type : AncientWillType.values()) {
			if (hasAncientWill(stack, type)) {
				list.add(Component.translatable("botania.armorset.will_" + type.name().toLowerCase(Locale.ROOT) + ".desc").withStyle(ChatFormatting.GRAY));
			}
		}
	}

	static void ancientWillInventoryTick(ItemStack stack, Level world, Entity entity){
		if (!world.isClientSide && entity instanceof Player player && player.getInventory().armor.contains(stack) && stack.getItem() instanceof AncientWillContainer awc && awc.hasFullArmorSet(player)) {
			int food = player.getFoodData().getFoodLevel();
			if (food > 0 && food < 18 && player.isHurt() && player.tickCount % 80 == 0) {
				player.heal(1F);
			}
			if (player.tickCount % 10 == 0) {
				ManaItemHandler.instance().dispatchManaExact(stack, player, 10, true);
			}
		}
	}

	boolean hasFullArmorSet(Player player);
}
