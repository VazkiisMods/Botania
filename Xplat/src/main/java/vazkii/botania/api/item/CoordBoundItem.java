/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.item;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.block.Bound;
import static vazkii.botania.api.BotaniaAPI.botaniaRL;

/**
 * Items with this capability can be bound to a position.
 * That position is highlighted when the item is being held
 *
 * @see Bound
 */
public interface CoordBoundItem {

	ResourceLocation ID = botaniaRL("coord_bound_item");

	@Nullable
	BlockPos getBinding(Level world);

}
