/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.flower.functional;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.block.Wandable;
import vazkii.botania.api.block_entity.FunctionalFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.api.item.FlowerPlaceable;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.helper.DelayHelper;
import vazkii.botania.common.helper.EntityHelper;
import vazkii.botania.xplat.BotaniaConfig;

import java.util.*;

public class RannuncarpusBlockEntity extends FunctionalFlowerBlockEntity implements Wandable {
	private static final int PICKUP_RANGE = 2;
	private static final int PICKUP_RANGE_Y = 3;
	private static final int RANGE_PLACE_MANA = 8;
	private static final int RANGE_PLACE = 6;
	private static final int RANGE_PLACE_Y = 6;

	private static final int RANGE_PLACE_MANA_MINI = 3;
	private static final int RANGE_PLACE_MINI = 2;
	private static final int RANGE_PLACE_Y_MINI = 2;
	private static final String TAG_STATE_SENSITIVE = "stateSensitive";
	public static final int PLACE_INTERVAL_TICKS = 10;
	private boolean stateSensitive = false;

	protected RannuncarpusBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public RannuncarpusBlockEntity(BlockPos pos, BlockState state) {
		this(BotaniaFlowerBlocks.RANNUNCARPUS, pos, state);
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		if (getLevel().isClientSide || redstoneSignal > 0) {
			return;
		}

		if (ticksExisted % PLACE_INTERVAL_TICKS == 0) {
			List<ItemEntity> items = getLevel().getEntitiesOfClass(ItemEntity.class, AABB.encapsulatingFullBlocks(getBlockPos().offset(-PICKUP_RANGE, -PICKUP_RANGE_Y, -PICKUP_RANGE), getBlockPos().offset(PICKUP_RANGE, PICKUP_RANGE_Y, PICKUP_RANGE)));

			List<ItemStack> filter = HopperhockBlockEntity.getFilterForInventory(getLevel(), getFilterPos(), false);

			for (ItemEntity item : items) {
				if (!DelayHelper.canInteractWith(this, item)) {
					continue;
				}

				ItemStack stack = item.getItem();
				if (!HopperhockBlockEntity.canAcceptItem(stack, filter, 0)) {
					continue;
				}

				Item stackItem = stack.getItem();
				ResourceLocation id = BuiltInRegistries.ITEM.getKey(stackItem);
				if (BotaniaConfig.common().rannuncarpusModBlacklist().contains(id.getNamespace())
						|| BotaniaConfig.common().rannuncarpusItemBlacklist().contains(id.toString())) {
					continue;
				}

				if (stackItem instanceof BlockItem || stackItem instanceof FlowerPlaceable) {
					BlockPos coords = getCandidatePosition(getLevel().random, stack);
					if (coords == null) {
						continue;
					}
					BlockPlaceContext ctx = getBlockPlaceContext(stack, coords);

					boolean success = false;
					if (stackItem instanceof FlowerPlaceable flowerPlaceable) {
						success = flowerPlaceable.tryPlace(this, ctx);
					}
					if (!success && stackItem instanceof BlockItem blockItem) {
						success = blockItem.place(ctx).consumesAction();
					}

					if (success) {
						if (BotaniaConfig.common().blockBreakParticles()) {
							BlockState state = getLevel().getBlockState(ctx.getClickedPos());
							getLevel().levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, coords, Block.getId(state));
						}
						if (getMana() > 1) {
							addMana(-1);
						}
						EntityHelper.syncItem(item);
						return;
					}
				}
			}
		}
	}

	@NotNull
	private BlockPlaceContext getBlockPlaceContext(ItemStack stack, BlockPos coords) {
		BlockHitResult ray = new BlockHitResult(new Vec3(coords.getX() + 0.5, coords.getY() + 1, coords.getZ() + 0.5), Direction.UP,
				coords, false);
		return new RannuncarpusPlaceContext(getLevel(), stack, ray, worldPosition);
	}

	private BlockPos getFilterPos() {
		return getBlockPos().below(isFloating() ? 1 : 2);
	}

	public BlockState getUnderlyingBlock() {
		return getLevel().getBlockState(getFilterPos());
	}

	@Nullable
	private BlockPos getCandidatePosition(RandomSource rand, ItemStack stack) {
		int rangePlace = getPlaceRange();
		int rangePlaceY = getVerticalPlaceRange();
		BlockPos center = getEffectivePos();
		BlockState filter = getUnderlyingBlock();
		List<BlockPos> emptyPositions = new ArrayList<>();
		List<BlockPos> additivePositions = new ArrayList<>();

		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-rangePlace, -rangePlaceY, -rangePlace),
				center.offset(rangePlace, rangePlaceY, rangePlace))) {
			BlockState state = getLevel().getBlockState(pos);
			BlockPos placementPos = pos.above();
			BlockState up = getLevel().getBlockState(placementPos);

			boolean matches;
			if (stateSensitive) {
				matches = state == filter;
			} else {
				matches = state.is(filter.getBlock());
			}

			if (matches) {
				if (isAirOrDifferentReplaceableBlock(up, stack)) {
					emptyPositions.add(pos.immutable());
				} else if (up.canBeReplaced(getBlockPlaceContext(stack, placementPos))) {
					// same block type, but can still place more
					additivePositions.add(pos.immutable());
				}
			}
		}

		return !emptyPositions.isEmpty() ? emptyPositions.get(rand.nextInt(emptyPositions.size()))
				: !additivePositions.isEmpty() ? additivePositions.get(rand.nextInt(additivePositions.size())) : null;
	}

	private static boolean isAirOrDifferentReplaceableBlock(BlockState state, ItemStack stack) {
		return state.isAir() || state.canBeReplaced() && !stack.is(state.getBlock().asItem());
	}

	@Override
	public boolean acceptsRedstone() {
		return true;
	}

	@Override
	public void readFromPacketNBT(CompoundTag cmp) {
		super.readFromPacketNBT(cmp);
		if (cmp.contains(TAG_STATE_SENSITIVE)) {
			stateSensitive = cmp.getBoolean(TAG_STATE_SENSITIVE);
		} else {
			// old flowers stay state sensitive, new flowers are state insensitive
			stateSensitive = true;
		}
	}

	@Override
	public void writeToPacketNBT(CompoundTag cmp) {
		super.writeToPacketNBT(cmp);
		cmp.putBoolean(TAG_STATE_SENSITIVE, stateSensitive);
	}

	@Override
	public boolean onUsedByWand(@Nullable Player player, ItemStack wand, Direction side) {
		if (player == null || player.isShiftKeyDown()) {
			stateSensitive = !stateSensitive;
			setChanged();
			sync();
			return true;
		}
		return false;
	}

	public static class WandHud extends BindableFlowerWandHud<RannuncarpusBlockEntity> {
		public WandHud(RannuncarpusBlockEntity flower) {
			super(flower);
		}

		@Override
		public void renderHUD(GuiGraphics gui, Minecraft mc) {
			ItemStack filterStack = new ItemStack(flower.getUnderlyingBlock().getBlock());
			int color = flower.getColor();
			String mode = I18n.get("botaniamisc.rannuncarpus." + (flower.stateSensitive ? "state_sensitive" : "state_insensitive"));
			int centerY = mc.getWindow().getGuiScaledHeight() / 2;
			int modeWidth = mc.font.width(mode);
			int modeTextStart = (mc.getWindow().getGuiScaledWidth() - modeWidth) / 2;
			int minWidth = Math.max(RenderHelper.itemWithNameWidth(mc, filterStack), modeWidth) + 4;

			super.renderHUD(gui, mc, minWidth / 2, minWidth / 2, filterStack.isEmpty() ? 40 : 60);
			gui.drawString(mc.font, mode, modeTextStart, centerY + 30, color);
			RenderHelper.renderItemWithNameCentered(gui, mc, filterStack, centerY + 40, color);
		}
	}

	@Override
	public RadiusDescriptor getRadius() {
		return RadiusDescriptor.Rectangle.square(getEffectivePos(), getPlaceRange());
	}

	@Override
	public RadiusDescriptor getSecondaryRadius() {
		if (getPlaceRange() == PICKUP_RANGE && getEffectivePos().equals(getBlockPos())) {
			return null;
		}
		return RadiusDescriptor.Rectangle.square(getBlockPos(), PICKUP_RANGE);
	}

	public int getPlaceRange() {
		return getMana() > 0 ? RANGE_PLACE_MANA : RANGE_PLACE;
	}

	public int getVerticalPlaceRange() {
		return RANGE_PLACE_Y;
	}

	@Override
	public int getMaxMana() {
		return 20;
	}

	@Override
	public int getColor() {
		return 0xFFB27F;
	}

	public static class Mini extends RannuncarpusBlockEntity {
		public Mini(BlockPos pos, BlockState state) {
			super(BotaniaFlowerBlocks.RANNUNCARPUS_CHIBI, pos, state);
		}

		@Override
		public int getPlaceRange() {
			return getMana() > 0 ? RANGE_PLACE_MANA_MINI : RANGE_PLACE_MINI;
		}

		@Override
		public int getVerticalPlaceRange() {
			return RANGE_PLACE_Y_MINI;
		}
	}

	// BlockItemUseContext uses a nullable player field without checking it -.-
	private static class RannuncarpusPlaceContext extends BlockPlaceContext {
		private final Direction[] lookDirs;
		private final float placementYaw;

		public RannuncarpusPlaceContext(Level world, ItemStack stack, BlockHitResult rtr, BlockPos flowerPos) {
			super(world, null, InteractionHand.MAIN_HAND, stack, rtr);
			int dx = rtr.getBlockPos().getX() - flowerPos.getX();
			int dy = rtr.getBlockPos().getY() - flowerPos.getY();
			int dz = rtr.getBlockPos().getZ() - flowerPos.getZ();

			Direction xClosest = dx >= 0 ? Direction.EAST : Direction.WEST;
			Direction yClosest = dy >= 0 ? Direction.UP : Direction.DOWN;
			Direction zClosest = dz >= 0 ? Direction.SOUTH : Direction.NORTH;

			List<Direction> directions = sortThree(xClosest, yClosest, zClosest, Math.abs(dx), Math.abs(dy), Math.abs(dz));

			Direction first = directions.get(0);
			Direction second = directions.get(1);
			Direction third = directions.get(2);

			lookDirs = new Direction[] {
					first,
					second,
					third,
					third.getOpposite(),
					second.getOpposite(),
					first.getOpposite()
			};

			placementYaw = (float) (-Mth.atan2(dx, dz) * 180 / Math.PI);
		}

		/**
		 * Arrange a, b and c such that their corresponding ints (a -> aInt) are in descending order.
		 */
		private static <T> List<T> sortThree(T a, T b, T c, int aInt, int bInt, int cInt) {
			if (aInt >= bInt) {
				if (bInt >= cInt) {
					return ImmutableList.of(a, b, c);
				} else {
					return cInt >= aInt ? ImmutableList.of(c, a, b) : ImmutableList.of(a, c, b);
				}
			} else if (bInt >= cInt) {
				return cInt >= aInt ? ImmutableList.of(b, c, a) : ImmutableList.of(b, a, c);
			} else {
				return ImmutableList.of(c, b, a);
			}
		}

		@NotNull
		@Override
		public Direction getNearestLookingDirection() {
			return getNearestLookingDirections()[0];
		}

		@NotNull
		@Override
		public Direction[] getNearestLookingDirections() {
			return lookDirs;
		}

		@NotNull
		@Override
		public Direction getHorizontalDirection() {
			return getNearestLookingDirection().getAxis().isHorizontal() ? getNearestLookingDirection() : getNearestLookingDirections()[1];
		}

		@Override
		public float getRotation() {
			return placementYaw;
		}
	}

}
