/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.integration.jei;

import com.mojang.blaze3d.systems.RenderSystem;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.recipe.PureDaisyRecipe;
import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.lib.LibMisc;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class PureDaisyRecipeCategory implements IRecipeCategory<PureDaisyRecipe> {

	public static final RecipeType<PureDaisyRecipe> TYPE = RecipeType.create(LibMisc.MOD_ID, "pure_daisy", PureDaisyRecipe.class);
	private final IDrawable background;
	private final Component localizedName;
	private final IDrawable overlay;
	private final IDrawable icon;
	@SuppressWarnings("rawtypes")
	private final IPlatformFluidHelper fluidHelper;

	public PureDaisyRecipeCategory(IGuiHelper guiHelper, IPlatformFluidHelper<?> fluidHelper) {
		background = guiHelper.createBlankDrawable(96, 44);
		localizedName = Component.translatable("botania.nei.pureDaisy");
		overlay = guiHelper.createDrawable(botaniaRL("textures/gui/pure_daisy_overlay.png"),
				0, 0, 64, 44);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BotaniaFlowerBlocks.pureDaisy));
		this.fluidHelper = fluidHelper;
	}

	@NotNull
	@Override
	public RecipeType<PureDaisyRecipe> getRecipeType() {
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
	public void draw(PureDaisyRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics gui, double mouseX, double mouseY) {
		RenderSystem.enableBlend();
		overlay.draw(gui, 17, 0);
		RenderSystem.disableBlend();
	}

	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull PureDaisyRecipe recipe, @NotNull IFocusGroup focusGroup) {
		buildSlot(recipe.getInput(), builder, RecipeIngredientRole.INPUT, 9, 12);

		builder.addSlot(RecipeIngredientRole.CATALYST, 39, 12)
				.addItemStack(new ItemStack(BotaniaFlowerBlocks.pureDaisy));

		buildSlot(recipe.getOutput(), builder, RecipeIngredientRole.OUTPUT, 68, 12);
	}

	@SuppressWarnings("unchecked")
	private void buildSlot(StateIngredient ingredient, @NotNull IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, int y) {
		IRecipeSlotBuilder inputSlotBuilder = builder.addSlot(role, x, y)
				.setFluidRenderer(1000, false, 16, 16);
		for (var state : ingredient.getDisplayed()) {
			if (!state.getFluidState().isEmpty()) {
				inputSlotBuilder.addIngredient(this.fluidHelper.getFluidIngredientType(),
						this.fluidHelper.create(state.getFluidState().getType(), 1000));
			}
		}
		inputSlotBuilder.addItemStacks(ingredient.getDisplayedStacks())
				.addTooltipCallback((view, tooltip) -> tooltip.addAll(ingredient.descriptionTooltip()));
	}
}
