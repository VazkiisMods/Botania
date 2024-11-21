/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.lens;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.api.mana.*;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.BotaniaItems;

import java.util.List;

public class LensItem extends Item implements ControlLensItem, CompositableLensItem, TinyPlanetExcempt {
	public static final int PROP_NONE = 0,
			PROP_POWER = 1,
			PROP_ORIENTATION = 1 << 1,
			PROP_TOUCH = 1 << 2,
			PROP_INTERACTION = 1 << 3,
			PROP_DAMAGE = 1 << 4,
			PROP_CONTROL = 1 << 5;

	private static final String TAG_COLOR = "color";
	private static final String TAG_COMPOSITE_LENS = "compositeLens";

	private final Lens lens;
	private final int props;

	public LensItem(Item.Properties builder, Lens lens, int props) {
		super(builder);
		this.lens = lens;
		this.props = props;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> stacks, TooltipFlag flags) {
		int storedColor = getStoredColor(stack);
		if (storedColor != -1) {
			var colorName = Component.translatable(storedColor == 16 ? "botania.color.rainbow" : "color.minecraft." + DyeColor.byId(storedColor));
			TextColor realColor = TextColor.fromRgb(getLensColor(stack, world));
			stacks.add(Component.translatable("botaniamisc.color", colorName).withStyle(s -> s.withColor(realColor)));
		}

		if (lens instanceof StormLens) {
			stacks.add(Component.translatable("botaniamisc.creative").withStyle(ChatFormatting.GRAY));
		}
	}

	@NotNull
	@Override
	public Component getName(@NotNull ItemStack stack) {
		ItemStack compositeLens = getCompositeLens(stack);
		if (compositeLens.isEmpty()) {
			return super.getName(stack);
		}
		String shortKeyA = stack.getDescriptionId() + ".short";
		String shortKeyB = compositeLens.getDescriptionId() + ".short";
		return Component.translatable("item.botania.composite_lens", Component.translatable(shortKeyA), Component.translatable(shortKeyB));
	}

	@Override
	public void apply(ItemStack stack, BurstProperties props, Level level) {
		int storedColor = getStoredColor(stack);
		if (storedColor != -1) {
			props.color = getLensColor(stack, level);
		}

		getLens(stack).apply(stack, props);

		ItemStack compositeLens = getCompositeLens(stack);
		if (!compositeLens.isEmpty() && compositeLens.getItem() instanceof BasicLensItem lens) {
			lens.apply(compositeLens, props, level);
		}
	}

	@Override
	public boolean collideBurst(ManaBurst burst, HitResult pos, boolean isManaBlock, boolean shouldKill, ItemStack stack) {
		shouldKill = getLens(stack).collideBurst(burst, pos, isManaBlock, shouldKill, stack);

		ItemStack compositeLens = getCompositeLens(stack);
		if (!compositeLens.isEmpty() && compositeLens.getItem() instanceof BasicLensItem lens) {
			shouldKill = lens.collideBurst(burst, pos, isManaBlock, shouldKill, compositeLens);
		}

		return shouldKill;
	}

	@Override
	public void updateBurst(ManaBurst burst, ItemStack stack) {
		int storedColor = getStoredColor(stack);

		if (storedColor == 16 && burst.entity().level().isClientSide) {
			burst.setColor(getLensColor(stack, burst.entity().level()));
		}

		getLens(stack).updateBurst(burst, stack);

		ItemStack compositeLens = getCompositeLens(stack);
		if (!compositeLens.isEmpty() && compositeLens.getItem() instanceof BasicLensItem lens) {
			lens.updateBurst(burst, compositeLens);
		}
	}

	@Override
	public int getLensColor(ItemStack stack, Level level) {
		int storedColor = getStoredColor(stack);

		if (storedColor == -1) {
			return 0xFFFFFF;
		}

		if (storedColor == 16) {
			if (level == null) {
				return 0xFFFFFF;
			}
			return Mth.hsvToRgb(level.getGameTime() * 2 % 360 / 360F, 1F, 1F);
		}

		return ColorHelper.getColorValue(DyeColor.byId(storedColor));
	}

	public static int getStoredColor(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_COLOR, -1);
	}

	public static void setLensColor(ItemStack stack, int color) {
		ItemNBTHelper.setInt(stack, TAG_COLOR, color);
	}

	@Override
	public boolean doParticles(ManaBurst burst, ItemStack stack) {
		return true;
	}

	public static boolean isBlacklisted(ItemStack lens1, ItemStack lens2) {
		CompositableLensItem item1 = (CompositableLensItem) lens1.getItem();
		CompositableLensItem item2 = (CompositableLensItem) lens2.getItem();
		return (item1.getProps(lens1) & item2.getProps(lens2)) != 0;
	}

	public static Lens getLens(ItemStack stack) {
		if (stack.getItem() instanceof LensItem lens) {
			return lens.lens;
		} else {
			return new Lens();
		}
	}

	@Override
	public boolean canCombineLenses(ItemStack sourceLens, ItemStack compositeLens) {
		CompositableLensItem sourceItem = (CompositableLensItem) sourceLens.getItem();
		CompositableLensItem compositeItem = (CompositableLensItem) compositeLens.getItem();
		if (sourceItem == compositeItem) {
			return false;
		}

		if (!sourceItem.isCombinable(sourceLens) || !compositeItem.isCombinable(compositeLens)) {
			return false;
		}

		if (isBlacklisted(sourceLens, compositeLens)) {
			return false;
		}

		return true;
	}

	@Override
	public ItemStack getCompositeLens(ItemStack stack) {
		CompoundTag cmp = ItemNBTHelper.getCompound(stack, TAG_COMPOSITE_LENS, true);
		if (cmp == null) {
			return ItemStack.EMPTY;
		} else {
			return ItemStack.of(cmp);
		}
	}

	@Override
	public ItemStack setCompositeLens(ItemStack sourceLens, ItemStack compositeLens) {
		if (compositeLens.isEmpty()) {
			ItemNBTHelper.removeEntry(sourceLens, TAG_COMPOSITE_LENS);
		} else {
			CompoundTag cmp = compositeLens.save(new CompoundTag());
			ItemNBTHelper.setCompound(sourceLens, TAG_COMPOSITE_LENS, cmp);
		}
		return sourceLens;
	}

	@Override
	public int getManaToTransfer(ManaBurst burst, ItemStack stack, ManaReceiver receiver) {
		return getLens(stack).getManaToTransfer(burst, stack, receiver);
	}

	@Override
	public boolean shouldPull(ItemStack stack) {
		return !stack.is(BotaniaItems.lensStorm);
	}

	@Override
	public boolean isControlLens(ItemStack stack) {
		return (getProps(stack) & PROP_CONTROL) != 0;
	}

	@Override
	public boolean allowBurstShooting(ItemStack stack, ManaSpreader spreader, boolean redstone) {
		return getLens(stack).allowBurstShooting(stack, spreader, redstone);
	}

	@Override
	public void onControlledSpreaderTick(ItemStack stack, ManaSpreader spreader, boolean redstone) {
		getLens(stack).onControlledSpreaderTick(stack, spreader, redstone);
	}

	@Override
	public void onControlledSpreaderPulse(ItemStack stack, ManaSpreader spreader) {
		getLens(stack).onControlledSpreaderPulse(stack, spreader);
	}

	@Override
	public int getProps(ItemStack stack) {
		return props;
	}

	@Override
	public boolean isCombinable(ItemStack stack) {
		return !stack.is(BotaniaItems.lensNormal);
	}

}
