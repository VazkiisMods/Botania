/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.patchouli.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;

import vazkii.botania.client.gui.HUDHandler;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.util.function.UnaryOperator;

/**
 * A custom component that renders a mana bar.
 * It only has one custom parameter, {@code mana}, which is a semicolon-separated list of mana values.
 * Setting x to {@code -1} will center the component.
 */
public class ManaComponent implements ICustomComponent {
	private transient int x, y;
	private transient int[] manaValues;

	public IVariable mana;

	@Override
	public void build(int componentX, int componentY, int pageNum) {
		this.x = componentX != -1 ? componentX : 7;
		this.y = componentY;
	}

	@Override
	public void render(GuiGraphics gui, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
		Font font = Minecraft.getInstance().font;
		Component manaUsage = Component.translatable("botaniamisc.manaUsage").setStyle(context.getFont());
		gui.drawString(font, manaUsage, x + 102 / 2 - font.width(manaUsage) / 2, y, 0x66000000, false);

		int ratio = 10;
		if (context.isAreaHovered(mouseX, mouseY, x, y - 2, 102, 5 + 20)) {
			ratio = 1;
		}
		HUDHandler.renderManaBar(gui, x, y + 10, 0x0000FF, 0.75F,
				manaValues[(context.getTicksInBook() / 20) % manaValues.length], ManaPoolBlockEntity.MAX_MANA / ratio);

		Component ratioString = Component.translatable("botaniamisc.ratio", ratio).setStyle(context.getFont());
		gui.drawString(font, ratioString, x + 102 / 2 - font.width(ratioString) / 2, y + 15, 0x99000000, false);
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
		IVariable manaVar = lookup.apply(mana);
		manaValues = manaVar.unwrap().isJsonArray() ? manaVar.asStream(registries).map(IVariable::asNumber).mapToInt(Number::intValue).toArray() : new int[] { manaVar.asNumber(0).intValue() };
	}
}
