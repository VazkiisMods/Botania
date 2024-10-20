/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import vazkii.botania.api.BotaniaAPI;
import static vazkii.botania.api.BotaniaAPI.botaniaRL;

/**
 * An Item that has this capability can provide blocks to other items that use them.
 * For example, the Black Hole Talisman uses this in order to allow for
 * the Rod of the Shifting Crust to pull blocks from it.
 *
 * Mutations to objects of this type propagate directly to the underlying stack it was retrieved from.
 */
public interface BlockProvider {

	ResourceLocation ID = botaniaRL("block_provider");

	/**
	 * Provides the requested item. The doit paremeter specifies whether this is
	 * just a test (false) or if the item should actually be removed (true).
	 * If you need to use calls to ManaItemHandler.requestMana[Exact], use
	 * the requestor as the ItemStack passed in.
	 */
	boolean provideBlock(Player player, ItemStack requestor, Block block, boolean doit);

	/**
	 * Gets the amount of blocks of the type passed stored in this item. You must
	 * check for the block passed in to not give the counter for a wrong block. Returning
	 * -1 states that the item can provide infinite of the item passed in (for example,
	 * the Rod of the Lands would return -1 if the block is dirt).
	 */
	int getBlockCount(Player player, ItemStack requestor, Block block);

}
