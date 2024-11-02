/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.data.recipes;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import vazkii.botania.common.block.BotaniaBlocks;

public class SmeltingProvider extends BotaniaRecipeProvider {
	public SmeltingProvider(PackOutput packOutput) {
		super(packOutput);
	}

	private static Criterion<InventoryChangeTrigger.TriggerInstance> conditionsFromItem(ItemLike item) {
		return CraftingRecipeProvider.conditionsFromItem(item);
	}

	@Override
	public void buildRecipes(RecipeOutput consumer) {
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(BotaniaBlocks.biomeCobblestoneForest),
				RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.biomeStoneForest, 0.1f, 200)
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.biomeCobblestoneForest))
				.save(consumer, "botania:smelting/metamorphic_forest_stone");
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(BotaniaBlocks.biomeCobblestonePlains),
				RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.biomeStonePlains, 0.1f, 200)
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.biomeCobblestonePlains))
				.save(consumer, "botania:smelting/metamorphic_plains_stone");
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(BotaniaBlocks.biomeCobblestoneMountain),
				RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.biomeStoneMountain, 0.1f, 200)
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.biomeCobblestoneMountain))
				.save(consumer, "botania:smelting/metamorphic_mountain_stone");
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(BotaniaBlocks.biomeCobblestoneFungal),
				RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.biomeStoneFungal, 0.1f, 200)
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.biomeCobblestoneFungal))
				.save(consumer, "botania:smelting/metamorphic_fungal_stone");
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(BotaniaBlocks.biomeCobblestoneSwamp),
				RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.biomeStoneSwamp, 0.1f, 200)
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.biomeCobblestoneSwamp))
				.save(consumer, "botania:smelting/metamorphic_swamp_stone");
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(BotaniaBlocks.biomeCobblestoneDesert),
				RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.biomeStoneDesert, 0.1f, 200)
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.biomeCobblestoneDesert))
				.save(consumer, "botania:smelting/metamorphic_desert_stone");
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(BotaniaBlocks.biomeCobblestoneTaiga),
				RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.biomeStoneTaiga, 0.1f, 200)
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.biomeCobblestoneTaiga))
				.save(consumer, "botania:smelting/metamorphic_taiga_stone");
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(BotaniaBlocks.biomeCobblestoneMesa),
				RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.biomeStoneMesa, 0.1f, 200)
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.biomeCobblestoneMesa))
				.save(consumer, "botania:smelting/metamorphic_mesa_stone");
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(BotaniaBlocks.livingrockBrick),
				RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.livingrockBrickCracked, 0.1f, 200)
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.livingrockBrick))
				.save(consumer, "botania:smelting/cracked_livingrock_bricks");
	}

	@Override
	public String getName() {
		return "Botania smelting recipes";
	}
}
