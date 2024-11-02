/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity.corporea;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.CorporeaRequestMatcher;
import vazkii.botania.api.corporea.CorporeaRequestor;
import vazkii.botania.api.corporea.CorporeaSpark;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.helper.InventoryHelper;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.ArrayList;
import java.util.List;

public class CorporeaFunnelBlockEntity extends BaseCorporeaBlockEntity implements CorporeaRequestor {
	public CorporeaFunnelBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.CORPOREA_FUNNEL, pos, state);
	}

	public void doRequest() {
		CorporeaSpark spark = getSpark();
		if (spark != null && spark.getMaster() != null) {
			List<ItemStack> filter = getFilter();
			if (!filter.isEmpty()) {
				ItemStack stack = filter.get(level.random.nextInt(filter.size()));

				if (!stack.isEmpty()) {
					var matcher = CorporeaHelper.instance().createMatcher(stack, true);
					doCorporeaRequest(matcher, stack.getCount(), spark, null);
				}
			}
		}
	}

	public List<ItemStack> getFilter() {
		List<ItemStack> filter = new ArrayList<>();

		final int[] rotationToStackSize = new int[] {
				1, 2, 4, 8, 16, 32, 48, 64
		};

		for (Direction dir : Direction.values()) {
			List<ItemFrame> frames = level.getEntitiesOfClass(ItemFrame.class, new AABB(worldPosition.relative(dir)));
			for (ItemFrame frame : frames) {
				Direction orientation = frame.getDirection();
				if (orientation == dir) {
					ItemStack stack = frame.getItem();
					if (!stack.isEmpty()) {
						ItemStack copy = stack.copyWithCount(rotationToStackSize[frame.getRotation()]);
						filter.add(copy);
					}
				}
			}
		}

		return filter;
	}

	@Override
	public void doCorporeaRequest(CorporeaRequestMatcher request, int count, CorporeaSpark spark, @Nullable LivingEntity entity) {
		BlockPos invPos = getInvPos();

		List<ItemStack> stacks = CorporeaHelper.instance().requestItem(request, count, spark, entity, true).stacks();
		spark.onItemsRequested(stacks);
		for (ItemStack reqStack : stacks) {
			if (invPos != null
					&& XplatAbstractions.INSTANCE.insertToInventory(level, invPos, Direction.UP, reqStack, true).isEmpty()) {
				InventoryHelper.checkEmpty(
						XplatAbstractions.INSTANCE.insertToInventory(level, invPos, Direction.UP, reqStack, false)
				);
			} else {
				ItemEntity item = new ItemEntity(level, spark.entity().getX(), spark.entity().getY(), spark.entity().getZ(), reqStack);
				level.addFreshEntity(item);
			}
		}
	}

	@Nullable
	private BlockPos getInvPos() {
		BlockPos downOne = worldPosition.below();
		if (XplatAbstractions.INSTANCE.hasInventory(level, downOne, Direction.UP)) {
			return downOne;
		}

		BlockPos downTwo = worldPosition.below(2);
		if (XplatAbstractions.INSTANCE.hasInventory(level, downTwo, Direction.UP)) {
			return downTwo;
		}

		return null;
	}

}
