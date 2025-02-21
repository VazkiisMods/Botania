/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class BotaniaStats {
	public static final ResourceLocation CORPOREA_ITEMS_REQUESTED =
			makeCustomStat("corporea_items_requested", StatFormatter.DEFAULT);

	public static final ResourceLocation LUMINIZER_ONE_CM =
			makeCustomStat("luminizer_one_cm", StatFormatter.DISTANCE);

	public static final ResourceLocation TINY_POTATOES_PETTED =
			makeCustomStat("tiny_potatoes_petted", StatFormatter.DEFAULT);

	public static void init() {

	}

	// [VanillaCopy] net.minecraft.stats.Stats#makeCustomStat, except applying Botania namespace instead of hardcoded Minecraft namespace
	private static ResourceLocation makeCustomStat(String key, StatFormatter formatter) {
		ResourceLocation resourcelocation = botaniaRL(key);
		Registry.register(BuiltInRegistries.CUSTOM_STAT, key, resourcelocation);
		Stats.CUSTOM.get(resourcelocation, formatter);
		return resourcelocation;
	}
}
