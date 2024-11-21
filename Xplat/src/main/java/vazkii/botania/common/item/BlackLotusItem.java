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
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import vazkii.botania.api.item.ManaDissolvable;
import vazkii.botania.api.mana.ManaPool;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.EntityHelper;
import vazkii.botania.network.EffectType;
import vazkii.botania.network.clientbound.BotaniaEffectPacket;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;

public class BlackLotusItem extends Item implements ManaDissolvable {

	private static final int MANA_PER = 8000;
	private static final int MANA_PER_T2 = 100000;

	public BlackLotusItem(Properties props) {
		super(props);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return super.isFoil(stack) || stack.is(BotaniaItems.blackerLotus);
	}

	@Override
	public void onDissolveTick(ManaPool pool, ItemEntity item) {
		if (pool.isFull() || pool.getCurrentMana() == 0) {
			return;
		}

		BlockPos pos = pool.getManaReceiverPos();
		boolean t2 = item.getItem().is(BotaniaItems.blackerLotus);

		if (!item.level().isClientSide) {
			pool.receiveMana(t2 ? MANA_PER_T2 : MANA_PER);
			EntityHelper.shrinkItem(item);
			XplatAbstractions.INSTANCE.sendToTracking(item, new BotaniaEffectPacket(EffectType.BLACK_LOTUS_DISSOLVE, pos.getX(), pos.getY() + 0.5, pos.getZ()));
		}

		item.playSound(BotaniaSounds.blackLotus, 1F, t2 ? 0.1F : 1F);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flags) {
		list.add(Component.translatable("botaniamisc.lotusDesc").withStyle(ChatFormatting.GRAY));
	}

}
