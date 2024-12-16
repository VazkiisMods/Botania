/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.dispenser;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;

public class SeedBehaviors {

	// This entire class is a copy from Quark, for the seed planting feature
	// Quark's own feature disables itself if Botania is present

	public static void init() {
		DispenserBlock.registerBehavior(Items.WHEAT_SEEDS, new SeedsBehavior(Blocks.WHEAT));
		DispenserBlock.registerBehavior(Items.POTATO, new SeedsBehavior(Blocks.POTATOES));
		DispenserBlock.registerBehavior(Items.CARROT, new SeedsBehavior(Blocks.CARROTS));
		DispenserBlock.registerBehavior(Items.NETHER_WART, new SeedsBehavior(Blocks.NETHER_WART));
		DispenserBlock.registerBehavior(Items.PUMPKIN_SEEDS, new SeedsBehavior(Blocks.PUMPKIN_STEM));
		DispenserBlock.registerBehavior(Items.MELON_SEEDS, new SeedsBehavior(Blocks.MELON_STEM));
		DispenserBlock.registerBehavior(Items.BEETROOT_SEEDS, new SeedsBehavior(Blocks.BEETROOTS));
		DispenserBlock.registerBehavior(Items.TORCHFLOWER_SEEDS, new SeedsBehavior(Blocks.TORCHFLOWER_CROP));
		DispenserBlock.registerBehavior(Items.PITCHER_POD, new SeedsBehavior(Blocks.PITCHER_CROP));
		DispenserBlock.registerBehavior(Blocks.CHORUS_FLOWER, new SeedsBehavior(Blocks.CHORUS_FLOWER));

		DispenserBlock.registerBehavior(Items.COCOA_BEANS, new CocoaBeansBehavior());
	}

}
