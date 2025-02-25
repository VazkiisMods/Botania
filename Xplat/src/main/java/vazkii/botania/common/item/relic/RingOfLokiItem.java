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
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
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
import org.jetbrains.annotations.Unmodifiable;

import vazkii.botania.api.item.Relic;
import vazkii.botania.api.item.SequentialBreaker;
import vazkii.botania.api.item.WireframeCoordinateListProvider;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.advancements.LokiPlaceTrigger;
import vazkii.botania.common.component.BotaniaDataComponents;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.helper.DataComponentHelper;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class RingOfLokiItem extends RelicBaubleItem implements WireframeCoordinateListProvider {

	/**
	 * This limit exists to prevent players from accidentally NBT-banning themselves from a world or server.
	 * TODO 1.21: It might be possible to increase this if the storage tag structure is optimized.
	 */
	public static final int MAX_NUM_CURSORS = 1023;

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
		List<BlockPos> cursors = new ArrayList<>(getCursorList(lokiRing));

		if (lookPos.getType() != HitResult.Type.BLOCK) {
			return InteractionResult.PASS;
		}

		BlockPos hit = lookPos.getBlockPos();
		if (stack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
			GlobalPos originCoords = getBindingCenter(lokiRing);
			if (!world.isClientSide) {
				if (originCoords == null || originCoords.dimension() != world.dimension()) {
					// Initiate a new pending list of positions
					setBindingCenter(lokiRing, GlobalPos.of(world.dimension(), hit));
					setCursorList(lokiRing, null);
				} else {
					if (originCoords.pos().equals(hit)) {
						// Finalize the pending list of positions
						exitBindingMode(lokiRing);
					} else {
						// Toggle offsets on or off from the pending list of positions
						BlockPos relPos = hit.subtract(originCoords.pos());

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
			GlobalPos origin = getBindingCenter(stack);
			if (origin != null && origin.dimension() != player.level().dimension()) {
				// binding mode for different dimension
				return Collections.emptyList();
			}

			List<BlockPos> list = getCursorList(stack);
			List<BlockPos> result = new ArrayList<>(list.size());
			Vec3i offset = origin != null ? origin.pos() : ((BlockHitResult) lookPos).getBlockPos();
			for (BlockPos cursor : list) {
				result.add(cursor.offset(offset));
			}

			return result;
		}

		return ImmutableList.of();
	}

	@Nullable
	@Override
	public BlockPos getSourceWireframe(Player player, ItemStack stack) {
		Minecraft mc = Minecraft.getInstance();
		if (getLokiRing(player) == stack) {
			GlobalPos currentBuildCenter = getBindingCenter(stack);
			if (currentBuildCenter != null && currentBuildCenter.dimension() == player.level().dimension()) {
				return currentBuildCenter.pos();
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

	@Nullable
	private static GlobalPos getBindingCenter(ItemStack stack) {
		return stack.get(BotaniaDataComponents.BINDING_POS);
	}

	private static void exitBindingMode(ItemStack stack) {
		stack.remove(BotaniaDataComponents.BINDING_POS);
	}

	private static void setBindingCenter(ItemStack stack, GlobalPos pos) {
		stack.set(BotaniaDataComponents.BINDING_POS, pos);
	}

	@Unmodifiable
	private static List<BlockPos> getCursorList(ItemStack stack) {
		return stack.getOrDefault(BotaniaDataComponents.LOKI_RING_OFFSET_LIST, Collections.emptyList());
	}

	private static void setCursorList(ItemStack stack, @Nullable List<BlockPos> cursors) {
		DataComponentHelper.setNonEmpty(stack, BotaniaDataComponents.LOKI_RING_OFFSET_LIST, cursors);
	}

	public static Relic makeRelic(ItemStack stack) {
		return new RelicImpl(stack, botaniaRL("challenge/loki_ring"));
	}

}
