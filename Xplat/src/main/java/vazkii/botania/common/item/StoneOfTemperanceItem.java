/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import vazkii.botania.common.component.BotaniaDataComponents;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.DataComponentHelper;

import java.util.List;

public class StoneOfTemperanceItem extends Item {

	public StoneOfTemperanceItem(Properties builder) {
		super(builder);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		toggleActive(stack, player, world);
		return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> stacks, TooltipFlag flags) {
		stacks.add(Component.translatable(
				stack.has(BotaniaDataComponents.ACTIVE) ? "botaniamisc.active" : "botaniamisc.inactive"));
	}

	public static boolean hasTemperanceActive(Player player) {
		Container inv = player.getInventory();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty() && stack.is(BotaniaItems.temperanceStone) && stack.has(BotaniaDataComponents.ACTIVE)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack cursor, Slot slot, ClickAction click, Player player, SlotAccess access) {
		Level world = player.level();
		if (click == ClickAction.SECONDARY && slot.allowModification(player) && cursor.isEmpty()) {
			toggleActive(stack, player, world);
			access.set(cursor);
			return true;
		}
		return false;
	}

	private void toggleActive(ItemStack stack, Player player, Level world) {
		DataComponentHelper.setFlag(stack, BotaniaDataComponents.ACTIVE, !stack.has(BotaniaDataComponents.ACTIVE));
		world.playSound(player, player.getX(), player.getY(), player.getZ(), BotaniaSounds.temperanceStoneConfigure, SoundSource.NEUTRAL, 1F, 1F);
	}

}
