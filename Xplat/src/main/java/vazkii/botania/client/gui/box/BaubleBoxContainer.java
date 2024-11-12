/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.gui.box;

import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.client.gui.SlotLocked;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.BaubleBoxItem;
import vazkii.botania.common.item.BotaniaItems;

public class BaubleBoxContainer extends AbstractContainerMenu {
	private final ItemStack box;

	public BaubleBoxContainer(int windowId, Inventory playerInv, boolean isMainHand) {
		super(BotaniaItems.BAUBLE_BOX_CONTAINER, windowId);
		int i;
		int j;

		this.box = playerInv.player.getItemInHand(isMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
		Container baubleBoxInv;
		if (!playerInv.player.level().isClientSide) {
			baubleBoxInv = BaubleBoxItem.getInventory(box);
		} else {
			baubleBoxInv = new SimpleContainer(BaubleBoxItem.SIZE);
		}

		for (i = 0; i < 4; ++i) {
			for (j = 0; j < 6; ++j) {
				int k = j + i * 6;
				addSlot(new Slot(baubleBoxInv, k, 62 + j * 18, 8 + i * 18) {
					@Override
					public boolean mayPlace(@NotNull ItemStack stack) {
						return EquipmentHandler.instance.isAccessory(stack);
					}
				});
			}
		}

		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 9; ++j) {
				addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (i = 0; i < 9; ++i) {
			if (playerInv.getItem(i) == box) {
				addSlot(new SlotLocked(playerInv, i, 8 + i * 18, 142));
			} else {
				addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
			}
		}

	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		ItemStack main = player.getMainHandItem();
		ItemStack off = player.getOffhandItem();
		return !main.isEmpty() && main == box || !off.isEmpty() && off == box;
	}

	@NotNull
	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = slots.get(slotIndex);

		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();

			int boxStart = 0;
			int boxEnd = boxStart + 24;
			int invEnd = boxEnd + 36;

			if (slotIndex < boxEnd) {
				if (!moveItemStackTo(itemstack1, boxEnd, invEnd, true)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (!itemstack1.isEmpty() && EquipmentHandler.instance.isAccessory(itemstack1) && !moveItemStackTo(itemstack1, boxStart, boxEnd, false)) {
					return ItemStack.EMPTY;
				}
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
		}

		return itemstack;
	}

	@Override
	public void removed(@NotNull Player player) {
		if (!player.level().isClientSide) {
			ItemNBTHelper.setBoolean(box, BaubleBoxItem.TAG_OPEN, false);
		}
		super.removed(player);
	}
}
