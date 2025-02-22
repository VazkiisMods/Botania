/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.mana;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.xplat.XplatAbstractions;

/**
 * Items with this tooltip component will render a mana bar above the tooltip.
 */
public class ManaBarTooltip implements TooltipComponent {
	private final float percentageFull;
	private final int pickLevel;

	/**
	 * Constructs a tooltip component directly from a stack of mana items.
	 * 
	 * @throws IllegalArgumentException if the item does not have the {@link ManaItem} capability.
	 */
	public static ManaBarTooltip fromManaItem(ItemStack stack) {
		var manaItem = XplatAbstractions.INSTANCE.findManaItem(stack);
		if (manaItem != null) {
			return new ManaBarTooltip(getFractionForDisplay(manaItem));
		}
		throw new IllegalArgumentException("Item does not have the capability " + ManaItem.class.getName());
	}

	/** Convenience method to calculate how full is the mana storing item. */
	public static float getFractionForDisplay(@Nullable ManaItem item) {
		return item != null ? item.getMana() / (float) item.getMaxMana() : 0;
	}

	public ManaBarTooltip(float percentageFull) {
		this(percentageFull, -1);
	}

	/** Used for terra shatterer, for level names displayed next to the bar */
	public ManaBarTooltip(float percentageFull, int pickLevel) {
		this.percentageFull = percentageFull;
		this.pickLevel = pickLevel;
	}

	public float getPercentageFull() {
		return percentageFull;
	}

	public int getPickLevel() {
		return pickLevel;
	}
}
