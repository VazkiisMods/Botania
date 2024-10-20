/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.relic;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.block.Bound;
import vazkii.botania.api.item.Relic;
import vazkii.botania.api.item.SequentialBreaker;
import vazkii.botania.api.item.WireframeCoordinateListProvider;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.advancements.LokiPlaceTrigger;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

import java.util.ArrayList;
import java.util.List;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class RingOfLokiItem extends RelicBaubleItem implements WireframeCoordinateListProvider {

	/**
	 * This limit exists to prevent players from accidentally NBT-banning themselves from a world or server.
	 * TODO 1.21: It might be possible to increase this if the storage tag structure is optimized.
	 */
	private static final int MAX_NUM_CURSORS = 1023;
	private static final String TAG_CURSOR_LIST = "cursorList";
	private static final String TAG_CURSOR_PREFIX = "cursor";
	private static final String TAG_CURSOR_COUNT = "cursorCount";
	private static final String TAG_X_OFFSET = "xOffset";
	private static final String TAG_Y_OFFSET = "yOffset";
	private static final String TAG_Z_OFFSET = "zOffset";
	private static final String TAG_X_ORIGIN = "xOrigin";
	private static final String TAG_Y_ORIGIN = "yOrigin";
	private static final String TAG_Z_ORIGIN = "zOrigin";

	private static boolean recCall = false;

	public RingOfLokiItem(Properties props) {
		super(props);
	}

	public static InteractionResult onPlayerInteract(Player player, Level world, InteractionHand hand, BlockHitResult lookPos) {
		ItemStack lokiRing = getLokiRing(player);
		if (lokiRing.isEmpty() || !player.isShiftKeyDown()) {
			return InteractionResult.PASS;
		}

		ItemStack stack = player.getItemInHand(hand);
		List<BlockPos> cursors = getCursorList(lokiRing);

		if (lookPos.getType() != HitResult.Type.BLOCK) {
			return InteractionResult.PASS;
		}

		BlockPos hit = lookPos.getBlockPos();
		if (stack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
			BlockPos originCoords = getBindingCenter(lokiRing);
			if (!world.isClientSide) {
				if (originCoords.getY() == Integer.MIN_VALUE) {
					// Initiate a new pending list of positions
					setBindingCenter(lokiRing, hit);
					setCursorList(lokiRing, null);
				} else {
					if (originCoords.equals(hit)) {
						// Finalize the pending list of positions
						exitBindingMode(lokiRing);
					} else {
						// Toggle offsets on or off from the pending list of positions
						BlockPos relPos = hit.subtract(originCoords);

						boolean removed = cursors.remove(relPos);
						if (!removed) {
							if (cursors.size() < MAX_NUM_CURSORS) {
								cursors.add(relPos);
							} else {
								player.displayClientMessage(Component.translatable("botaniamisc.lokiRingLimitReached"), true);
							}
						}
						setCursorList(lokiRing, cursors);
					}
				}
			}

			return InteractionResult.SUCCESS;
		} else {
			int numCursors = cursors.size();
			// particularly large cursor counts can overflow after exponentiation
			int cost = numCursors > 10 ? numCursors : Math.min(numCursors, (int) Math.pow(Math.E, numCursors * 0.25));
			ItemStack original = stack.copy();
			int successes = 0;
			for (BlockPos cursor : cursors) {
				BlockPos pos = hit.offset(cursor);
				if (ManaItemHandler.instance().requestManaExact(lokiRing, player, cost, false)) {
					UseOnContext ctx = getUseOnContext(player, hand, pos, lookPos.getLocation(), lookPos.getDirection());

					InteractionResult result;
					if (player.isCreative()) {
						result = PlayerHelper.substituteUse(ctx, original.copy());
					} else {
						result = stack.useOn(ctx);
					}

					if (result.consumesAction()) {
						ManaItemHandler.instance().requestManaExact(lokiRing, player, cost, true);
						successes++;
					}
				} else {
					break;
				}
			}
			if (successes > 0 && player instanceof ServerPlayer serverPlayer) {
				LokiPlaceTrigger.INSTANCE.trigger(serverPlayer, lokiRing, successes);
			}
			return successes > 0 ? InteractionResult.SUCCESS : InteractionResult.PASS;
		}
	}

	@NotNull
	public static UseOnContext getUseOnContext(Player player, InteractionHand hand, BlockPos pos, Vec3 lookHit, Direction direction) {
		Vec3 newHitVec = new Vec3(pos.getX() + Mth.frac(lookHit.x()), pos.getY() + Mth.frac(lookHit.y()), pos.getZ() + Mth.frac(lookHit.z()));
		BlockHitResult newHit = new BlockHitResult(newHitVec, direction, pos, false);
		return new UseOnContext(player, hand, newHit);
	}

	public static void breakOnAllCursors(Player player, ItemStack stack, BlockPos pos, Direction side) {
		Item item = stack.getItem();
		ItemStack lokiRing = getLokiRing(player);
		if (lokiRing.isEmpty() || player.level().isClientSide || !(item instanceof SequentialBreaker breaker)) {
			return;
		}

		if (recCall) {
			return;
		}
		recCall = true;

		List<BlockPos> cursors = getCursorList(lokiRing);

		try {
			for (BlockPos offset : cursors) {
				BlockPos coords = pos.offset(offset);
				BlockState state = player.level().getBlockState(coords);
				breaker.breakOtherBlock(player, stack, coords, pos, side);
				ToolCommons.removeBlockWithDrops(player, stack, player.level(), coords,
						s -> s.is(state.getBlock()));
			}
		} finally {
			recCall = false;
		}
	}

	@Override
	public void onUnequipped(ItemStack stack, LivingEntity living) {
		setCursorList(stack, null);
	}

	// onUnequipped has itemstack identity issues and doesn't actually fully work, so do this here every tick.
	// This prevents a player from accidentally entering binding mode, then forgetting where the binding center
	// is and thus being unable to exit binding mode.
	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean held) {
		super.inventoryTick(stack, world, entity, slot, held);
		// Curios actually calls this method, but with a negative slot, so we can check if we're in the "real" inventory this way
		if (slot >= 0) {
			exitBindingMode(stack);
		}
	}

	@Override
	public List<BlockPos> getWireframesToDraw(Player player, ItemStack stack) {
		if (getLokiRing(player) != stack) {
			return ImmutableList.of();
		}

		HitResult lookPos = Minecraft.getInstance().hitResult;

		if (lookPos != null
				&& lookPos.getType() == HitResult.Type.BLOCK
				&& !player.level().isEmptyBlock(((BlockHitResult) lookPos).getBlockPos())) {
			List<BlockPos> list = getCursorList(stack);
			BlockPos origin = getBindingCenter(stack);

			for (int i = 0; i < list.size(); i++) {
				if (origin.getY() != Integer.MIN_VALUE) {
					list.set(i, list.get(i).offset(origin));
				} else {
					list.set(i, list.get(i).offset(((BlockHitResult) lookPos).getBlockPos()));
				}
			}

			return list;
		}

		return ImmutableList.of();
	}

	@Override
	public BlockPos getSourceWireframe(Player player, ItemStack stack) {
		Minecraft mc = Minecraft.getInstance();
		if (getLokiRing(player) == stack) {
			BlockPos currentBuildCenter = getBindingCenter(stack);
			if (currentBuildCenter.getY() != Integer.MIN_VALUE) {
				return currentBuildCenter;
			} else if (mc.hitResult instanceof BlockHitResult hitRes
					&& mc.hitResult.getType() == HitResult.Type.BLOCK
					&& !getCursorList(stack).isEmpty()) {
				return hitRes.getBlockPos();
			}
		}

		return null;
	}

	private static ItemStack getLokiRing(Player player) {
		return EquipmentHandler.findOrEmpty(BotaniaItems.lokiRing, player);
	}

	private static BlockPos getBindingCenter(ItemStack stack) {
		int x = ItemNBTHelper.getInt(stack, TAG_X_ORIGIN, 0);
		int y = ItemNBTHelper.getInt(stack, TAG_Y_ORIGIN, Integer.MIN_VALUE);
		int z = ItemNBTHelper.getInt(stack, TAG_Z_ORIGIN, 0);
		return new BlockPos(x, y, z);
	}

	private static void exitBindingMode(ItemStack stack) {
		setBindingCenter(stack, Bound.UNBOUND_POS);
	}

	private static void setBindingCenter(ItemStack stack, BlockPos pos) {
		ItemNBTHelper.setInt(stack, TAG_X_ORIGIN, pos.getX());
		ItemNBTHelper.setInt(stack, TAG_Y_ORIGIN, pos.getY());
		ItemNBTHelper.setInt(stack, TAG_Z_ORIGIN, pos.getZ());
	}

	private static List<BlockPos> getCursorList(ItemStack stack) {
		CompoundTag cmp = ItemNBTHelper.getCompound(stack, TAG_CURSOR_LIST, false);
		List<BlockPos> cursors = new ArrayList<>();

		int count = cmp.getInt(TAG_CURSOR_COUNT);
		for (int i = 0; i < count; i++) {
			CompoundTag cursorCmp = cmp.getCompound(TAG_CURSOR_PREFIX + i);
			int x = cursorCmp.getInt(TAG_X_OFFSET);
			int y = cursorCmp.getInt(TAG_Y_OFFSET);
			int z = cursorCmp.getInt(TAG_Z_OFFSET);
			cursors.add(new BlockPos(x, y, z));
		}

		return cursors;
	}

	private static void setCursorList(ItemStack stack, @Nullable List<BlockPos> cursors) {
		CompoundTag cmp = new CompoundTag();
		if (cursors != null) {
			int i = 0;
			for (BlockPos cursor : cursors) {
				CompoundTag cursorCmp = cursorToCmp(cursor);
				cmp.put(TAG_CURSOR_PREFIX + i, cursorCmp);
				i++;
			}
			cmp.putInt(TAG_CURSOR_COUNT, i);
		}

		ItemNBTHelper.setCompound(stack, TAG_CURSOR_LIST, cmp);
	}

	private static CompoundTag cursorToCmp(BlockPos pos) {
		CompoundTag cmp = new CompoundTag();
		cmp.putInt(TAG_X_OFFSET, pos.getX());
		cmp.putInt(TAG_Y_OFFSET, pos.getY());
		cmp.putInt(TAG_Z_OFFSET, pos.getZ());
		return cmp;
	}

	public static Relic makeRelic(ItemStack stack) {
		return new RelicImpl(stack, botaniaRL("challenge/loki_ring"));
	}

}
