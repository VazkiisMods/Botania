/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.integration.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.block.BotaniaBlocks;

import java.util.ArrayList;
import java.util.List;

public class BreweryREICategory implements DisplayCategory<BreweryREIDisplay> {
	private final EntryStack<ItemStack> brewery = EntryStacks.of(new ItemStack(BotaniaBlocks.brewery));
	private final ResourceLocation BREWERY_OVERLAY = BotaniaAPI.botaniaRL("textures/gui/nei_brewery.png");

	@Override
	public @NotNull CategoryIdentifier<BreweryREIDisplay> getCategoryIdentifier() {
		return BotaniaREICategoryIdentifiers.BREWERY;
	}

	@Override
	public @NotNull Renderer getIcon() {
		return this.brewery;
	}

	@Override
	public @NotNull Component getTitle() {
		return Component.translatable("botania.nei.brewery");
	}

	@Override
	public @NotNull List<Widget> setupDisplay(BreweryREIDisplay display, Rectangle bounds) {
		List<Widget> widgets = new ArrayList<>();
		List<EntryIngredient> inputs = display.getInputEntries();
		Point center = new Point(bounds.getCenterX() - 8, bounds.getCenterY() + 9);

		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.createDrawableWidget((gui, mouseX, mouseY, delta) -> CategoryUtils.drawOverlay(gui, BREWERY_OVERLAY, center.x - 59, center.y - 36, 28, 6, 86, 55)));

		widgets.add(Widgets.createSlot(new Point(center.x - 48, center.y)).entries(display.getContainers()));
		int posX = center.x - (inputs.size() - 1) * 9;
		for (EntryIngredient o : inputs) {
			widgets.add(Widgets.createSlot(new Point(posX, center.y - 35)).entries(o).disableBackground());
			posX += 18;
		}
		widgets.add(Widgets.createSlot(center).entries(display.getOutputEntries().get(0)));

		return widgets;
	}

	@Override
	public int getDisplayHeight() {
		return 65;
	}
}
