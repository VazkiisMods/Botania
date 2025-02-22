/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import vazkii.botania.common.block.flower.generating.NarslimmusBlockEntity;
import vazkii.botania.common.component.BotaniaDataComponents;
import vazkii.botania.common.helper.ItemNBTHelper;

public class SlimeInABottleItem extends Item {

	public SlimeInABottleItem(Properties builder) {
		super(builder);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (!level.isClientSide) {
			boolean slime = NarslimmusBlockEntity.isSlimeChunk(level, entity.blockPosition());
			ItemNBTHelper.setFlag(stack, BotaniaDataComponents.ACTIVE_TRANSIENT, slime);
		}
	}
}
