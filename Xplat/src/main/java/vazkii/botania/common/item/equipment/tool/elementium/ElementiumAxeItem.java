/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.tool.elementium;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.annotations.SoftImplement;
import vazkii.botania.common.item.equipment.tool.manasteel.ManasteelAxeItem;

import java.util.function.Consumer;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class ElementiumAxeItem extends ManasteelAxeItem {
	private static final ResourceLocation BEHEADING_LOOT_TABLE = prefix("elementium_axe_beheading");

	public ElementiumAxeItem(Properties props) {
		super(BotaniaAPI.instance().getElementiumItemTier(), 6F, -3.1F, props);
	}

	public static void onEntityDrops(boolean hitRecently, DamageSource source, LivingEntity target,
			Consumer<ItemStack> consumer) {
		LootParams.Builder ctx = (new LootParams.Builder((ServerLevel) target.level()))
				.withParameter(LootContextParams.THIS_ENTITY, target)
				.withParameter(LootContextParams.ORIGIN, target.position())
				.withParameter(LootContextParams.DAMAGE_SOURCE, source)
				.withOptionalParameter(LootContextParams.KILLER_ENTITY, source.getEntity())
				.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, source.getDirectEntity());

		if (hitRecently && target.getKillCredit() != null && target.getKillCredit() instanceof Player p) {
			ctx = ctx.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, p).withLuck(p.getLuck());
		}

		target.level().getServer().getLootData().getLootTable(BEHEADING_LOOT_TABLE)
				.getRandomItems(ctx.create(LootContextParamSets.ENTITY), target.getLootTableSeed(), consumer);
	}

	@SoftImplement("IItemExtension")
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if (enchantment == Enchantments.MOB_LOOTING) {
			return true;
		} else {
			// Copy the default impl
			return enchantment.category.canEnchant(this);
		}

	}

	// [VanillaCopy] modified from DiggerItem::hurtEnemy, actually same as SwordItem::hurtEnemy
	@Override
	public boolean hurtEnemy(ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
		// only do 1 durability damage, since this is primarily a weapon
		stack.hurtAndBreak(1, attacker, living -> living.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		return true;
	}

}
