/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.impl.corporea;

import com.google.common.base.Predicates;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.corporea.*;
import vazkii.botania.common.block.block_entity.corporea.CorporeaRetainerBlockEntity;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.*;
import java.util.function.Function;

public class CorporeaHelperImpl implements CorporeaHelper {
	private final WeakHashMap<CorporeaSpark, Set<CorporeaNode>> cachedNetworks = new WeakHashMap<>();

	@Override
	public Set<CorporeaNode> getNodesOnNetwork(CorporeaSpark spark) {
		CorporeaSpark master = spark.getMaster();
		if (master == null) {
			return Collections.emptySet();
		}
		Set<CorporeaSpark> network = master.getConnections();

		var cache = cachedNetworks.get(master);
		if (cache != null) {
			return cache;
		}

		Set<CorporeaNode> nodes = new LinkedHashSet<>();
		if (network != null) {
			for (CorporeaSpark otherSpark : network) {
				if (otherSpark != null) {
					nodes.add(otherSpark.getSparkNode());
				}
			}
		}

		cachedNetworks.put(master, nodes);
		return nodes;
	}

	@Override
	public CorporeaRequestMatcher createMatcher(ItemStack stack, boolean checkNBT) {
		return new CorporeaItemStackMatcher(stack, checkNBT);
	}

	@Override
	public CorporeaRequestMatcher createMatcher(String name) {
		return new CorporeaStringMatcher(name);
	}

	@Override
	public CorporeaResult requestItem(CorporeaRequestMatcher matcher, int itemCount, CorporeaSpark spark, @Nullable LivingEntity entity, boolean doit) {
		List<ItemStack> stacks = new ArrayList<>();
		if (XplatAbstractions.INSTANCE.fireCorporeaRequestEvent(matcher, itemCount, spark, !doit)) {
			return new CorporeaResultImpl(stacks, 0, 0, Object2IntMaps.emptyMap());
		}

		Object2IntMap<CorporeaNode> matchCountByNode = new Object2IntOpenHashMap<>();
		Set<CorporeaNode> nodes = getNodesOnNetwork(spark);
		Map<CorporeaInterceptor, CorporeaSpark> interceptors = new HashMap<>();

		CorporeaRequest request = new CorporeaRequestImpl(matcher, itemCount, entity);
		for (CorporeaNode node : nodes) {
			CorporeaSpark invSpark = node.getSpark();

			BlockEntity te = node.getWorld().getBlockEntity(node.getPos());
			if (te instanceof CorporeaInterceptor interceptor) {
				interceptor.interceptRequest(matcher, itemCount, invSpark, spark, stacks, nodes, doit);
				interceptors.put(interceptor, invSpark);
			}

			var nodeStacks = doit ? node.extractItems(request) : node.countItems(request);
			int sum = 0;
			for (var stack : nodeStacks) {
				sum += stack.getCount();
			}
			matchCountByNode.mergeInt(node, sum, Integer::sum);
			stacks.addAll(nodeStacks);
		}

		for (CorporeaInterceptor interceptor : interceptors.keySet()) {
			interceptor.interceptRequestLast(matcher, itemCount, interceptors.get(interceptor), spark, stacks, nodes, doit);
		}

		return new CorporeaResultImpl(stacks, request.getFound(), request.getExtracted(), matchCountByNode);
	}

	@Override
	public CorporeaSpark getSparkForBlock(Level world, BlockPos pos) {
		List<Entity> sparks = world.getEntitiesOfClass(Entity.class, new AABB(pos.above()), Predicates.instanceOf(CorporeaSpark.class));
		return sparks.isEmpty() ? null : (CorporeaSpark) sparks.get(0);
	}

	@Override
	public int signalStrengthForRequestSize(int requestSize) {
		if (requestSize <= 0) {
			return 0;
		} else if (requestSize >= 16384) {
			return 15;
		} else {
			return Math.min(15, Mth.log2(requestSize) + 1);
		}
	}

	@Override
	public <T extends CorporeaRequestMatcher> void registerRequestMatcher(ResourceLocation id, Class<T> clazz, Function<CompoundTag, T> deserializer) {
		CorporeaRetainerBlockEntity.addCorporeaRequestMatcher(id, clazz, deserializer);
	}

	public void clearCache() {
		cachedNetworks.clear();
	}
}
