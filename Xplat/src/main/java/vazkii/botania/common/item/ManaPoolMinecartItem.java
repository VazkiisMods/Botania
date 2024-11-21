/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.entity.ManaPoolMinecartEntity;

public class ManaPoolMinecartItem extends Item {

	public ManaPoolMinecartItem(Properties builder) {
		super(builder);
	}

	// [VanillaCopy] MinecartItem
	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level world = context.getLevel();
		BlockPos blockPos = context.getClickedPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (!blockState.is(BlockTags.RAILS)) {
			return InteractionResult.FAIL;
		} else {
			ItemStack itemStack = context.getItemInHand();
			if (!world.isClientSide) {
				RailShape railShape = blockState.getBlock() instanceof BaseRailBlock ? blockState.getValue(((BaseRailBlock) blockState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
				double d = 0.0D;
				if (railShape.isAscending()) {
					d = 0.5D;
				}

				AbstractMinecart abstractMinecartEntity = new ManaPoolMinecartEntity(world, (double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.0625D + d, (double) blockPos.getZ() + 0.5D);
				if (itemStack.has(DataComponents.CUSTOM_NAME)) {
					abstractMinecartEntity.setCustomName(itemStack.getHoverName());
				}

				world.addFreshEntity(abstractMinecartEntity);
			}

			itemStack.shrink(1);
			return InteractionResult.sidedSuccess(world.isClientSide);
		}
	}

}
