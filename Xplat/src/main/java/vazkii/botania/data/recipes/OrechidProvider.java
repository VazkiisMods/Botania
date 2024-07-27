/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.data.recipes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.crafting.*;
import vazkii.botania.common.lib.BotaniaTags;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class OrechidProvider extends BotaniaRecipeProvider {

	public OrechidProvider(PackOutput packOutput) {
		super(packOutput);
	}

	// TODO: We had an enormous amount of ores defined for mod compat.
	//       The old data needs to be completely revised.
	@Override
	public void buildRecipes(RecipeOutput consumer) {
		stone(consumer, Blocks.COAL_ORE, 67415);
		stone(consumer, Blocks.IRON_ORE, 29371);
		stone(consumer, Blocks.REDSTONE_ORE, 7654);
		stone(consumer, Blocks.COPPER_ORE, 7000);
		stone(consumer, Blocks.GOLD_ORE, 2647);
		stone(consumer, Blocks.EMERALD_ORE, 1239);
		stone(consumer, Blocks.LAPIS_ORE, 1079);
		stone(consumer, Blocks.DIAMOND_ORE, 883);

		deepslate(consumer, Blocks.DEEPSLATE_COAL_ORE, 75);
		deepslate(consumer, Blocks.DEEPSLATE_IRON_ORE, 250);
		deepslate(consumer, Blocks.DEEPSLATE_REDSTONE_ORE, 150);
		deepslate(consumer, Blocks.DEEPSLATE_COPPER_ORE, 75);
		deepslate(consumer, Blocks.DEEPSLATE_GOLD_ORE, 125);
		deepslate(consumer, Blocks.DEEPSLATE_EMERALD_ORE, 50);
		deepslate(consumer, Blocks.DEEPSLATE_LAPIS_ORE, 175);
		deepslate(consumer, Blocks.DEEPSLATE_DIAMOND_ORE, 100);

		netherrack(consumer, Blocks.NETHER_QUARTZ_ORE, 19600);
		netherrack(consumer, Blocks.NETHER_GOLD_ORE, 3635);
		netherrack(consumer, Blocks.ANCIENT_DEBRIS, 148);

		biomeStone(consumer, BotaniaBlocks.biomeStoneForest, BotaniaTags.Biomes.MARIMORPHOSIS_FOREST_BONUS);
		biomeStone(consumer, BotaniaBlocks.biomeStonePlains, BotaniaTags.Biomes.MARIMORPHOSIS_PLAINS_BONUS);
		biomeStone(consumer, BotaniaBlocks.biomeStoneMountain, BotaniaTags.Biomes.MARIMORPHOSIS_MOUNTAIN_BONUS);
		biomeStone(consumer, BotaniaBlocks.biomeStoneFungal, BotaniaTags.Biomes.MARIMORPHOSIS_FUNGAL_BONUS);
		biomeStone(consumer, BotaniaBlocks.biomeStoneSwamp, BotaniaTags.Biomes.MARIMORPHOSIS_SWAMP_BONUS);
		biomeStone(consumer, BotaniaBlocks.biomeStoneDesert, BotaniaTags.Biomes.MARIMORPHOSIS_DESERT_BONUS);
		biomeStone(consumer, BotaniaBlocks.biomeStoneTaiga, BotaniaTags.Biomes.MARIMORPHOSIS_TAIGA_BONUS);
		biomeStone(consumer, BotaniaBlocks.biomeStoneMesa, BotaniaTags.Biomes.MARIMORPHOSIS_MESA_BONUS);
	}

	protected ResourceLocation orechidId(Block b) {
		return prefix("orechid/" + BuiltInRegistries.BLOCK.getKey(b).getPath());
	}

	protected ResourceLocation ignemId(Block b) {
		return prefix("orechid_ignem/" + BuiltInRegistries.BLOCK.getKey(b).getPath());
	}

	protected ResourceLocation marimorphosisId(Block b) {
		return prefix("marimorphosis/" + BuiltInRegistries.BLOCK.getKey(b).getPath());
	}

	protected void stone(RecipeOutput consumer, Block output, int weight) {
		consumer.accept(orechidId(output), new OrechidRecipe(forBlock(Blocks.STONE),
				forBlock(output), weight, null), null);
	}

	protected void deepslate(RecipeOutput consumer, Block output, int weight) {
		consumer.accept(orechidId(output), new OrechidRecipe(forBlock(Blocks.DEEPSLATE),
				forBlock(output), weight, null), null);
	}

	protected void netherrack(RecipeOutput consumer, Block output, int weight) {
		consumer.accept(ignemId(output), new OrechidIgnemRecipe(forBlock(Blocks.NETHERRACK),
				forBlock(output), weight, null), null);
	}

	protected void biomeStone(RecipeOutput consumer, Block output, TagKey<Biome> biome) {
		consumer.accept(marimorphosisId(output), new MarimorphosisRecipe(forTag(BotaniaTags.Blocks.MARIMORPHOSIS_CONVERTABLE),
				forBlock(output), 1, null, 11, biome), null);
	}

	protected static StateIngredient forBlock(Block block) {
		return StateIngredients.of(block);
	}

	protected static StateIngredient forTag(TagKey<Block> tag) {
		return StateIngredients.of(tag);
	}

	@Override
	public String getName() {
		return "Botania Orechid and Marimorphosis recipes";
	}

}
