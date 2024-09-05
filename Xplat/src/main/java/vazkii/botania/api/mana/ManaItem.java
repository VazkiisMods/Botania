/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.mana;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import vazkii.botania.api.BotaniaAPI;

/**
 * An item that has this capability can contain mana.
 */
public interface ManaItem {

	ResourceLocation ID = new ResourceLocation(BotaniaAPI.MODID, "mana_item");

	/**
	 * Gets the amount of mana this item contains
	 */
	int getMana();

	/**
	 * Gets the max amount of mana this item can hold.
	 */
	int getMaxMana();

	/**
	 * Adds mana to this item.
	 */
	void addMana(int mana);

	/**
	 * Can this item receive mana from a mana Pool?
	 * 
	 * @param pool The pool it's receiving mana from, can be casted to ManaPool.
	 * @see ManaPool#isOutputtingPower()
	 */
	boolean canReceiveManaFromPool(BlockEntity pool);

	/**
	 * Can this item recieve mana from another item?
	 */
	boolean canReceiveManaFromItem(ItemStack otherStack);

	/**
	 * Can this item export mana to a mana Pool?
	 * 
	 * @param pool The pool it's exporting mana to, can be casted to ManaPool.
	 * @see ManaPool#isOutputtingPower()
	 */
	boolean canExportManaToPool(BlockEntity pool);

	/**
	 * Can this item export mana to another item?
	 */
	boolean canExportManaToItem(ItemStack otherStack);

	/**
	 * If this item simply does not export mana at all, set this to true. This is
	 * used to skip items that contain mana but can't export it when drawing the
	 * mana bar above the XP bar.
	 */
	boolean isNoExport();

}
