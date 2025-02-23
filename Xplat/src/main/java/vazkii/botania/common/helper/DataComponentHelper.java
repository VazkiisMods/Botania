/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 */

package vazkii.botania.common.helper;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.mana.ManaItem;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.Collection;

public final class DataComponentHelper {
	/**
	 * Sets an Integer component of the ItemStack to the specified value.
	 * If the value is zero, that component is removed instead.
	 */
	@Contract(mutates = "param1")
	public static void setIntNonZero(ItemStack stack, DataComponentType<Integer> component, int value) {
		if (value == 0) {
			stack.remove(component);
		} else {
			stack.set(component, value);
		}
	}

	/**
	 * Ensures a {@link Unit} component is present on the ItemStack if the value is true,
	 * otherwise ensures that component is not present.
	 */
	@Contract(mutates = "param1")
	public static void setFlag(ItemStack stack, DataComponentType<Unit> component, boolean value) {
		if (value) {
			stack.set(component, Unit.INSTANCE);
		} else {
			stack.remove(component);
		}
	}

	/**
	 * Sets a component of the ItemStack to a value. If that value is null, the component is removed instead.
	 */
	@Contract(mutates = "param1")
	public static <T> void setOptional(ItemStack stack, DataComponentType<? super T> component, @Nullable T value) {
		if (value == null) {
			stack.remove(component);
		} else {
			stack.set(component, value);
		}
	}

	/**
	 * Sets a component of the ItemStack to a non-default value.
	 * If the value is null or equal to the specified default value, the component is removed instead.
	 */
	@Contract(mutates = "param1")
	public static <T> void setUnlessDefault(ItemStack stack, DataComponentType<? super T> component, @Nullable T value, T defaultValue) {
		if (value == null || value.equals(defaultValue)) {
			stack.remove(component);
		} else {
			stack.set(component, value);
		}
	}

	/**
	 * Sets a collection component of the ItemStack to the specified collection.
	 * If that collection is null or empty, the component is removed instead.
	 */
	@Contract(mutates = "param1")
	public static <C extends Collection<?>> void setNonEmpty(ItemStack stack, DataComponentType<C> component, @Nullable C collection) {
		if (collection == null || collection.isEmpty()) {
			stack.remove(component);
		} else {
			stack.set(component, collection);
		}
	}

	/**
	 * Returns the fullness of the mana item:
	 * 0 if empty, 1 if partially full, 2 if full.
	 */
	public static int getFullness(ManaItem item) {
		int mana = item.getMana();
		if (mana == 0) {
			return 0;
		} else if (mana == item.getMaxMana()) {
			return 2;
		} else {
			return 1;
		}
	}

	public static ItemStack duplicateAndClearMana(ItemStack stack) {
		ItemStack copy = stack.copy();
		ManaItem manaItem = XplatAbstractions.INSTANCE.findManaItem(copy);
		if (manaItem != null) {
			manaItem.addMana(-manaItem.getMana());
		}
		return copy;
	}

	/**
	 * Checks if two items are the same and have the same NBT. If they are `IManaItems`, their mana property is matched
	 * on whether they are empty, partially full, or full.
	 */
	public static boolean matchTagAndManaFullness(ItemStack stack1, ItemStack stack2) {
		if (!ItemStack.isSameItem(stack1, stack2)) {
			return false;
		}
		ManaItem manaItem1 = XplatAbstractions.INSTANCE.findManaItem(stack1);
		ManaItem manaItem2 = XplatAbstractions.INSTANCE.findManaItem(stack2);
		if (manaItem1 != null && manaItem2 != null) {
			if (getFullness(manaItem1) != getFullness(manaItem2)) {
				return false;
			} else {
				return ItemStack.matches(duplicateAndClearMana(stack1), duplicateAndClearMana(stack2));
			}
		}
		return ItemStack.isSameItemSameComponents(stack1, stack2);
	}
}
