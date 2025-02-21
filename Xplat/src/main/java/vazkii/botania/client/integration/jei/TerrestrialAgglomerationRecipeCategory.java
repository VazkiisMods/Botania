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
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec2;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.recipe.TerrestrialAgglomerationRecipe;
import vazkii.botania.client.gui.HUDHandler;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.lib.LibMisc;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class TerrestrialAgglomerationRecipeCategory implements IRecipeCategory<TerrestrialAgglomerationRecipe> {
	public static final RecipeType<TerrestrialAgglomerationRecipe> TYPE =
			RecipeType.create(LibMisc.MOD_ID, "terra_plate", TerrestrialAgglomerationRecipe.class);

	private final Component localizedName;
	private final IDrawable background;
	private final IDrawable overlay;
	private final IDrawable icon;

	private final IDrawable terraPlate;

	public TerrestrialAgglomerationRecipeCategory(IGuiHelper guiHelper) {
		ResourceLocation location = botaniaRL("textures/gui/terrasteel_jei_overlay.png");
		background = guiHelper.createBlankDrawable(114, 131);
		overlay = guiHelper.createDrawable(location, 42, 29, 64, 64);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BotaniaBlocks.terraPlate));
		localizedName = Component.translatable("botania.nei.terraPlate");

		IDrawable livingrock = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BotaniaBlocks.livingrock));
		terraPlate = new TerrestrialAgglomerationDrawable(livingrock, livingrock,
				guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.LAPIS_BLOCK))
		);
	}

	@NotNull
	@Override
	public RecipeType<TerrestrialAgglomerationRecipe> getRecipeType() {
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
	public void draw(@NotNull TerrestrialAgglomerationRecipe recipe, @NotNull IRecipeSlotsView view, @NotNull GuiGraphics gui, double mouseX, double mouseY) {
		RenderSystem.enableBlend();
		overlay.draw(gui, 25, 14);
		HUDHandler.renderManaBar(gui, 6, 126, 0x0000FF, 0.75F, recipe.getMana(), 100000);
		terraPlate.draw(gui, 35, 92);
		RenderSystem.disableBlend();
	}

	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull TerrestrialAgglomerationRecipe recipe, @NotNull IFocusGroup focusGroup) {
		// TODO 1.19.4 figure out the proper way to get a registry access
		builder.addSlot(RecipeIngredientRole.OUTPUT, 48, 37)
				.addItemStack(recipe.getResultItem(RegistryAccess.EMPTY));

		double angleBetweenEach = 360.0 / recipe.getIngredients().size();
		Vec2 point = new Vec2(48, 5), center = new Vec2(48, 37);

		for (var ingr : recipe.getIngredients()) {
			builder.addSlot(RecipeIngredientRole.INPUT, (int) point.x, (int) point.y)
					.addIngredients(ingr);
			point = PetalApothecaryRecipeCategory.rotatePointAbout(point, center, angleBetweenEach);
		}

		builder.addSlot(RecipeIngredientRole.CATALYST, 48, 92)
				.addItemStack(new ItemStack(BotaniaBlocks.terraPlate));
	}
}
