/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.integration.jei.orechid;

import com.mojang.blaze3d.systems.RenderSystem;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.recipe.OrechidRecipe;
import vazkii.botania.client.integration.shared.OrechidUIHelper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public abstract class OrechidRecipeCategoryBase implements IRecipeCategory<OrechidRecipe> {

	private final IDrawableStatic background;
	private final Component localizedName;
	private final IDrawableStatic overlay;
	private final IDrawable icon;
	private final ItemStack iconStack;

	public OrechidRecipeCategoryBase(IGuiHelper guiHelper, ItemStack iconStack, Component localizedName) {
		overlay = guiHelper.createDrawable(botaniaRL("textures/gui/pure_daisy_overlay.png"),
				0, 0, 64, 44);
		background = guiHelper.createBlankDrawable(96, 44);
		this.localizedName = localizedName;
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconStack);
		this.iconStack = iconStack;
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
	} //todo

	@NotNull
	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull OrechidRecipe recipe, @NotNull IFocusGroup focusGroup) {

		builder.addSlot(RecipeIngredientRole.INPUT, 9, 12)
				.addItemStacks(recipe.getInput().getDisplayedStacks());
		builder.addSlot(RecipeIngredientRole.CATALYST, 39, 12).addItemStack(iconStack);

		builder.addSlot(RecipeIngredientRole.OUTPUT, 68, 12)
				.addItemStacks(recipe.getOutput().getDisplayedStacks())
				.addRichTooltipCallback((view, tooltip) -> tooltip.addAll(recipe.getOutput().descriptionTooltip()));
	}

	@Override
	public void draw(@NotNull OrechidRecipe recipe, @NotNull IRecipeSlotsView view, @NotNull GuiGraphics gui, double mouseX, double mouseY) {
		final Double chance = getChance(recipe);
		if (chance != null) {
			final Component chanceComponent = OrechidUIHelper.getPercentageComponent(chance);
			Font font = Minecraft.getInstance().font;
			int xOffset = 90 - font.width(chanceComponent);
			gui.drawString(font, chanceComponent, xOffset, 1, 0x888888, false);
		}
		RenderSystem.enableBlend();
		overlay.draw(gui, 17, 0);
		RenderSystem.disableBlend();
	}

	@NotNull
	@Override
	public List<Component> getTooltipStrings(@NotNull OrechidRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		if (mouseX > 0.6 * background.getWidth() && mouseX < 90 && mouseY < 12) {
			final Double chance = getChance(recipe);
			if (chance != null) {
				return getChanceTooltipComponents(chance, recipe).toList();
			}
		}
		return Collections.emptyList();
	}

	@NotNull
	protected Stream<Component> getChanceTooltipComponents(double chance, @NotNull OrechidRecipe recipe) {
		final var ratio = OrechidUIHelper.getRatioForChance(chance);
		Stream<Component> genericChanceTooltipComponents = Stream.of(OrechidUIHelper.getRatioTooltipComponent(ratio));
		Stream<Component> biomeChanceTooltipComponents = OrechidUIHelper.getBiomeChanceAndRatioTooltipComponents(chance, recipe);
		return Stream.concat(genericChanceTooltipComponents, biomeChanceTooltipComponents);
	}

	@Nullable
	protected Double getChance(@NotNull OrechidRecipe recipe) {
		return OrechidUIHelper.getChance(recipe, null);
	}
}
