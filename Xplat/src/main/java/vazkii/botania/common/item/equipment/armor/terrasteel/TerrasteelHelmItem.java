/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.armor.terrasteel;

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

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import vazkii.botania.api.item.AncientWillContainer;
import vazkii.botania.api.mana.ManaDiscountArmor;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.BotaniaDamageTypes;
import vazkii.botania.common.component.BotaniaDataComponents;
import vazkii.botania.common.item.BotaniaItems;

import java.util.*;

public class TerrasteelHelmItem extends TerrasteelArmorItem implements ManaDiscountArmor, AncientWillContainer {

	public TerrasteelHelmItem(Properties props) {
		super(Type.HELMET, props);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		if (!world.isClientSide && entity instanceof Player player
				&& player.getInventory().armor.contains(stack)
				&& hasArmorSet(player)) {
			int food = player.getFoodData().getFoodLevel();
			if (food > 0 && food < 18 && player.isHurt() && player.tickCount % 80 == 0) {
				player.heal(1F);
			}
			if (player.tickCount % 10 == 0) {
				ManaItemHandler.instance().dispatchManaExact(stack, player, 10, true);
			}
		}
	}

	@Override
	public float getDiscount(ItemStack stack, int slot, Player player, @Nullable ItemStack tool) {
		return hasArmorSet(player) ? 0.2F : 0F;
	}

	@Override
	public void addAncientWill(ItemStack stack, AncientWillType will) {
		Set<String> setOfWills = new TreeSet<>(getAncientWillsList(stack));
		setOfWills.add(getWillId(will));
		stack.set(BotaniaDataComponents.ANCIENT_WILLS, new ArrayList<>(setOfWills));
	}

	private static String getWillId(AncientWillType will) {
		return will.name().toLowerCase(Locale.ROOT);
	}

	@Override
	public boolean hasAncientWill(ItemStack stack, AncientWillType will) {
		return hasAncientWill_(stack, will);
	}

	private static boolean hasAncientWill_(ItemStack stack, AncientWillType will) {
		return getAncientWillsList(stack).contains(getWillId(will));
	}

	@Unmodifiable
	private static List<String> getAncientWillsList(ItemStack stack) {
		return stack.getOrDefault(BotaniaDataComponents.ANCIENT_WILLS, Collections.emptyList());
	}

	@Override
	public void addArmorSetDescription(ItemStack stack, List<Component> list) {
		super.addArmorSetDescription(stack, list);
		for (AncientWillType type : AncientWillType.values()) {
			if (hasAncientWill(stack, type)) {
				list.add(Component.translatable("botania.armorset.will_" + getWillId(type) + ".desc").withStyle(ChatFormatting.GRAY));
			}
		}
	}

	public static boolean hasAnyWill(ItemStack stack) {
		return !getAncientWillsList(stack).isEmpty();
	}

	public static boolean hasTerraArmorSet(Player player) {
		return ((TerrasteelHelmItem) BotaniaItems.terrasteelHelm).hasArmorSet(player);
	}

	public static float getCritDamageMult(Player player) {
		if (hasTerraArmorSet(player)) {
			ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
			if (!stack.isEmpty() && stack.getItem() instanceof TerrasteelHelmItem
					&& hasAncientWill_(stack, AncientWillType.DHAROK)) {
				return 1F + (1F - player.getHealth() / player.getMaxHealth()) * 0.5F;
			}
		}
		return 1.0F;
	}

	public static DamageSource onEntityAttacked(DamageSource source, float amount, Player player, LivingEntity entity) {
		if (hasTerraArmorSet(player)) {
			ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
			if (!stack.isEmpty() && stack.getItem() instanceof TerrasteelHelmItem) {
				// TODO: might be worth refactoring this to only get the set of wills once
				if (hasAncientWill_(stack, AncientWillType.AHRIM)) {
					entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 1));
				}
				if (hasAncientWill_(stack, AncientWillType.GUTHAN)) {
					player.heal(amount * 0.25F);
				}
				if (hasAncientWill_(stack, AncientWillType.TORAG)) {
					entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
				}
				if (hasAncientWill_(stack, AncientWillType.VERAC)) {
					source = BotaniaDamageTypes.Sources.playerAttackArmorPiercing(player.level().registryAccess(), player);
				}
				if (hasAncientWill_(stack, AncientWillType.KARIL)) {
					entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1));
				}
			}
		}
		return source;
	}

}
