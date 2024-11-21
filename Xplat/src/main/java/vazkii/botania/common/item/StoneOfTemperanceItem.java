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

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.ItemNBTHelper;

import java.util.List;

public class StoneOfTemperanceItem extends Item {
	public static final String TAG_ACTIVE = "active";

	public StoneOfTemperanceItem(Properties builder) {
		super(builder);
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		toggleActive(stack, player, world);
		return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> stacks, TooltipFlag flags) {
		if (stack.has(BotaniaDataComponents.ACTIVE)) {
			stacks.add(Component.translatable("botaniamisc.active"));
		} else {
			stacks.add(Component.translatable("botaniamisc.inactive"));
		}
	}

	public static boolean hasTemperanceActive(Player player) {
		Container inv = player.getInventory();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty() && stack.is(BotaniaItems.temperanceStone) && ItemNBTHelper.getBoolean(stack, TAG_ACTIVE, false)) {
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
		ItemNBTHelper.setBoolean(stack, TAG_ACTIVE, !ItemNBTHelper.getBoolean(stack, TAG_ACTIVE, false));
		world.playSound(player, player.getX(), player.getY(), player.getZ(), BotaniaSounds.temperanceStoneConfigure, SoundSource.NEUTRAL, 1F, 1F);
	}

}
