/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.rod;

import com.google.common.collect.ImmutableList;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.item.BlockProvider;
import vazkii.botania.api.item.WireframeCoordinateListProvider;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.gui.ItemsRemainingRenderHandler;
import vazkii.botania.common.CollectingNeighborUpdaterAccess;
import vazkii.botania.common.block.PlatformBlock;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.item.StoneOfTemperanceItem;
import vazkii.botania.mixin.LevelAccessor;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.ArrayList;
import java.util.List;

public class ShiftingCrustRodItem extends Item implements WireframeCoordinateListProvider {

	private static final int RANGE = 3;
	private static final int COST = 40;

	private static final String TAG_REPLACEMENT_ITEM = "placedItem";
	private static final String TAG_TARGET_BLOCK_NAME = "targetBlock";
	private static final String TAG_SWAPPING = "swapping";
	private static final String TAG_SELECT_X = "selectX";
	private static final String TAG_SELECT_Y = "selectY";
	private static final String TAG_SELECT_Z = "selectZ";
	private static final String TAG_EXTRA_RANGE = "extraRange";
	private static final String TAG_SWAP_HIT_VEC = "swapHitVec";
	private static final String TAG_SWAP_DIRECTION = "swapDirection";
	private static final String TAG_SWAP_CLICKED_AXIS = "swapClickAxis";
	private static final String TAG_TEMPERANCE_STONE = "temperanceStone";

	public ShiftingCrustRodItem(Properties props) {
		super(props);
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Player player = ctx.getPlayer();
		ItemStack stack = ctx.getItemInHand();
		BlockState wstate = world.getBlockState(pos);
		Block block = wstate.getBlock();

		if (player != null && player.isSecondaryUseActive()) {
			BlockEntity tile = world.getBlockEntity(pos);
			if (tile == null && block.asItem() != Items.AIR && PlatformBlock.isValidBlock(wstate, world, pos)
					&& (wstate.canOcclude() || block instanceof TransparentBlock || block instanceof IronBarsBlock)
					&& block.asItem() instanceof BlockItem) {
				setItemToPlace(stack, block.asItem());
				setSwapTemplateDirection(stack, ctx.getClickedFace());
				setHitPos(stack, ctx.getClickLocation());

				displayRemainderCounter(player, stack);
				return InteractionResult.sidedSuccess(world.isClientSide());
			}
		} else if (canExchange(stack) && !ItemNBTHelper.getBoolean(stack, TAG_SWAPPING, false)) {
			Item replacement = getItemToPlace(stack);
			List<BlockPos> swap = getTargetPositions(world, stack, replacement, pos, block, ctx.getClickedFace());
			if (swap.size() > 0) {
				ItemNBTHelper.setBoolean(stack, TAG_SWAPPING, true);
				ItemNBTHelper.setInt(stack, TAG_SELECT_X, pos.getX());
				ItemNBTHelper.setInt(stack, TAG_SELECT_Y, pos.getY());
				ItemNBTHelper.setInt(stack, TAG_SELECT_Z, pos.getZ());
				setSwapClickDirection(stack, ctx.getClickedFace());
				setTarget(stack, block);
			}
		}

		return InteractionResult.sidedSuccess(world.isClientSide());
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player player) {
		return !player.isCreative();
	}

	public InteractionResult onLeftClick(Player player, Level world, InteractionHand hand, BlockPos pos, Direction side) {
		if (player.isSpectator()) {
			return InteractionResult.PASS;
		}

		ItemStack stack = player.getItemInHand(hand);
		if (!stack.isEmpty() && stack.is(this)) {
			// Skip logic on the client, the server will replace the block when it receives the action packet
			if (world.isClientSide()) {
				return InteractionResult.SUCCESS;
			}

			if (canExchange(stack) && ManaItemHandler.instance().requestManaExactForTool(stack, player, COST, false)) {
				int exchange = exchange(world, player, pos, stack, getItemToPlace(stack));
				if (exchange > 0) {
					ManaItemHandler.instance().requestManaExactForTool(stack, player, exchange, true);
				}
			}
			// Always return SUCCESS with rod in hand to prevent any vanilla block breaking
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;

	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean equipped) {
		if (!canExchange(stack) || !(entity instanceof Player player)) {
			return;
		}

		int extraRange = ItemNBTHelper.getInt(stack, TAG_EXTRA_RANGE, 1);
		int extraRangeNew = ManaItemHandler.instance().hasProficiency(player, stack) ? 3 : 1;
		if (extraRange != extraRangeNew) {
			ItemNBTHelper.setInt(stack, TAG_EXTRA_RANGE, extraRangeNew);
		}
		boolean temperanceActive = StoneOfTemperanceItem.hasTemperanceActive(player);
		if (temperanceActive != stack.getOrCreateTag().getBoolean(TAG_TEMPERANCE_STONE)) {
			stack.getOrCreateTag().putBoolean(TAG_TEMPERANCE_STONE, temperanceActive);
		}

		Item replacement = getItemToPlace(stack);
		if (ItemNBTHelper.getBoolean(stack, TAG_SWAPPING, false)) {
			if (!ManaItemHandler.instance().requestManaExactForTool(stack, player, COST, false)) {
				endSwapping(stack);
				return;
			}

			int x = ItemNBTHelper.getInt(stack, TAG_SELECT_X, 0);
			int y = ItemNBTHelper.getInt(stack, TAG_SELECT_Y, 0);
			int z = ItemNBTHelper.getInt(stack, TAG_SELECT_Z, 0);
			Block target = getTargetState(stack);
			List<BlockPos> swap = getTargetPositions(world, stack, replacement, new BlockPos(x, y, z), target, getSwapClickDirection(stack));
			if (swap.size() == 0) {
				endSwapping(stack);
				return;
			}

			BlockPos coords = swap.get(world.random.nextInt(swap.size()));
			int exchange = exchange(world, player, coords, stack, replacement);
			if (exchange > 0) {
				ManaItemHandler.instance().requestManaForTool(stack, player, exchange, true);
			} else {
				endSwapping(stack);
			}
		}
	}

	public List<BlockPos> getTargetPositions(Level world, ItemStack stack, Item toPlace, BlockPos pos, Block toReplace, Direction clickedSide) {
		// Our result list
		List<BlockPos> coordsList = new ArrayList<>();

		// We subtract 1 from the effective range as the center tile is included
		// So, with a range of 3, we are visiting tiles at -2, -1, 0, 1, 2
		// If we have the stone of temperance, on one axis we only visit 0.
		Direction.Axis axis = clickedSide.getAxis();
		int xRange = getRange(stack, axis, Direction.Axis.X);
		int yRange = getRange(stack, axis, Direction.Axis.Y);
		int zRange = getRange(stack, axis, Direction.Axis.Z);

		// Iterate in all 3 dimensions through our possible positions.
		for (int offsetX = -xRange; offsetX <= xRange; offsetX++) {
			for (int offsetY = -yRange; offsetY <= yRange; offsetY++) {
				for (int offsetZ = -zRange; offsetZ <= zRange; offsetZ++) {
					BlockPos pos_ = pos.offset(offsetX, offsetY, offsetZ);

					BlockState currentState = world.getBlockState(pos_);

					// If this block is not our target, ignore it, as we don't need
					// to consider replacing it
					if (!currentState.is(toReplace)) {
						continue;
					}

					// If this block is already the block we're swapping to,
					// we don't need to swap again
					if (currentState.getBlock().asItem() == toPlace) {
						continue;
					}

					// Check to see if the block is visible on any side:
					for (Direction dir : Direction.values()) {
						BlockPos adjPos = pos_.relative(dir);
						BlockState adjState = world.getBlockState(adjPos);

						if (!Block.isFaceFull(adjState.getBlockSupportShape(world, pos), dir.getOpposite())) {
							coordsList.add(pos_);
							break;
						}
					}
				}
			}
		}

		return coordsList;
	}

	public int exchange(Level world, Player player, BlockPos pos, ItemStack rod, Item replacement) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile != null) {
			return 0;
		}

		ItemStack placeStack = removeFromInventory(player, rod, replacement, false);
		if (!placeStack.isEmpty()) {
			BlockState stateAt = world.getBlockState(pos);
			if (!stateAt.isAir() && stateAt.getDestroyProgress(player, world, pos) > 0
					&& stateAt.getBlock().asItem() != replacement) {
				float hardness = stateAt.getDestroySpeed(world, pos);
				if (!world.isClientSide) {
					final NeighborUpdater neighborUpdater = ((LevelAccessor) world).getNeighborUpdater();
					try {
						if (neighborUpdater instanceof CollectingNeighborUpdaterAccess access) {
							access.botania$pauseUpdates();
						}
						world.destroyBlock(pos, !player.getAbilities().instabuild, player);
						BlockHitResult hit = new BlockHitResult(getHitPos(rod, pos), getSwapTemplateDirection(rod), pos, false);
						InteractionResult result = PlayerHelper.substituteUse(new UseOnContext(player, InteractionHand.MAIN_HAND, hit), placeStack);
						// TODO: provide an use context that overrides player facing direction/yaw?
						//  currently it pulls from the player directly

						if (!player.getAbilities().instabuild) {
							if (result.consumesAction()) {
								removeFromInventory(player, rod, replacement, true);
								displayRemainderCounter(player, rod);
							} else {
								((ServerLevel) world).sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
										2, 0.1, 0.1, 0.1, 0);
							}
						}
					} finally {
						if (neighborUpdater instanceof CollectingNeighborUpdaterAccess access) {
							access.botania$resumeUpdates();
						}
					}
				}
				return hardness <= 10 ? COST : (int) (0.5 * COST + 3 * hardness);
			}
		}

		return 0;
	}

	public boolean canExchange(ItemStack stack) {
		return getItemToPlace(stack) != Items.AIR;
	}

	public static ItemStack removeFromInventory(Player player, Container inv, ItemStack tool, Item requested, boolean doit) {
		List<BlockProvider> providers = new ArrayList<>();
		for (int i = inv.getContainerSize() - 1; i >= 0; i--) {
			ItemStack invStack = inv.getItem(i);
			if (invStack.isEmpty()) {
				continue;
			}

			Item item = invStack.getItem();
			if (item == requested) {
				ItemStack ret;
				if (doit) {
					ret = inv.removeItem(i, 1);
				} else {
					ret = invStack.copyWithCount(1);
				}
				return ret;
			}

			var provider = XplatAbstractions.INSTANCE.findBlockProvider(invStack);
			if (provider != null) {
				providers.add(provider);
			}
		}

		if (requested instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			for (BlockProvider prov : providers) {
				if (prov.provideBlock(player, tool, block, doit)) {
					return new ItemStack(requested);
				}
			}
		}

		return ItemStack.EMPTY;
	}

	public static ItemStack removeFromInventory(Player player, ItemStack tool, Item item, boolean doit) {
		if (player.getAbilities().instabuild) {
			return new ItemStack(item);
		}

		ItemStack outStack = removeFromInventory(player, BotaniaAPI.instance().getAccessoriesInventory(player), tool, item, doit);
		if (outStack.isEmpty()) {
			outStack = removeFromInventory(player, player.getInventory(), tool, item, doit);
		}
		return outStack;
	}

	public static int getInventoryItemCount(Player player, ItemStack stack, Item item) {
		if (player.getAbilities().instabuild) {
			return -1;
		}

		int baubleCount = getInventoryItemCount(player, BotaniaAPI.instance().getAccessoriesInventory(player), stack, item);
		if (baubleCount == -1) {
			return -1;
		}

		int count = getInventoryItemCount(player, player.getInventory(), stack, item);
		if (count == -1) {
			return -1;
		}

		return count + baubleCount;
	}

	public static int getInventoryItemCount(Player player, Container inv, ItemStack stack, Item requested) {
		if (player.getAbilities().instabuild) {
			return -1;
		}

		int count = 0;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack invStack = inv.getItem(i);
			if (invStack.isEmpty()) {
				continue;
			}

			Item item = invStack.getItem();
			if (item == requested.asItem()) {
				count += invStack.getCount();
			}

			var prov = XplatAbstractions.INSTANCE.findBlockProvider(invStack);
			if (prov != null && requested instanceof BlockItem blockItem) {
				int provCount = prov.getBlockCount(player, stack, blockItem.getBlock());
				if (provCount == -1) {
					return -1;
				}
				count += provCount;
			}
		}

		return count;
	}

	public void displayRemainderCounter(Player player, ItemStack stack) {
		if (!player.level().isClientSide) {
			Item item = getItemToPlace(stack);
			int count = getInventoryItemCount(player, stack, item);
			ItemsRemainingRenderHandler.send(player, new ItemStack(item), count);
		}
	}

	private void setItemToPlace(ItemStack stack, Item item) {
		ItemNBTHelper.setString(stack, TAG_REPLACEMENT_ITEM, BuiltInRegistries.ITEM.getKey(item).toString());
	}

	private Item getItemToPlace(ItemStack stack) {
		return BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(ItemNBTHelper.getString(stack, TAG_REPLACEMENT_ITEM, "air")));
	}

	private void setHitPos(ItemStack stack, Vec3 vec) {
		ListTag list = new ListTag();
		list.add(DoubleTag.valueOf(Mth.frac(vec.x())));
		list.add(DoubleTag.valueOf(Mth.frac(vec.y())));
		list.add(DoubleTag.valueOf(Mth.frac(vec.z())));
		stack.getOrCreateTag().put(TAG_SWAP_HIT_VEC, list);
	}

	private Vec3 getHitPos(ItemStack stack, BlockPos pos) {
		ListTag list = stack.getOrCreateTag().getList(TAG_SWAP_HIT_VEC, Tag.TAG_DOUBLE);
		return new Vec3(pos.getX() + list.getDouble(0),
				pos.getY() + list.getDouble(1),
				pos.getZ() + list.getDouble(2));
	}

	private void setSwapTemplateDirection(ItemStack stack, Direction direction) {
		stack.getOrCreateTag().putInt(TAG_SWAP_DIRECTION, direction.get3DDataValue());
	}

	private Direction getSwapTemplateDirection(ItemStack stack) {
		return Direction.from3DDataValue(stack.getOrCreateTag().getInt(TAG_SWAP_DIRECTION));
	}

	private void setSwapClickDirection(ItemStack stack, Direction direction) {
		stack.getOrCreateTag().putInt(TAG_SWAP_CLICKED_AXIS, direction.get3DDataValue());
	}

	private Direction getSwapClickDirection(ItemStack stack) {
		return Direction.from3DDataValue(stack.getOrCreateTag().getInt(TAG_SWAP_CLICKED_AXIS));
	}

	private int getRange(ItemStack stack, Direction.Axis clickAxis, Direction.Axis rangeAxis) {
		if (stack.getOrCreateTag().getBoolean(TAG_TEMPERANCE_STONE) && rangeAxis == clickAxis) {
			return 0;
		}
		return RANGE + ItemNBTHelper.getInt(stack, TAG_EXTRA_RANGE, 1) - 1;
	}

	private static void endSwapping(ItemStack stack) {
		ItemNBTHelper.setBoolean(stack, TAG_SWAPPING, false);
		ItemNBTHelper.removeEntry(stack, TAG_SELECT_X);
		ItemNBTHelper.removeEntry(stack, TAG_SELECT_Y);
		ItemNBTHelper.removeEntry(stack, TAG_SELECT_Z);
		ItemNBTHelper.removeEntry(stack, TAG_TARGET_BLOCK_NAME);
		ItemNBTHelper.removeEntry(stack, TAG_SWAP_CLICKED_AXIS);
	}

	@NotNull
	@Override
	public Component getName(@NotNull ItemStack stack) {
		Item item = getItemToPlace(stack);
		MutableComponent cmp = super.getName(stack).copy();
		if (item != Items.AIR) {
			cmp.append(" (");
			Component sub = new ItemStack(item).getHoverName();
			cmp.append(sub.copy().withStyle(ChatFormatting.GREEN));
			cmp.append(")");
		}
		return cmp;
	}

	private void setTarget(ItemStack stack, Block block) {
		ItemNBTHelper.setString(stack, TAG_TARGET_BLOCK_NAME, BuiltInRegistries.BLOCK.getKey(block).toString());
	}

	public static Block getTargetState(ItemStack stack) {
		ResourceLocation id = new ResourceLocation(ItemNBTHelper.getString(stack, TAG_TARGET_BLOCK_NAME, "minecraft:air"));
		return BuiltInRegistries.BLOCK.get(id);
	}

	@Override
	public List<BlockPos> getWireframesToDraw(Player player, ItemStack stack) {
		ItemStack holding = player.getMainHandItem();
		if (holding != stack || !canExchange(stack)) {
			return ImmutableList.of();
		}

		HitResult pos = Minecraft.getInstance().hitResult;
		if (pos != null && pos.getType() == HitResult.Type.BLOCK) {
			BlockPos bPos = ((BlockHitResult) pos).getBlockPos();
			Block target = Minecraft.getInstance().level.getBlockState(bPos).getBlock();
			if (ItemNBTHelper.getBoolean(stack, TAG_SWAPPING, false)) {
				bPos = new BlockPos(
						ItemNBTHelper.getInt(stack, TAG_SELECT_X, 0),
						ItemNBTHelper.getInt(stack, TAG_SELECT_Y, 0),
						ItemNBTHelper.getInt(stack, TAG_SELECT_Z, 0)
				);
				target = getTargetState(stack);
			}

			if (!player.level().isEmptyBlock(bPos)) {
				Item item = getItemToPlace(stack);
				List<BlockPos> coordsList = getTargetPositions(player.level(), stack, item, bPos, target, ((BlockHitResult) pos).getDirection());
				coordsList.removeIf(bPos::equals);
				return coordsList;
			}

		}
		return ImmutableList.of();
	}

}
