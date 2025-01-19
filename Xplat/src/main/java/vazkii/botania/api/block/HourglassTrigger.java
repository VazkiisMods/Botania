/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

/**
 * A block with this capability will receive a custom callback when a Hovering
 * Hourglass adjacent to the block turns.
 */
public interface HourglassTrigger {

	ResourceLocation ID = botaniaRL("hourglass_trigger");

	void onTriggeredByHourglass(BlockEntity hourglass);

}
