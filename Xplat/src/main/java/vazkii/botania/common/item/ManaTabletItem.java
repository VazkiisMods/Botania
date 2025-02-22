/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;

import vazkii.botania.api.mana.ManaBarTooltip;
import vazkii.botania.common.component.BotaniaDataComponents;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;
import java.util.Optional;

public class ManaTabletItem extends Item implements CustomCreativeTabContents {

	public static final int DEFAULT_MAX_MANA = 500000;

	public ManaTabletItem(Properties props) {
		super(props);
	}

	@Override
	public void addToCreativeTab(Item me, CreativeModeTab.Output output) {
		output.accept(me);

		// TODO: maybe use DataComponentPatch
		ItemStack fullPower = new ItemStack(me);
		setMana(fullPower, DEFAULT_MAX_MANA);
		output.accept(fullPower);

		ItemStack creative = new ItemStack(me);
		setMana(creative, DEFAULT_MAX_MANA);
		setStackCreative(creative);
		output.accept(creative);
	}

	/*todo
	@NotNull
	@Override
	public Rarity getRarity(@NotNull ItemStack stack) {
		return isStackCreative(stack) ? Rarity.EPIC : super.getRarity(stack);
	}
	
	 */

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> stacks, TooltipFlag flags) {
		if (isStackCreative(stack)) {
			stacks.add(Component.translatable("botaniamisc.creative").withStyle(ChatFormatting.GRAY));
		}
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return Optional.of(ManaBarTooltip.fromManaItem(stack));
	}

	protected static void setMana(ItemStack stack, int mana) {
		ItemNBTHelper.setIntNonZero(stack, BotaniaDataComponents.MANA, mana);
	}

	public static void setStackCreative(ItemStack stack) {
		stack.set(BotaniaDataComponents.CREATIVE_MANA, Unit.INSTANCE);
		stack.remove(BotaniaDataComponents.CAN_RECEIVE_MANA_FROM_ITEM);
	}

	public static boolean isStackCreative(ItemStack stack) {
		return stack.has(BotaniaDataComponents.CREATIVE_MANA);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return !isStackCreative(stack);
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
