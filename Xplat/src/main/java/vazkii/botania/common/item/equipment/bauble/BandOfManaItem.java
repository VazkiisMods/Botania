/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.bauble;

import net.minecraft.util.Mth;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.api.mana.ManaBarTooltip;
import vazkii.botania.common.component.BotaniaDataComponents;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.CustomCreativeTabContents;
import vazkii.botania.common.item.ManaTabletItem;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.Optional;

public class BandOfManaItem extends BaubleItem implements CustomCreativeTabContents {

	public static final int DEFAULT_MAX_MANA = ManaTabletItem.DEFAULT_MAX_MANA;
	public static final int DEFAULT_GREATER_MAX_MANA = DEFAULT_MAX_MANA * 4;

	private static final String TAG_MANA = "mana";

	public BandOfManaItem(Properties props) {
		super(props);
	}

	@Override
	public void addToCreativeTab(Item me, CreativeModeTab.Output output) {
		output.accept(me);

		ItemStack full = new ItemStack(me);
		setMana(full, me.components().getOrDefault(BotaniaDataComponents.MAX_MANA, DEFAULT_MAX_MANA));
		output.accept(full);
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return Optional.of(ManaBarTooltip.fromManaItem(stack));
	}

	protected static void setMana(ItemStack stack, int mana) {
		ItemNBTHelper.setIntNonZero(stack, BotaniaDataComponents.MANA, mana);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		var manaItem = XplatAbstractions.INSTANCE.findManaItem(stack);
		return Math.round(13 * ManaBarTooltip.getFractionForDisplay(manaItem));
	}

	@Override
	public int getBarColor(ItemStack stack) {
		var manaItem = XplatAbstractions.INSTANCE.findManaItem(stack);
		return Mth.hsvToRgb(ManaBarTooltip.getFractionForDisplay(manaItem) / 3.0F, 1.0F, 1.0F);
	}
}
