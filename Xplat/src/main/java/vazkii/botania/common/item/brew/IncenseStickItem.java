/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.brew;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.brew.Brew;
import vazkii.botania.api.brew.BrewContainer;
import vazkii.botania.api.brew.BrewItem;
import vazkii.botania.common.brew.BotaniaBrews;
import vazkii.botania.common.component.BotaniaDataComponents;
import vazkii.botania.common.helper.DataComponentHelper;
import vazkii.botania.common.item.CustomCreativeTabContents;

import java.util.List;
import java.util.Objects;

public class IncenseStickItem extends Item implements BrewItem, BrewContainer, CustomCreativeTabContents {

	public static final int TIME_MULTIPLIER = 60;

	public IncenseStickItem(Properties builder) {
		super(builder);
	}

	@Override
	public void addToCreativeTab(Item me, CreativeModeTab.Output output) {
		output.accept(this);
		for (Brew brew : BotaniaAPI.instance().getBrewRegistry()) {
			ItemStack brewStack = getItemForBrew(brew, new ItemStack(this));
			if (!brewStack.isEmpty()) {
				output.accept(brewStack);
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flags) {
		Brew brew = getBrew(stack);
		if (brew == BotaniaBrews.fallbackBrew) {
			list.add(Component.translatable("botaniamisc.notInfused").withStyle(ChatFormatting.LIGHT_PURPLE));
			return;
		}

		list.add(Component.translatable("botaniamisc.brewOf",
				Component.translatable(brew.getTranslationKey(stack))).withStyle(ChatFormatting.LIGHT_PURPLE));
		PotionContents.addPotionTooltip(brew.getPotionEffects(stack), list::add, TIME_MULTIPLIER, context.tickRate());
	}

	@Override
	public Brew getBrew(ItemStack stack) {
		ResourceLocation id = stack.get(BotaniaDataComponents.BREW);
		return Objects.requireNonNull(BotaniaAPI.instance().getBrewRegistry().get(id));
	}

	public static void setBrew(ItemStack stack, Brew brew) {
		setBrew(stack, Objects.requireNonNull(BotaniaAPI.instance().getBrewRegistry().getKey(brew)));
	}

	public static void setBrew(ItemStack stack, ResourceLocation brew) {
		DataComponentHelper.setOptional(stack, BotaniaDataComponents.BREW, brew);
	}

	@Override
	public ItemStack getItemForBrew(Brew brew, ItemStack stack) {
		if (!brew.canInfuseIncense() || brew.getPotionEffects(stack).size() != 1
				|| brew.getPotionEffects(stack).getFirst().getEffect().value().isInstantenous()) {
			return ItemStack.EMPTY;
		}

		ItemStack brewStack = new ItemStack(this);
		setBrew(brewStack, brew);
		return brewStack;
	}

	@Override
	public int getManaCost(Brew brew, ItemStack stack) {
		return brew.getManaCost() * 10;
	}
}
