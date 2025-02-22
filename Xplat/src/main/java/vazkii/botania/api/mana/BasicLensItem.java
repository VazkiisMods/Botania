/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.mana;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.UnknownNullability;

/**
 * Have an Item implement this to be counted as a lens for the mana spreader.
 */
public interface BasicLensItem extends LensEffectItem {

	int getLensColor(ItemStack stack, @UnknownNullability Level level);

	/**
	 * Can the source lens be combined with the composite lens? This is called
	 * for both the BasicLensItem instance of ItemStack.getItem() of sourceLens and compositeLens.
	 */
	boolean canCombineLenses(ItemStack sourceLens, ItemStack compositeLens);

	/**
	 * Gets the composite lens in the stack passed in, return empty for none.
	 */
	ItemStack getCompositeLens(ItemStack stack);

	/**
	 * Sets the composite lens for the sourceLens as the compositeLens, returns
	 * the ItemStack with the combination. If compositeLens is empty, this removes
	 * the composite.
	 */
	ItemStack setCompositeLens(ItemStack sourceLens, ItemStack compositeLens);

}
