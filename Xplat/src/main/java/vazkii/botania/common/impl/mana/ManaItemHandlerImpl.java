/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.impl.mana;

import com.google.common.collect.Iterables;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.item.ManaProficiencyArmor;
import vazkii.botania.api.mana.*;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.*;

public class ManaItemHandlerImpl implements ManaItemHandler {
	@Override
	public List<ItemStack> getManaItems(Player player) {
		if (player == null) {
			return Collections.emptyList();
		}

		List<ItemStack> toReturn = new ArrayList<>();

		for (ItemStack stackInSlot : Iterables.concat(player.getInventory().items, player.getInventory().offhand)) {
			if (!stackInSlot.isEmpty() && XplatAbstractions.INSTANCE.findManaItem(stackInSlot) != null) {
				toReturn.add(stackInSlot);
			}
		}

		XplatAbstractions.INSTANCE.fireManaItemEvent(player, toReturn);
		return toReturn;
	}

	@Override
	public List<ItemStack> getManaAccesories(Player player) {
		if (player == null) {
			return Collections.emptyList();
		}

		Container acc = BotaniaAPI.instance().getAccessoriesInventory(player);

		List<ItemStack> toReturn = new ArrayList<>(acc.getContainerSize());

		for (int slot = 0; slot < acc.getContainerSize(); slot++) {
			ItemStack stackInSlot = acc.getItem(slot);

			if (!stackInSlot.isEmpty() && XplatAbstractions.INSTANCE.findManaItem(stackInSlot) != null) {
				toReturn.add(stackInSlot);
			}
		}

		return toReturn;
	}

	@Override
	public int requestMana(ItemStack stack, Player player, int manaToGet, boolean remove) {
		if (stack.isEmpty()) {
			return 0;
		}

		List<ItemStack> items = getManaItems(player);
		List<ItemStack> acc = getManaAccesories(player);
		int manaReceived = 0;
		for (ItemStack stackInSlot : Iterables.concat(items, acc)) {
			if (stackInSlot == stack) {
				continue;
			}
			var manaItem = XplatAbstractions.INSTANCE.findManaItem(stackInSlot);
			if (manaItem.canExportManaToItem(stack) && manaItem.getMana() > 0) {
				var requestor = XplatAbstractions.INSTANCE.findManaItem(stack);
				if (requestor != null && !requestor.canReceiveManaFromItem(stackInSlot)) {
					continue;
				}

				int mana = Math.min(manaToGet - manaReceived, manaItem.getMana());

				if (remove) {
					manaItem.addMana(-mana);
				}

				manaReceived += mana;

				if (manaReceived >= manaToGet) {
					break;
				}
			}
		}

		return manaReceived;
	}

	@Override
	public boolean requestManaExact(ItemStack stack, Player player, int manaToGet, boolean remove) {
		if (stack.isEmpty()) {
			return false;
		}

		List<ItemStack> items = getManaItems(player);
		List<ItemStack> acc = getManaAccesories(player);
		int manaReceived = 0;
		Object2IntMap<ManaItem> manaToRemove = new Object2IntOpenHashMap<>();
		for (ItemStack stackInSlot : Iterables.concat(items, acc)) {
			if (stackInSlot == stack) {
				continue;
			}
			var manaItemSlot = XplatAbstractions.INSTANCE.findManaItem(stackInSlot);
			if (manaItemSlot.canExportManaToItem(stack)) {
				var manaItem = XplatAbstractions.INSTANCE.findManaItem(stack);
				if (manaItem != null && !manaItem.canReceiveManaFromItem(stackInSlot)) {
					continue;
				}

				int mana = Math.min(manaToGet - manaReceived, manaItemSlot.getMana());

				if (remove) {
					manaToRemove.put(manaItemSlot, mana);
				}

				manaReceived += mana;

				if (manaReceived >= manaToGet) {
					break;
				}
			}
		}

		if (manaReceived == manaToGet) {
			for (var e : manaToRemove.object2IntEntrySet()) {
				e.getKey().addMana(-e.getIntValue());
			}
			return true;
		}

		return false;
	}

	@Override
	public int dispatchMana(ItemStack stack, Player player, int manaToSend, boolean add) {
		if (stack.isEmpty()) {
			return 0;
		}

		List<ItemStack> items = getManaItems(player);
		List<ItemStack> acc = getManaAccesories(player);
		for (ItemStack stackInSlot : Iterables.concat(items, acc)) {
			if (stackInSlot == stack) {
				continue;
			}
			ManaItem manaItemSlot = XplatAbstractions.INSTANCE.findManaItem(stackInSlot);
			if (manaItemSlot.canReceiveManaFromItem(stack)) {
				var manaItem = XplatAbstractions.INSTANCE.findManaItem(stack);
				if (manaItem != null && !manaItem.canExportManaToItem(stackInSlot)) {
					continue;
				}

				int received;
				if (manaItemSlot.getMana() + manaToSend <= manaItemSlot.getMaxMana()) {
					received = manaToSend;
				} else {
					received = manaToSend - (manaItemSlot.getMana() + manaToSend - manaItemSlot.getMaxMana());
				}

				if (add) {
					manaItemSlot.addMana(manaToSend);
				}

				return received;
			}
		}

		return 0;
	}

	@Override
	public boolean dispatchManaExact(ItemStack stack, Player player, int manaToSend, boolean add) {
		if (stack.isEmpty()) {
			return false;
		}

		List<ItemStack> items = getManaItems(player);
		List<ItemStack> acc = getManaAccesories(player);
		for (ItemStack stackInSlot : Iterables.concat(items, acc)) {
			if (stackInSlot == stack) {
				continue;
			}
			ManaItem manaItemSlot = XplatAbstractions.INSTANCE.findManaItem(stackInSlot);
			if (manaItemSlot.getMana() + manaToSend <= manaItemSlot.getMaxMana() && manaItemSlot.canReceiveManaFromItem(stack)) {
				var manaItem = XplatAbstractions.INSTANCE.findManaItem(stack);
				if (manaItem != null && !manaItem.canExportManaToItem(stackInSlot)) {
					continue;
				}

				if (add) {
					manaItemSlot.addMana(manaToSend);
				}

				return true;
			}
		}

		return false;
	}

	private int discountManaForTool(ItemStack stack, Player player, int inCost) {
		float multiplier = Math.max(0F, 1F - getFullDiscountForTools(player, stack));
		return (int) (inCost * multiplier);
	}

	@Override
	public int requestManaForTool(ItemStack stack, Player player, int manaToGet, boolean remove) {
		int cost = discountManaForTool(stack, player, manaToGet);
		return requestMana(stack, player, cost, remove);
	}

	@Override
	public boolean requestManaExactForTool(ItemStack stack, Player player, int manaToGet, boolean remove) {
		int cost = discountManaForTool(stack, player, manaToGet);
		return requestManaExact(stack, player, cost, remove);
	}

	@Override
	public int getInvocationCountForTool(ItemStack stack, Player player, int manaToGet) {
		if (stack.isEmpty()) {
			return 0;
		}

		int cost = discountManaForTool(stack, player, manaToGet);
		int invocations = 0;

		List<ItemStack> items = getManaItems(player);
		List<ItemStack> acc = getManaAccesories(player);
		for (ItemStack stackInSlot : Iterables.concat(items, acc)) {
			if (stackInSlot == stack) {
				continue;
			}
			ManaItem manaItemSlot = XplatAbstractions.INSTANCE.findManaItem(stackInSlot);
			int availableMana = manaItemSlot.getMana();
			if (manaItemSlot.canExportManaToItem(stack) && availableMana > cost) {
				var manaItem = XplatAbstractions.INSTANCE.findManaItem(stack);
				if (manaItem != null && !manaItem.canReceiveManaFromItem(stackInSlot)) {
					continue;
				}

				invocations += availableMana / cost;
			}
		}

		return invocations;
	}

	@Override
	public float getFullDiscountForTools(Player player, ItemStack tool) {
		float discount = 0F;
		for (int i = 0; i < player.getInventory().armor.size(); i++) {
			ItemStack armor = player.getInventory().armor.get(i);
			if (!armor.isEmpty() && armor.getItem() instanceof ManaDiscountArmor discountArmor) {
				discount += discountArmor.getDiscount(armor, i, player, tool);
			}
		}

		int unbreaking = EnchantmentHelper.getItemEnchantmentLevel(player.level().holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING), tool);
		discount += unbreaking * 0.05F;
		discount = XplatAbstractions.INSTANCE.fireManaDiscountEvent(player, discount, tool);

		return discount;
	}

	@Override
	public boolean hasProficiency(Player player, ItemStack manaItem) {
		boolean proficient = false;

		for (EquipmentSlot e : EquipmentSlot.values()) {
			if (e.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
				continue;
			}
			ItemStack stack = player.getItemBySlot(e);
			if (!stack.isEmpty()) {
				Item item = stack.getItem();
				if (item instanceof ManaProficiencyArmor armor
						&& armor.shouldGiveProficiency(stack, e, player, manaItem)) {
					proficient = true;
					break;
				}
			}
		}

		return XplatAbstractions.INSTANCE.fireManaProficiencyEvent(player, manaItem, proficient);
	}
}
