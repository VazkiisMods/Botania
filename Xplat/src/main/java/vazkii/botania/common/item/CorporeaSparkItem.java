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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.common.entity.BotaniaEntities;
import vazkii.botania.common.entity.CorporeaSparkEntity;
import vazkii.botania.common.impl.corporea.DummyCorporeaNode;
import vazkii.botania.common.lib.BotaniaTags;

import java.util.List;

public class CorporeaSparkItem extends Item {

	public CorporeaSparkItem(Properties props) {
		super(props);
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		return attachSpark(ctx.getLevel(), ctx.getClickedPos(), ctx.getItemInHand())
				? InteractionResult.sidedSuccess(ctx.getLevel().isClientSide())
				: InteractionResult.PASS;
	}

	private static boolean canPlace(Level world, CorporeaSparkEntity spark) {
		return world.getBlockState(spark.getAttachPos()).is(BotaniaTags.Blocks.CORPOREA_SPARK_OVERRIDE)
				|| !(spark.getSparkNode() instanceof DummyCorporeaNode);
	}

	public static boolean attachSpark(Level world, BlockPos pos, ItemStack stack) {
		CorporeaSparkEntity spark = BotaniaEntities.CORPOREA_SPARK.create(world);
		if (stack.is(BotaniaItems.corporeaSparkMaster)) {
			spark.setMaster(true);
		}
		if (stack.is(BotaniaItems.corporeaSparkCreative)) {
			spark.setCreative(true);
		}
		spark.setPos(pos.getX() + 0.5, pos.getY() + 1.25, pos.getZ() + 0.5);

		if (canPlace(world, spark) && !CorporeaHelper.instance().doesBlockHaveSpark(world, pos)) {
			if (!world.isClientSide) {
				world.addFreshEntity(spark);
				stack.shrink(1);
			}
			return true;
		}
		return false;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
		if (stack.is(BotaniaItems.corporeaSparkCreative)) {
			tooltip.add(Component.translatable("botaniamisc.creativeSpark").withStyle(ChatFormatting.GRAY));
		}
	}
}
