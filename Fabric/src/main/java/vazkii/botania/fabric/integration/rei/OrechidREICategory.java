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
import me.shedaniel.rei.api.client.gui.widgets.Label;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.recipe.OrechidRecipe;
import vazkii.botania.client.integration.shared.OrechidUIHelper;
import vazkii.botania.common.block.BotaniaFlowerBlocks;

import java.util.List;
import java.util.stream.Stream;

import static vazkii.botania.fabric.integration.rei.PureDaisyREICategory.setupPureDaisyDisplay;

public class OrechidREICategory implements DisplayCategory<OrechidBaseREIDisplay> {
	private final EntryStack<ItemStack> orechid;
	private final CategoryIdentifier<? extends OrechidBaseREIDisplay> categoryId;
	private final String langKey;

	public OrechidREICategory(CategoryIdentifier<? extends OrechidBaseREIDisplay> categoryId, Block orechid) {
		this.categoryId = categoryId;
		this.orechid = EntryStacks.of(orechid);
		this.langKey = "botania.nei." + (orechid == BotaniaFlowerBlocks.orechidIgnem ? "orechidIgnem" : "orechid");
	}

	@Override
	public @NotNull CategoryIdentifier<? extends OrechidBaseREIDisplay> getCategoryIdentifier() {
		return categoryId;
	}

	@Override
	public @NotNull Renderer getIcon() {
		return orechid;
	}

	@Override
	public @NotNull Component getTitle() {
		return Component.translatable(langKey);
	}

	@Override
	public @NotNull List<Widget> setupDisplay(OrechidBaseREIDisplay display, Rectangle bounds) {
		List<Widget> widgets = setupPureDaisyDisplay(display, bounds, orechid);

		final Double chance = getChance(display.getRecipe().value());
		if (chance != null) {
			final Component chanceComponent = OrechidUIHelper.getPercentageComponent(chance);
			final Point center = new Point(bounds.getCenterX() - 8, bounds.getCenterY() - 9);
			final Label chanceLabel = Widgets.createLabel(new Point(center.x + 51, center.y - 11), chanceComponent)
					.rightAligned().color(0x555555, 0xAAAAAA).noShadow();
			chanceLabel.tooltip(getChanceTooltipComponents(chance, display.getRecipe().value()).toArray(Component[]::new));
			widgets.add(chanceLabel);
		}

		return widgets;
	}

	@NotNull
	protected Stream<Component> getChanceTooltipComponents(double chance, OrechidRecipe recipe) {
		final var ratio = OrechidUIHelper.getRatioForChance(chance);
		Stream<Component> genericChanceTooltipComponents = Stream.of(OrechidUIHelper.getRatioTooltipComponent(ratio));
		Stream<Component> biomeChanceTooltipComponents = OrechidUIHelper.getBiomeChanceAndRatioTooltipComponents(chance, recipe);
		return Stream.concat(genericChanceTooltipComponents, biomeChanceTooltipComponents);
	}

	@Nullable
	protected Double getChance(@NotNull OrechidRecipe recipe) {
		return OrechidUIHelper.getChance(recipe, null);
	}

	@Override
	public int getDisplayHeight() {
		return 54;
	}

	@Override
	public int getDisplayWidth(OrechidBaseREIDisplay display) {
		return 112;
	}
}
