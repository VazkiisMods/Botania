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
import vazkii.botania.api.BotaniaAPI;

/**
 * A block with this capability will receive a custom callback when a Hovering
 * Hourglass adjacent to the block turns.
 */
public interface HourglassTrigger {

	ResourceLocation ID = new ResourceLocation(BotaniaAPI.MODID, "hourglass_trigger");

	void onTriggeredByHourglass(BlockEntity hourglass);

}
