/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.tool.elementium;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.tool.manasteel.ManasteelShearsItem;

import java.util.List;

public class ElementiumShearsItem extends ManasteelShearsItem {

	public ElementiumShearsItem(Properties props) {
		super(props);
	}

	@NotNull
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 72000;
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		return ItemUtils.startUsingInstantly(world, player, hand);
	}

	@Override
	public void onUseTick(Level world, @NotNull LivingEntity living, @NotNull ItemStack stack, int count) {
		if (world.isClientSide) {
			return;
		}

		if (count != getUseDuration(stack, living) && count % 5 == 0) {
			int range = 12;
			List<Entity> shearables = world.getEntitiesOfClass(Entity.class, new AABB(living.getX() - range, living.getY() - range, living.getZ() - range, living.getX() + range, living.getY() + range, living.getZ() + range), Shearable.class::isInstance);
			if (shearables.size() > 0) {
				for (Entity entity : shearables) {
					if (entity instanceof Shearable shearable && shearable.readyForShearing()) {
						shearable.shear(living.getSoundSource());
						stack.hurtAndBreak(1, living, LivingEntity.getSlotForHand(living.getUsedItemHand()));
						break;
					}
				}
			}
		}
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, @NotNull ItemStack repairBy) {
		return repairBy.is(BotaniaItems.elementium) || super.isValidRepairItem(toRepair, repairBy);
	}

	@Override
	public int getSortingPriority(ItemStack stack, BlockState state) {
		return super.getSortingPriority(stack, state) + 100;
	}
}
