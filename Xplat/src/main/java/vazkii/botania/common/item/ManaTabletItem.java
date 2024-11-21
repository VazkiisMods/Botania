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
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.mana.ManaBarTooltip;
import vazkii.botania.api.mana.ManaItem;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;
import java.util.Optional;

public class ManaTabletItem extends Item implements CustomCreativeTabContents {

	public static final int MAX_MANA = 500000;

	private static final String TAG_MANA = "mana";
	private static final String TAG_CREATIVE = "creative";
	private static final String TAG_ONE_USE = "oneUse";

	public ManaTabletItem(Properties props) {
		super(props);
	}

	@Override
	public void addToCreativeTab(Item me, CreativeModeTab.Output output) {
		output.accept(this);

		ItemStack fullPower = new ItemStack(this);
		setMana(fullPower, MAX_MANA);
		output.accept(fullPower);

		ItemStack creative = new ItemStack(this);
		setMana(creative, MAX_MANA);
		setStackCreative(creative);
		output.accept(creative);
	}

	@NotNull
	@Override
	public Rarity getRarity(@NotNull ItemStack stack) {
		return isStackCreative(stack) ? Rarity.EPIC : super.getRarity(stack);
	}

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
		if (mana > 0) {
			ItemNBTHelper.setInt(stack, TAG_MANA, mana);
		} else {
			ItemNBTHelper.removeEntry(stack, TAG_MANA);
		}
	}

	public static void setStackCreative(ItemStack stack) {
		ItemNBTHelper.setBoolean(stack, TAG_CREATIVE, true);
	}

	public static boolean isStackCreative(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_CREATIVE, false);
	}

	public static class ManaItemImpl implements ManaItem {
		private final ItemStack stack;

		public ManaItemImpl(ItemStack stack) {
			this.stack = stack;
		}

		@Override
		public int getMana() {
			if (isStackCreative(stack)) {
				return getMaxMana();
			}
			return ItemNBTHelper.getInt(stack, TAG_MANA, 0) * stack.getCount();
		}

		@Override
		public int getMaxMana() {
			return (isStackCreative(stack) ? MAX_MANA + 1000 : MAX_MANA) * stack.getCount();
		}

		@Override
		public void addMana(int mana) {
			if (!isStackCreative(stack)) {
				setMana(stack, Math.min(getMana() + mana, getMaxMana()) / stack.getCount());
			}
		}

		@Override
		public boolean canReceiveManaFromPool(BlockEntity pool) {
			return !ItemNBTHelper.getBoolean(stack, TAG_ONE_USE, false);
		}

		@Override
		public boolean canReceiveManaFromItem(ItemStack otherStack) {
			return !isStackCreative(stack);
		}

		@Override
		public boolean canExportManaToPool(BlockEntity pool) {
			return true;
		}

		@Override
		public boolean canExportManaToItem(ItemStack otherStack) {
			return true;
		}

		@Override
		public boolean isNoExport() {
			return false;
		}
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
