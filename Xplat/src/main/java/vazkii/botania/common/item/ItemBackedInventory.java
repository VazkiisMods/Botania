/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.common.helper.ItemNBTHelper;

/**
 * An inventory that writes into the provided stack's NBT on save
 */
public class ItemBackedInventory extends SimpleContainer {
	private static final String TAG_ITEMS = "Items";
	private final ItemStack stack;

	public ItemBackedInventory(ItemStack stack, int expectedSize) {
		super(expectedSize);
		this.stack = stack;

		ListTag lst = ItemNBTHelper.getList(stack, TAG_ITEMS, Tag.TAG_COMPOUND, false);
		int i = 0;
		for (; i < expectedSize && i < lst.size(); i++) {
			setItem(i, /*ItemStack.of(lst.getCompound(i))*/ ItemStack.EMPTY);
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return !stack.isEmpty();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		ListTag list = new ListTag();
		for (int i = 0; i < getContainerSize(); i++) {
			//todo list.add(getItem(i).save(new CompoundTag()));
		}
		ItemNBTHelper.setList(stack, TAG_ITEMS, list);
	}
}
