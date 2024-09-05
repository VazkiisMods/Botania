/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.block;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;

/**
 * Any block with this component can be used with the Wand for the Forest for some purpose.
 */
public interface Wandable {

	ResourceLocation ID = new ResourceLocation(BotaniaAPI.MODID, "wandable");

	/**
	 * Called when the block is used by a wand.
	 * 
	 * @param player Null if the block is being wanded by a dispenser
	 */
	boolean onUsedByWand(@Nullable Player player, ItemStack stack, Direction side);

}
