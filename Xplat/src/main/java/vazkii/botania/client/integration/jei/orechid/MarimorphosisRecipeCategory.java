package vazkii.botania.client.integration.jei.orechid;

import mezz.jei.api.helpers.IGuiHelper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.recipe.OrechidRecipe;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.lib.LibMisc;

public class MarimorphosisRecipeCategory extends OrechidRecipeCategoryBase {
	public static final mezz.jei.api.recipe.RecipeType<OrechidRecipe> TYPE =
			mezz.jei.api.recipe.RecipeType.create(LibMisc.MOD_ID, "marimorphosis", OrechidRecipe.class);

	public MarimorphosisRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper, new ItemStack(BotaniaFlowerBlocks.marimorphosis), Component.translatable("botania.nei.marimorphosis"));
	}

	@NotNull
	@Override
	public mezz.jei.api.recipe.RecipeType<OrechidRecipe> getRecipeType() {
		return TYPE;
	}
}
