/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 */

package vazkii.botania.common.impl.mana;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import vazkii.botania.api.mana.ManaItem;
import vazkii.botania.common.component.BotaniaDataComponents;
import vazkii.botania.common.helper.ItemNBTHelper;

/**
 * Default implementation for the {@link ManaItem} interface. This implementation covers all of Botania's own mana
 * items, from tablets, to mana mirror and terra shatterer.
 *
 * @param stack The mana item's stack.
 */
public record DefaultManaItemImpl(ItemStack stack) implements ManaItem {

	@Override
	public int getMana() {
		if (stack.has(BotaniaDataComponents.CREATIVE_MANA)) {
			return getMaxMana();
		}
		return stack.getOrDefault(BotaniaDataComponents.MANA, 0);
	}

	@Override
	public int getMaxMana() {
		return stack.getOrDefault(BotaniaDataComponents.MAX_MANA, 0);
	}

	@Override
	public void addMana(int mana) {
		if (stack.has(BotaniaDataComponents.CREATIVE_MANA)) {
			return;
		}

		ItemNBTHelper.setIntNonZero(stack, BotaniaDataComponents.MANA, Math.min(getMana() + mana, getMaxMana()));
		Integer manaBacklog = stack.get(BotaniaDataComponents.MANA_BACKLOG);
		if (manaBacklog != null) {
			stack.set(BotaniaDataComponents.MANA_BACKLOG, manaBacklog + mana);
		}
	}

	@Override
	public boolean canReceiveManaFromPool(BlockEntity pool) {
		return stack.has(BotaniaDataComponents.CAN_RECEIVE_MANA_FROM_POOL);
	}

	@Override
	public boolean canReceiveManaFromItem(ItemStack otherStack) {
		return stack.has(BotaniaDataComponents.CAN_RECEIVE_MANA_FROM_ITEM);
	}

	@Override
	public boolean canExportManaToPool(BlockEntity pool) {
		return stack.has(BotaniaDataComponents.CAN_EXPORT_MANA_TO_POOL);
	}

	@Override
	public boolean canExportManaToItem(ItemStack otherStack) {
		return stack.has(BotaniaDataComponents.CAN_EXPORT_MANA_TO_ITEM);
	}

	@Override
	public boolean isNoExport() {
		return stack.has(BotaniaDataComponents.CAN_EXPORT_MANA_TO_ITEM);
	}
}
