/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.integration.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.recipe.BotanicalBreweryRecipe;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.lib.LibMisc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class BreweryRecipeCategory implements IRecipeCategory<BotanicalBreweryRecipe> {

	public static final RecipeType<BotanicalBreweryRecipe> TYPE = RecipeType.create(LibMisc.MOD_ID, "brewery", BotanicalBreweryRecipe.class);
	private final IDrawableStatic background;
	private final IDrawable icon;
	private final Component localizedName;

	public BreweryRecipeCategory(IGuiHelper guiHelper) {
		ResourceLocation location = botaniaRL("textures/gui/nei_brewery.png");
		background = guiHelper.createDrawable(location, 28, 6, 131, 55);
		localizedName = Component.translatable("botania.nei.brewery");
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BotaniaBlocks.brewery));
	}

	@NotNull
	@Override
	public RecipeType<BotanicalBreweryRecipe> getRecipeType() {
		return TYPE;
	}

	@NotNull
	@Override
	public Component getTitle() {
		return localizedName;
	}

	@NotNull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@NotNull
	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull BotanicalBreweryRecipe recipe, @NotNull IFocusGroup focuses) {
		List<ItemStack> outputs = new ArrayList<>();
		List<ItemStack> containers = new ArrayList<>();

		for (var container : new ItemStack[] {
				new ItemStack(BotaniaItems.vial), new ItemStack(BotaniaItems.flask),
				new ItemStack(BotaniaItems.incenseStick), new ItemStack(BotaniaItems.bloodPendant)
		}) {
			ItemStack brewed = recipe.getOutput(container);
			if (!brewed.isEmpty()) {
				containers.add(container);
				outputs.add(brewed);
			}
		}

		IFocus<ItemStack> outputFocus = focuses.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.OUTPUT).findAny().orElse(null);
		IFocus<ItemStack> inputFocus = focuses.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.INPUT).findAny().orElse(null);

		builder.addSlot(RecipeIngredientRole.INPUT, 11, 36)
				.addItemStacks(getItemMatchingFocus(outputFocus, outputs, containers));

		var inputs = recipe.getIngredients();
		int posX = 67 - (inputs.size() * 9);
		for (var ingr : inputs) {
			builder.addSlot(RecipeIngredientRole.INPUT, posX, 0)
					.addIngredients(ingr);
			posX += 18;
		}

		builder.addSlot(RecipeIngredientRole.OUTPUT, 59, 36)
				.addItemStacks(getItemMatchingFocus(inputFocus, containers, outputs));
	}

	/**
	 * If an item in this recipe is focused, returns the corresponding item instead of all the containers/results.
	 */
	private List<ItemStack> getItemMatchingFocus(IFocus<ItemStack> focus, List<ItemStack> focused, List<ItemStack> other) {
		if (focus != null) {
			ItemStack focusStack = focus.getTypedValue().getIngredient();
			for (int i = 0; i < focused.size(); i++) {
				if (ItemStack.isSameItem(focusStack, focused.get(i))) {
					return Collections.singletonList(other.get(i));
				}
			}
		}
		return other;
	}
}
