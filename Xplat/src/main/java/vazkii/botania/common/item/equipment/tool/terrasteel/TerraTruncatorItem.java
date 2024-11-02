/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.tool.terrasteel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.item.SequentialBreaker;
import vazkii.botania.common.annotations.SoftImplement;
import vazkii.botania.common.item.StoneOfTemperanceItem;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.item.equipment.tool.manasteel.ManasteelAxeItem;

import java.util.*;

public class TerraTruncatorItem extends ManasteelAxeItem implements SequentialBreaker {

	/**
	 * The number of blocks per tick which the Terra Truncator will
	 * collect.
	 */
	private static final int BLOCK_SWAP_RATE = 10;

	/**
	 * The maximum radius (in blocks) which the Terra Truncator will go
	 * in order to try and murder/cut down the tree.
	 */
	public static final int BLOCK_RANGE = 32;

	/**
	 * The maximum number of leaf blocks which the Terra Truncator will chew/go
	 * through once a leaf block is encountered.
	 */
	private static final int LEAF_BLOCK_RANGE = 3;

	/**
	 * The amount of mana required to restore 1 point of damage.
	 */
	private static final int MANA_PER_DAMAGE = 100;

	/**
	 * Represents a map of dimension IDs to a set of all block swappers
	 * active in that dimension.
	 */
	private static final Map<ResourceKey<Level>, Set<BlockSwapper>> blockSwappers = new HashMap<>();

	/**
	 * Toggled during the block swapper ticking to prevent adding more of them during the map iteration.
	 */
	private static boolean tickingSwappers = false;

	public TerraTruncatorItem(Properties props) {
		super(BotaniaAPI.instance().getTerrasteelItemTier(), 5.0F, -3.0F, props);
	}

	public static boolean shouldBreak(Player player) {
		return !player.isShiftKeyDown() && !StoneOfTemperanceItem.hasTemperanceActive(player);
	}

	@SoftImplement("IItemExtension")
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
		BlockHitResult raycast = ToolCommons.raytraceFromEntity(player, 10, false);
		if (raycast.getType() == HitResult.Type.BLOCK) {
			Direction face = raycast.getDirection();
			breakOtherBlock(player, stack, pos, pos, face);
			if (player.isSecondaryUseActive()) {
				BotaniaAPI.instance().breakOnAllCursors(player, stack, pos, face);
			}
		}

		return false;
	}

	@Override
	public int getManaPerDamage() {
		return MANA_PER_DAMAGE;
	}

	@Override
	public void breakOtherBlock(Player player, ItemStack stack, BlockPos pos, BlockPos originPos, Direction side) {
		if (shouldBreak(player) && !tickingSwappers) {
			addBlockSwapper(player.level(), player, stack, pos);
		}
	}

	public static void onTickEnd(ServerLevel world) {
		ResourceKey<Level> dim = world.dimension();
		if (blockSwappers.containsKey(dim)) {
			tickingSwappers = true;
			Set<BlockSwapper> swappers = blockSwappers.get(dim);

			// Iterate through all of our swappers, removing any
			// which no longer need to tick.
			swappers.removeIf(next -> next == null || !next.tick());
			tickingSwappers = false;
		}
	}

	/**
	 * Adds a new block swapper to the provided world as the provided player.
	 * Block swappers are only added on the server, and a marker instance
	 * which is not actually ticked but contains the proper passed in
	 * information will be returned to the client.
	 * 
	 * @param world      The world to add the swapper to.
	 * @param player     The player who is responsible for this swapper.
	 * @param stack      The Terra Truncator which caused this block swapper.
	 * @param origCoords The original coordinates the swapper should start at.
	 */
	private static void addBlockSwapper(Level world, Player player, ItemStack stack, BlockPos origCoords) {
		// Block swapper registration should only occur on the server
		if (world.isClientSide) {
			return;
		}

		BlockSwapper swapper = new BlockSwapper(world, player, stack, origCoords, TerraTruncatorItem.BLOCK_RANGE);

		ResourceKey<Level> dim = world.dimension();
		blockSwappers.computeIfAbsent(dim, d -> new HashSet<>()).add(swapper);
	}

	/**
	 * A block swapper for the Terra Truncator, which uses a standard
	 * Breadth First Search to try and murder/cut down trees.
	 * The Terra Truncator will look up to BLOCK_RANGE blocks to find wood
	 * to cut down (only cutting down adjacent pieces of wood, so it doesn't
	 * jump through the air). However, the truncator will only go through
	 * LEAF_BLOCK_RANGE leave blocks in order to prevent adjacent trees which
	 * are connected only by leaves from being devoured as well.
	 * The leaf restriction is implemented by reducing the number of remaining
	 * steps to the min of LEAF_BLOCK_RANGE and the current range. The restriction
	 * can be removed entirely by setting the "leaves" variable to true, in which
	 * case leaves will be treated normally.
	 */
	private static class BlockSwapper {

		/**
		 * Represents the range which a single block will scan when looking
		 * for the next candidates for swapping. 1 is a good default.
		 */
		public static final int SINGLE_BLOCK_RADIUS = 1;

		private final Level world;
		private final Player player;
		private final ItemStack truncator;

		/**
		 * The priority queue of all possible candidates for swapping.
		 */
		private final PriorityQueue<SwapCandidate> candidateQueue = new PriorityQueue<>();

		/**
		 * The set of already swaps coordinates which do not have
		 * to be revisited.
		 */
		private final Set<BlockPos> completedCoords = new HashSet<>();

		/**
		 * Creates a new block swapper with the provided parameters.
		 * 
		 * @param world      The world the swapper is in.
		 * @param player     The player responsible for creating this swapper.
		 * @param truncator  The Terra Truncator responsible for creating this swapper.
		 * @param origCoords The original coordinates this swapper should start at.
		 * @param range      The range this swapper should swap in.
		 */
		public BlockSwapper(Level world, Player player, ItemStack truncator, BlockPos origCoords, int range) {
			this.world = world;
			this.player = player;
			this.truncator = truncator;

			// Add the origin to our candidate queue with the original range
			candidateQueue.offer(new SwapCandidate(origCoords, range));
		}

		/**
		 * @return True if the block swapper has more blocks to swap, false
		 *         otherwise (implying it can be safely removed).
		 */
		public boolean tick() {
			// If empty, this swapper is done.
			if (candidateQueue.isEmpty()) {
				return false;
			}

			int remainingSwaps = BLOCK_SWAP_RATE;
			while (remainingSwaps > 0 && !candidateQueue.isEmpty()) {
				SwapCandidate cand = candidateQueue.poll();

				// If we've already completed this location, move along, as this
				// is just a suboptimal one.
				if (completedCoords.contains(cand.coordinates)) {
					continue;
				}

				// If this candidate is out of range, discard it.
				if (cand.range <= 0) {
					continue;
				}

				// Otherwise, perform the break and then look at the adjacent tiles.
				ToolCommons.removeBlockWithDrops(player, truncator, world,
						cand.coordinates,
						state -> state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.LEAVES));

				remainingSwaps--;

				completedCoords.add(cand.coordinates);

				// Then, go through all of the adjacent blocks and look if
				// any of them are any good.
				for (BlockPos adj : adjacent(cand.coordinates)) {
					var state = world.getBlockState(adj);

					boolean isWood = state.is(BlockTags.LOGS);
					boolean isLeaf = state.is(BlockTags.LEAVES);
					// TODO: Make "blocks the axe will propagate through" a tag
					boolean shouldPropagateThrough = isWood || isLeaf
							|| state.is(Blocks.MANGROVE_ROOTS);

					if (!shouldPropagateThrough) {
						continue;
					}

					// If this is a leaf, it gets
					// the minimum of the leaf range and the current range - 1.
					// Otherwise, it gets the standard range - 1.
					int newRange = isLeaf ? Math.min(LEAF_BLOCK_RANGE, cand.range - 1) : cand.range - 1;

					candidateQueue.offer(new SwapCandidate(adj, newRange));
				}
			}

			// If we did any iteration, then hang around until next tick.
			return true;
		}

		public List<BlockPos> adjacent(BlockPos original) {
			List<BlockPos> coords = new ArrayList<>();
			// Visit all the surrounding blocks in the provided radius.
			// Gotta love these nested loops, right?
			for (int dx = -SINGLE_BLOCK_RADIUS; dx <= SINGLE_BLOCK_RADIUS; dx++) {
				for (int dy = -SINGLE_BLOCK_RADIUS; dy <= SINGLE_BLOCK_RADIUS; dy++) {
					for (int dz = -SINGLE_BLOCK_RADIUS; dz <= SINGLE_BLOCK_RADIUS; dz++) {
						// Skip the central tile.
						if (dx == 0 && dy == 0 && dz == 0) {
							continue;
						}

						coords.add(original.offset(dx, dy, dz));
					}
				}
			}

			return coords;
		}

		/**
		 * Represents a potential candidate for swapping/removal. Sorted by
		 * range (where a larger range is more preferable). As we're using
		 * a priority queue, which is a min-heap internally, larger ranges
		 * are considered "smaller" than smaller ranges (so they show up in the
		 * min-heap first).
		 *
		 * @param coordinates The coordinates of this candidate.
		 * @param range       The remaining range of this candidate.
		 */
		public record SwapCandidate(BlockPos coordinates, int range) implements Comparable<SwapCandidate> {
			@Override
			public int compareTo(@NotNull SwapCandidate other) {
				// Aka, a bigger range implies a smaller value, meaning
				// bigger ranges will be preferred in a min-heap
				return other.range - range;
			}
		}
	}

}
