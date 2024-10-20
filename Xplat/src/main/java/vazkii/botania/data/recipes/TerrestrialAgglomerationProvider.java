/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.data.recipes;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;
import vazkii.botania.common.crafting.TerrestrialAgglomerationRecipe;
import vazkii.botania.common.item.BotaniaItems;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class TerrestrialAgglomerationProvider extends BotaniaRecipeProvider {
	public TerrestrialAgglomerationProvider(PackOutput packOutput) {
		super(packOutput);
	}

	@Override
	public String getName() {
		return "Botania Terra Plate recipes";
	}

	@Override
	public void buildRecipes(RecipeOutput consumer) {
		consumer.accept(idFor("terrasteel_ingot"), new TerrestrialAgglomerationRecipe(
				ManaPoolBlockEntity.MAX_MANA / 2,
				new ItemStack(BotaniaItems.terrasteel), Ingredient.of(BotaniaItems.manaSteel),
				Ingredient.of(BotaniaItems.manaPearl), Ingredient.of(BotaniaItems.manaDiamond)
		), null);
	}

	private static ResourceLocation idFor(String s) {
		return botaniaRL("terra_plate/" + s);
	}
}
