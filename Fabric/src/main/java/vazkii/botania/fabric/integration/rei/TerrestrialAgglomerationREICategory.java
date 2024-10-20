/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.integration.rei;

import me.shedaniel.math.FloatingPoint;
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
import vazkii.botania.client.gui.HUDHandler;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class TerrestrialAgglomerationREICategory implements DisplayCategory<TerrestrialAgglomerationREIDisplay> {
	private static final ResourceLocation OVERLAY = BotaniaAPI.botaniaRL("textures/gui/terrasteel_jei_overlay.png");
	private final EntryStack<ItemStack> icon = EntryStacks.of(BotaniaBlocks.terraPlate);

	@Override
	public @NotNull CategoryIdentifier<TerrestrialAgglomerationREIDisplay> getCategoryIdentifier() {
		return BotaniaREICategoryIdentifiers.TERRA_PLATE;
	}

	@Override
	public @NotNull Renderer getIcon() {
		return icon;
	}

	@Override
	public @NotNull Component getTitle() {
		return Component.translatable("botania.nei.terraPlate");
	}

	@Override
	public @NotNull List<Widget> setupDisplay(TerrestrialAgglomerationREIDisplay display, Rectangle bounds) {
		List<Widget> widgets = new ArrayList<>();
		List<EntryIngredient> inputs = display.getInputEntries();
		EntryStack<?> output = display.getOutputEntries().get(0).get(0);

		double angleBetweenEach = 360.0 / inputs.size();
		FloatingPoint point = new FloatingPoint(bounds.getCenterX() - 8, bounds.getCenterY() - 44);
		Point center = new Point(bounds.getCenterX() - 8, bounds.getCenterY() - 12);
		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.createDrawableWidget(((gui, mouseX, mouseY, delta) -> {
			CategoryUtils.drawOverlay(gui, OVERLAY, center.x - 24, center.y - 24, 42, 29, 64, 64);
			HUDHandler.renderManaBar(gui, center.x - 43, center.y + 52, 0x0000FF, 0.75F, display.getManaCost(), ManaPoolBlockEntity.MAX_MANA / 10);
		})));

		for (EntryIngredient o : inputs) {
			widgets.add(Widgets.createSlot(point.getLocation()).entries(o).disableBackground());
			point = CategoryUtils.rotatePointAbout(point, center, angleBetweenEach);
		}
		widgets.add(Widgets.createSlot(center).entry(output).disableBackground());

		return widgets;
	}

	@Override
	public int getDisplayHeight() {
		return 103;
	}
}
