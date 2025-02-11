/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.tool.elementium;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.annotations.SoftImplement;
import vazkii.botania.common.item.equipment.tool.manasteel.ManasteelAxeItem;
import vazkii.botania.common.loot.BotaniaLootTables;

import java.util.function.Consumer;

public class ElementiumAxeItem extends ManasteelAxeItem {

	public ElementiumAxeItem(Properties props) {
		super(BotaniaAPI.instance().getElementiumItemTier(), props.attributes(ElementiumAxeItem.createAttributes(BotaniaAPI.instance().getElementiumItemTier(), 6F, -3.1F)));
	}

	public static void onEntityDrops(boolean hitRecently, DamageSource source, LivingEntity target,
			Consumer<ItemStack> consumer) {
		LootParams.Builder ctx = (new LootParams.Builder((ServerLevel) target.level()))
				.withParameter(LootContextParams.THIS_ENTITY, target)
				.withParameter(LootContextParams.ORIGIN, target.position())
				.withParameter(LootContextParams.DAMAGE_SOURCE, source)
				.withOptionalParameter(LootContextParams.ATTACKING_ENTITY, source.getEntity())
				.withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, source.getDirectEntity());

		if (hitRecently && target.getKillCredit() != null && target.getKillCredit() instanceof Player p) {
			ctx = ctx.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, p).withLuck(p.getLuck());
		}

		target.level().getServer().reloadableRegistries().getLootTable(BotaniaLootTables.BEHEADING_LOOT_TABLE)
				.getRandomItems(ctx.create(LootContextParamSets.ENTITY), target.getLootTableSeed(), consumer);
	}

	@SoftImplement("IItemExtension")
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		if (enchantment == Enchantments.LOOTING) {
			return true;
		} else {
			// Copy the default impl
			return stack.is(Items.ENCHANTED_BOOK) || enchantment.value().isSupportedItem(stack);
		}

	}

	// [VanillaCopy] modified from DiggerItem::hurtEnemy, actually same as SwordItem::hurtEnemy
	@Override
	public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		// only do 1 durability damage, since this is primarily a weapon
		stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
	}

}
