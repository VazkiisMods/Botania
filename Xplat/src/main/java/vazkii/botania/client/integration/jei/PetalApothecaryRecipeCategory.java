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
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec2;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.recipe.PetalApothecaryRecipe;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.lib.LibMisc;

import java.util.List;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class PetalApothecaryRecipeCategory implements IRecipeCategory<PetalApothecaryRecipe> {

	public static final RecipeType<PetalApothecaryRecipe> TYPE = RecipeType.create(LibMisc.MOD_ID, "petals", PetalApothecaryRecipe.class);
	public static final int CENTER_X = 48;
	public static final int CENTER_Y = 45;
	private final IDrawableStatic background;
	private final Component localizedName;
	private final IDrawableStatic overlay;
	private final IDrawable icon;
	private final Ingredient WATER_BUCKET = Ingredient.of(Items.WATER_BUCKET);

	public PetalApothecaryRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createBlankDrawable(114, 97);
		localizedName = Component.translatable("botania.nei.petalApothecary");
		overlay = guiHelper.createDrawable(botaniaRL("textures/gui/petal_overlay.png"),
				17, 11, 114, 82);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BotaniaBlocks.defaultAltar));
	}

	@NotNull
	@Override
	public RecipeType<PetalApothecaryRecipe> getRecipeType() {
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
	public void draw(@NotNull PetalApothecaryRecipe recipe, @NotNull IRecipeSlotsView slotsView, @NotNull GuiGraphics gui, double mouseX, double mouseY) {
		RenderSystem.enableBlend();
		overlay.draw(gui, 0, 4);
		RenderSystem.disableBlend();
	}

	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull PetalApothecaryRecipe recipe, @NotNull IFocusGroup focusGroup) {
		setRecipeLayout(builder, recipe.getIngredients(), BotaniaBlocks.defaultAltar,
				recipe.getResultItem(RegistryAccess.EMPTY), WATER_BUCKET, recipe.getReagent());
	}

	public static void setRecipeLayout(@NotNull IRecipeLayoutBuilder builder, List<Ingredient> ingredients, Block catalyst, ItemStack output, Ingredient... reagents) {
		Vec2 center = new Vec2(CENTER_X, CENTER_Y);
		if (reagents.length > 0) {
			Vec2 reagentPoint = new Vec2(CENTER_X, CENTER_Y + 10);
			builder.addSlot(RecipeIngredientRole.CATALYST, (int) reagentPoint.x, (int) reagentPoint.y).addItemStack(new ItemStack(catalyst));

			double angleBetweenReagents = 360.0 / (reagents.length + 1);
			for (int i = 0; i < reagents.length; i++) {
				reagentPoint = rotatePointAbout(reagentPoint, center, angleBetweenReagents);
				builder.addSlot(RecipeIngredientRole.INPUT, (int) reagentPoint.x, (int) reagentPoint.y).addIngredients(reagents[i]);
			}
		} else {
			builder.addSlot(RecipeIngredientRole.CATALYST, CENTER_X, CENTER_Y).addItemStack(new ItemStack(catalyst));
		}
		double angleBetweenEach = 360.0 / ingredients.size();
		Vec2 point = new Vec2(CENTER_X, 13);

		for (var ingr : ingredients) {
			builder.addSlot(RecipeIngredientRole.INPUT, (int) point.x, (int) point.y).addIngredients(ingr);
			point = rotatePointAbout(point, center, angleBetweenEach);
		}

		// TODO 1.19.4 figure out the proper way to get a registry access
		builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 10)
				.addItemStack(output);
	}

	public static Vec2 rotatePointAbout(Vec2 in, Vec2 about, double degrees) {
		double rad = degrees * Math.PI / 180.0;
		double newX = Math.cos(rad) * (in.x - about.x) - Math.sin(rad) * (in.y - about.y) + about.x;
		double newY = Math.sin(rad) * (in.x - about.x) + Math.cos(rad) * (in.y - about.y) + about.y;
		return new Vec2((float) newX, (float) newY);
	}
}
