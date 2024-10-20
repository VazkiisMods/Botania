/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.function.BiConsumer;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class BotaniaFeatures {
	public static final ResourceKey<PlacedFeature> MYSTICAL_FLOWERS_PLACED_FEATURE =
			ResourceKey.create(Registries.PLACED_FEATURE, botaniaRL("mystical_flowers"));
	public static final ResourceKey<PlacedFeature> MYSTICAL_MUSHROOMS_PLACED_FEATURE =
			ResourceKey.create(Registries.PLACED_FEATURE, botaniaRL("mystical_mushrooms"));

	public static void registerFeatures(BiConsumer<Feature<?>, ResourceLocation> r) {
		r.accept(new MysticalFlowerFeature(MysticalFlowerConfig.CODEC), botaniaRL("mystical_flower"));
	}

}
