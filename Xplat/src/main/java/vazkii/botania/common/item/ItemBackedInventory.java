/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import vazkii.botania.common.helper.DataComponentHelper;

/**
 * An inventory that writes into the provided stack's {@link net.minecraft.core.component.DataComponents#CONTAINER} on
 * save
 */
public class ItemBackedInventory extends SimpleContainer {
	private final ItemStack stack;

	public ItemBackedInventory(ItemStack stack, int expectedSize) {
		super(expectedSize);
		this.stack = stack;

		var contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
		contents.copyInto(getItems());
	}

	@Override
	public boolean stillValid(Player player) {
		return !stack.isEmpty();
	}

	@Override
	public void setChanged() {
		super.setChanged();

		ItemContainerContents contents = ItemContainerContents.fromItems(getItems());
		DataComponentHelper.setUnlessDefault(stack, DataComponents.CONTAINER, contents, ItemContainerContents.EMPTY);
	}
}
