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
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import vazkii.botania.api.corporea.CorporeaInterceptor;
import vazkii.botania.api.corporea.CorporeaNode;
import vazkii.botania.api.corporea.CorporeaRequestMatcher;
import vazkii.botania.api.corporea.CorporeaSpark;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CorporeaInterceptorBlockEntity extends BaseCorporeaBlockEntity implements CorporeaInterceptor {
	public CorporeaInterceptorBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.CORPOREA_INTERCEPTOR, pos, state);
	}

	@Override
	public void interceptRequest(CorporeaRequestMatcher request, int count, CorporeaSpark spark, CorporeaSpark source, List<ItemStack> stacks, Set<CorporeaNode> nodes, boolean doit) {}

	@Override
	public void interceptRequestLast(CorporeaRequestMatcher request, int count, CorporeaSpark spark, CorporeaSpark source, List<ItemStack> stacks, Set<CorporeaNode> nodes, boolean doit) {
		List<ItemStack> filter = getFilter();

		boolean filterMatch = false;
		for (ItemStack stack : filter) {
			if (request.test(stack)) {
				filterMatch = true;
			}
		}

		if (filterMatch || filter.isEmpty()) {
			int missing = count;
			for (ItemStack stack : stacks) {
				missing -= stack.getCount();
			}

			if (missing > 0 && !getBlockState().getValue(BlockStateProperties.POWERED)) {
				BlockPos requestorPos = source.getSparkNode().getPos();

				List<CorporeaRetainerBlockEntity> retainers = new ArrayList<>();
				for (Direction dir : Direction.values()) {
					BlockEntity tile = level.getBlockEntity(worldPosition.relative(dir));
					if (tile instanceof CorporeaRetainerBlockEntity retainer) {
						retainers.add(retainer);
						retainer.forget();
					}
				}

				level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(BlockStateProperties.POWERED, true));
				level.scheduleTick(getBlockPos(), getBlockState().getBlock(), 2);

				for (CorporeaRetainerBlockEntity retainer : retainers) {
					retainer.remember(requestorPos, request, count, missing);
				}
			}
		}
	}

	private List<ItemStack> getFilter() {
		List<ItemStack> filter = new ArrayList<>();

		for (Direction dir : Direction.values()) {
			List<ItemFrame> frames = level.getEntitiesOfClass(ItemFrame.class, new AABB(worldPosition.relative(dir)));
			for (ItemFrame frame : frames) {
				Direction orientation = frame.getDirection();
				if (orientation == dir) {
					ItemStack stack = frame.getItem();
					if (!stack.isEmpty()) {
						filter.add(stack);
					}
				}
			}
		}

		return filter;
	}

}
