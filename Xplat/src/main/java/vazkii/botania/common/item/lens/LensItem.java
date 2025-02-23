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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.api.mana.*;
import vazkii.botania.common.component.BotaniaDataComponents;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.common.helper.DataComponentHelper;
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
		boolean isRainbow = isLensRainbow(stack);
		DyeColor lensColor = getLensColor(stack);
		if (isRainbow || lensColor != null) {

			var colorName = Component.translatable(isRainbow ? "botania.color.rainbow" : "color.minecraft." + lensColor.getName());
			// TODO: can't do rainbow without level reference anymore
			TextColor realColor = TextColor.fromRgb(getLensColor(stack, null));
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
		if (isLensTinted(stack)) {
			props.color = getLensColor(stack, level);
		}

		getLens(stack).apply(stack, props);

		ItemStack compositeLens = getCompositeLens(stack);
		if (!compositeLens.isEmpty() && compositeLens.getItem() instanceof BasicLensItem basicLensItem) {
			basicLensItem.apply(compositeLens, props, level);
		}
	}

	@Override
	public boolean collideBurst(ManaBurst burst, HitResult pos, boolean isManaBlock, boolean shouldKill, ItemStack stack) {
		shouldKill = getLens(stack).collideBurst(burst, pos, isManaBlock, shouldKill, stack);

		ItemStack compositeLens = getCompositeLens(stack);
		if (!compositeLens.isEmpty() && compositeLens.getItem() instanceof BasicLensItem basicLensItem) {
			shouldKill = basicLensItem.collideBurst(burst, pos, isManaBlock, shouldKill, compositeLens);
		}

		return shouldKill;
	}

	@Override
	public void updateBurst(ManaBurst burst, ItemStack stack) {
		if (isLensRainbow(stack) && burst.entity().level().isClientSide) {
			burst.setColor(getLensColor(stack, burst.entity().level()));
		}

		getLens(stack).updateBurst(burst, stack);

		ItemStack compositeLens = getCompositeLens(stack);
		if (!compositeLens.isEmpty() && compositeLens.getItem() instanceof BasicLensItem basicLensItem) {
			basicLensItem.updateBurst(burst, compositeLens);
		}
	}

	@Override
	public int getLensColor(ItemStack stack, @UnknownNullability Level level) {
		if (isLensRainbow(stack)) {
			if (level == null) {
				return 0xFFFFFF;
			}
			return Mth.hsvToRgb(level.getGameTime() * 2 % 360 / 360F, 1F, 1F);
		}

		DyeColor lensColor = getLensColor(stack);

		return lensColor != null ? ColorHelper.getColorValue(lensColor) : 0xFFFFFF;
	}

	public static boolean isLensTinted(ItemStack stack) {
		return stack.has(BotaniaDataComponents.LENS_TINT) || isLensRainbow(stack);
	}

	@Nullable
	public static DyeColor getLensColor(ItemStack stack) {
		return stack.get(BotaniaDataComponents.LENS_TINT);
	}

	public static boolean isLensRainbow(ItemStack stack) {
		return stack.has(BotaniaDataComponents.LENS_RAINBOW_TINT);
	}

	public static void setLensColor(ItemStack stack, DyeColor color) {
		stack.remove(BotaniaDataComponents.LENS_RAINBOW_TINT);
		stack.set(BotaniaDataComponents.LENS_TINT, color);
	}

	public static void setLensRainbow(ItemStack stack) {
		stack.remove(BotaniaDataComponents.LENS_TINT);
		stack.set(BotaniaDataComponents.LENS_RAINBOW_TINT, Unit.INSTANCE);
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
		return stack.getOrDefault(BotaniaDataComponents.ATTACHED_LENS, ItemStack.EMPTY);
	}

	@Override
	public ItemStack setCompositeLens(ItemStack sourceLens, ItemStack compositeLens) {
		DataComponentHelper.setOptional(sourceLens, BotaniaDataComponents.ATTACHED_LENS, compositeLens);
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
