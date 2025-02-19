/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.tool.manasteel;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.item.SortableTool;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.gui.ItemsRemainingRenderHandler;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.item.equipment.CustomDamageItem;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

import java.util.function.Consumer;
import java.util.regex.Pattern;

public class ManasteelAxeItem extends AxeItem implements CustomDamageItem, SortableTool {

	private static final Pattern SAPLING_PATTERN = Pattern.compile("(?:(?:(?:[A-Z-_.:]|^)sapling)|(?:(?:[a-z-_.:]|^)Sapling))(?:[A-Z-_.:]|$)");

	private static final int MANA_PER_DAMAGE = 60;

	public ManasteelAxeItem(Properties props) {
		this(BotaniaAPI.instance().getManasteelItemTier(), props.attributes(ManasteelAxeItem.createAttributes(BotaniaAPI.instance().getManasteelItemTier(), 6F, -3.1F)));
	}

	public ManasteelAxeItem(Tier mat, Properties props) {
		super(mat, props);
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> breakCallback) {
		int manaPerDamage = ((ManasteelAxeItem) stack.getItem()).getManaPerDamage();
		return ToolCommons.damageItemIfPossible(stack, amount, entity, manaPerDamage);
	}

	public int getManaPerDamage() {
		return MANA_PER_DAMAGE;
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Player player = ctx.getPlayer();
		if (player != null) {
			if (ctx.getHand() == InteractionHand.MAIN_HAND && player.getOffhandItem().getItem() instanceof BlockItem) {
				return InteractionResult.PASS;
			}

			for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
				ItemStack stackAt = player.getInventory().getItem(i);
				if (!stackAt.isEmpty() && SAPLING_PATTERN.matcher(stackAt.getItem().getDescriptionId()).find()) {
					ItemStack displayStack = stackAt.copy();
					var result = PlayerHelper.substituteUse(ctx, stackAt);
					if (result.consumesAction()) {
						if (!ctx.getLevel().isClientSide) {
							ItemsRemainingRenderHandler.send(player, displayStack, SAPLING_PATTERN);
						}
						return result;
					}
				}
			}
		}

		return super.useOn(ctx);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		if (!world.isClientSide && entity instanceof Player player && stack.getDamageValue() > 0 && ManaItemHandler.instance().requestManaExactForTool(stack, player, getManaPerDamage() * 2, true)) {
			stack.setDamageValue(stack.getDamageValue() - 1);
		}
	}

	@Override
	public int getSortingPriority(ItemStack stack, BlockState state) {
		return ToolCommons.getToolPriority(stack);
	}
}
