/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.data.recipes;

import net.minecraft.commands.CacheableFunction;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.crafting.PureDaisyRecipe;
import vazkii.botania.common.crafting.StateIngredients;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class PureDaisyProvider extends BotaniaRecipeProvider {
	public PureDaisyProvider(PackOutput packOutput) {
		super(packOutput);
	}

	@Override
	public void buildRecipes(RecipeOutput consumer) {

		normal(consumer, id("livingrock"), StateIngredients.of(Blocks.STONE), BotaniaBlocks.livingrock.defaultBlockState());
		stateCopying(consumer, id("livingwood"), StateIngredients.of(BlockTags.LOGS), BotaniaBlocks.livingwoodLog);

		normal(consumer, id("cobblestone"), StateIngredients.of(Blocks.NETHERRACK), Blocks.COBBLESTONE.defaultBlockState());
		withFunction(consumer, id("end_stone_to_cobbled_deepslate"), StateIngredients.of(Blocks.END_STONE),
				Blocks.COBBLED_DEEPSLATE.defaultBlockState(), new CacheableFunction(prefix("ender_air_release")));
		normal(consumer, id("sand"), StateIngredients.of(Blocks.SOUL_SAND), Blocks.SAND.defaultBlockState());
		normal(consumer, id("packed_ice"), StateIngredients.of(Blocks.ICE), Blocks.PACKED_ICE.defaultBlockState());
		normal(consumer, id("blue_ice"), StateIngredients.of(Blocks.PACKED_ICE), Blocks.BLUE_ICE.defaultBlockState());
		normal(consumer, id("obsidian"), StateIngredients.of(BotaniaBlocks.blazeBlock), Blocks.OBSIDIAN.defaultBlockState());
		normal(consumer, id("snow_block"), StateIngredients.of(Blocks.WATER), Blocks.SNOW_BLOCK.defaultBlockState());
	}

	@Override
	public String getName() {
		return "Botania Pure Daisy recipes";
	}

	private void normal(RecipeOutput consumer, ResourceLocation id, StateIngredient input, BlockState output) {
		withFunction(consumer, id, input, output, null);
	}

	private void withFunction(RecipeOutput consumer, ResourceLocation id, StateIngredient input, BlockState output, @Nullable CacheableFunction successFunction) {
		consumer.accept(id, new PureDaisyRecipe(input, StateIngredients.of(output), 150, false, successFunction), null);
	}

	private void stateCopying(RecipeOutput consumer, ResourceLocation id, StateIngredient input, Block output) {
		consumer.accept(id, new PureDaisyRecipe(input, StateIngredients.of(output), 150, true, null), null);
	}

	private static ResourceLocation id(String path) {
		return prefix("pure_daisy/" + path);
	}
}
