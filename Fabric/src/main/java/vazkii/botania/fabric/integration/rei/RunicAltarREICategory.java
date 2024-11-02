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

public class RunicAltarREICategory implements DisplayCategory<RunicAltarREIDisplay> {
	private final EntryStack<ItemStack> altar = EntryStacks.of(new ItemStack(BotaniaBlocks.runeAltar));
	private final EntryStack<ItemStack> livingrock = EntryStacks.of(new ItemStack(BotaniaBlocks.livingrock));
	private final ResourceLocation PETAL_OVERLAY = BotaniaAPI.botaniaRL("textures/gui/petal_overlay.png");

	@Override
	public @NotNull CategoryIdentifier<RunicAltarREIDisplay> getCategoryIdentifier() {
		return BotaniaREICategoryIdentifiers.RUNE_ALTAR;
	}

	@Override
	public @NotNull Renderer getIcon() {
		return altar;
	}

	@Override
	public @NotNull Component getTitle() {
		return Component.translatable("botania.nei.runicAltar");
	}

	@Override
	public @NotNull List<Widget> setupDisplay(RunicAltarREIDisplay display, Rectangle bounds) {
		// TODO: optimize catalyst handling
		List<EntryIngredient> inputs = new ArrayList<>(display.getInputEntries());
		inputs.addAll(display.getCatalysts());
		EntryStack<?> output = display.getOutputEntries().get(0).get(0);

		double angleBetweenEach = 360.0 / inputs.size();
		FloatingPoint point = new FloatingPoint(bounds.getCenterX() - 8, bounds.getCenterY() - 38);
		Point center = new Point(bounds.getCenterX() - 8, bounds.getCenterY() - 6);
		List<Widget> widgets = new ArrayList<>();
		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.createDrawableWidget(((gui, mouseX, mouseY, delta) -> {
			CategoryUtils.drawOverlay(gui, PETAL_OVERLAY, center.x - 24, center.y - 42, 42, 11, 85, 82);
			HUDHandler.renderManaBar(gui, center.x - 43, center.y + 52, 0x0000FF, 0.75F, display.getManaCost(), ManaPoolBlockEntity.MAX_MANA / 10);
		})));

		for (EntryIngredient o : inputs) {
			widgets.add(Widgets.createSlot(point.getLocation()).entries(o).disableBackground());
			point = CategoryUtils.rotatePointAbout(point, center, angleBetweenEach);
		}
		widgets.add(Widgets.createSlot(new Point(center.x, center.y + 10)).entry(this.altar).disableBackground());
		widgets.add(Widgets.createSlot(new Point(center.x, center.y - 10)).entry(this.livingrock).disableBackground());
		widgets.add(Widgets.createSlot(new Point(center.x + 38, center.y - 35)).entry(output).disableBackground());

		return widgets;
	}

	@Override
	public int getDisplayHeight() {
		return 114;
	}
}
