/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.item.BlockProvider;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.gui.ItemsRemainingRenderHandler;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.BlockProviderHelper;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.item.relic.RingOfLokiItem;
import vazkii.botania.common.item.rod.ShiftingCrustRodItem;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.ArrayList;
import java.util.List;

public class AstrolabeItem extends Item {

	public static final String TAG_BLOCKSTATE = "blockstate";
	public static final String TAG_SIZE = "size";
	public static final int BASE_COST = 320;

	public AstrolabeItem(Properties props) {
		super(props);
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		ItemStack stack = ctx.getItemInHand();
		BlockState state = ctx.getLevel().getBlockState(ctx.getClickedPos());
		Player player = ctx.getPlayer();

		if (player != null && player.isSecondaryUseActive()) {
			if (setBlock(stack, state)) {
				displayRemainderCounter(player, stack);
				return InteractionResult.sidedSuccess(player.level().isClientSide());
			}
		} else if (player != null) {
			boolean did = placeAllBlocks(stack, player, ctx.getHand());
			if (did) {
				displayRemainderCounter(player, stack);
			}

			return did ? InteractionResult.SUCCESS : InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @NotNull InteractionHand hand) {
		ItemStack stack = playerIn.getItemInHand(hand);
		if (playerIn.isSecondaryUseActive()) {
			playerIn.playSound(BotaniaSounds.astrolabeConfigure, 1F, 1F);
			if (!worldIn.isClientSide) {
				int size = getSize(stack);
				int newSize = size == 11 ? 3 : size + 2;
				setSize(stack, newSize);
				ItemsRemainingRenderHandler.send(playerIn, stack, 0, Component.literal(newSize + "x" + newSize));
			}

			return InteractionResultHolder.sidedSuccess(stack, worldIn.isClientSide());
		}

		return InteractionResultHolder.pass(stack);
	}

	public boolean placeAllBlocks(ItemStack requester, Player player, InteractionHand hand) {
		Block blockToPlace = getBlock(requester, player.level().holderLookup(Registries.BLOCK));
		int size = getSize(requester);
		BlockPlaceContext ctx = getBlockPlaceContext(player, hand, blockToPlace);
		List<BlockPos> placePositions = getPlacePositions(ctx, size);
		List<BlockProvider> blockProviders = findBlockProviders(requester, player, placePositions.size(), blockToPlace);
		if (ctx == null || blockProviders.isEmpty()) {
			return false;
		}

		int cost = size * BASE_COST;
		if (!ManaItemHandler.instance().requestManaExact(requester, player, cost, true)) {
			return false;
		}

		for (BlockPos coords : placePositions) {
			if (!placeBlockAndConsume(requester, blockToPlace, ctx, coords, blockProviders)) {
				break;
			}
		}

		return true;
	}

	/**
	 * Attempts to place the specified block and consume it from the player's inventory.
	 *
	 * @return {@code true} if continuing to attempt placing blocks makes sense,
	 *         {@code false} if not, e.g. because there are fewer blocks available than expected.
	 */
	private boolean placeBlockAndConsume(ItemStack requester, Block blockToPlace, BlockPlaceContext ctx,
			BlockPos pos, List<BlockProvider> providers) {
		final Player player = ctx.getPlayer();
		if (player == null) {
			return false;
		}

		BlockState state = blockToPlace.getStateForPlacement(ctx);
		if (state == null) {
			return true;
		}

		if (providers.stream().noneMatch(prov -> prov.provideBlock(player, requester, blockToPlace, false))) {
			// don't place blocks we don't have (e.g. because mana calculations were inaccurate somehow)
			return false;
		}

		UseOnContext useOnContext = RingOfLokiItem.getUseOnContext(player, ctx.getHand(), pos, ctx.getClickLocation(), ctx.getClickedFace());
		if (!PlayerHelper.substituteUse(useOnContext, new ItemStack(blockToPlace)).consumesAction()) {
			return true;
		}
		for (BlockProvider prov : providers) {
			if (prov.provideBlock(player, requester, blockToPlace, true)) {
				break;
			}
		}
		return true;
	}

	public static boolean hasBlocks(ItemStack requester, Player player, int required, Block block) {
		if (player.getAbilities().instabuild) {
			return true;
		}

		return !findBlockProviders(requester, player, required, block).isEmpty();
	}

	public static List<BlockProvider> findBlockProviders(ItemStack requester, Player player, int required, Block block) {
		if (block == Blocks.AIR || required == 0) {
			return List.of();
		}
		if (player.getAbilities().instabuild) {
			return List.of(BlockProviderHelper.asInfiniteBlockProvider(new ItemStack(block)));
		}

		int current = 0;
		List<BlockProvider> providersToCheck = new ArrayList<>();
		for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
			ItemStack stackInSlot = player.getInventory().getItem(i);
			if (stackInSlot.isEmpty()) {
				continue;
			}
			if (stackInSlot.is(block.asItem())) {
				current += stackInSlot.getCount();
				final var stackProvider = BlockProviderHelper.asBlockProvider(stackInSlot);
				providersToCheck.add(stackProvider);
			} else {
				var provider = XplatAbstractions.INSTANCE.findBlockProvider(stackInSlot);
				if (provider != null) {
					final int count = provider.getBlockCount(player, requester, block);
					if (count != 0) {
						current += count;
						providersToCheck.add(provider);
					}
				}
			}
		}

		return current >= required ? providersToCheck : List.of();
	}

	@Nullable
	public static BlockPlaceContext getBlockPlaceContext(Player player, InteractionHand hand, Block blockToPlace) {
		if (blockToPlace == Blocks.AIR) {
			return null;
		}
		BlockHitResult rtr = ToolCommons.raytraceFromEntity(player, 5, true);
		return rtr.getType() == HitResult.Type.BLOCK
				? new BlockPlaceContext(player, hand, new ItemStack(blockToPlace.asItem()), rtr)
				: null;
	}

	public static List<BlockPos> getPlacePositions(BlockPlaceContext ctx, int size) {
		if (ctx == null || ctx.getPlayer() == null) {
			return List.of();
		}
		List<BlockPos> coords = new ArrayList<>();
		BlockPos pos = ctx.getClickedPos();
		BlockState clickedState = ctx.getLevel().getBlockState(pos);
		if (clickedState.canBeReplaced() || clickedState.canBeReplaced(ctx)) {
			pos = pos.relative(ctx.getClickedFace().getOpposite());
		}

		int range = (size ^ 1) / 2;

		Direction dir = ctx.getClickedFace();
		Direction rotationDir = Direction.fromYRot(ctx.getPlayer().getYRot());

		boolean pitchedVertically = Math.abs(ctx.getPlayer().getXRot()) > 50;

		boolean axisX = rotationDir.getAxis() == Axis.X;
		boolean axisZ = rotationDir.getAxis() == Axis.Z;

		int xOff = axisZ || pitchedVertically ? range : 0;
		int yOff = pitchedVertically ? 0 : range;
		int zOff = axisX || pitchedVertically ? range : 0;

		for (int x = -xOff; x < xOff + 1; x++) {
			for (int y = 0; y < yOff * 2 + 1; y++) {
				for (int z = -zOff; z < zOff + 1; z++) {
					int xp = pos.getX() + x + dir.getStepX();
					int yp = pos.getY() + y + dir.getStepY();
					int zp = pos.getZ() + z + dir.getStepZ();

					BlockPos newPos = new BlockPos(xp, yp, zp);
					BlockState state = ctx.getLevel().getBlockState(newPos);
					if (ctx.getLevel().getWorldBorder().isWithinBounds(newPos)
							&& (state.isAir() || state.canBeReplaced() || state.canBeReplaced(ctx))) {
						coords.add(newPos);
					}
				}
			}
		}

		return coords;
	}

	public void displayRemainderCounter(Player player, ItemStack stack) {
		Block block = getBlock(stack, player.level().holderLookup(Registries.BLOCK));
		int count = ShiftingCrustRodItem.getInventoryItemCount(player, stack, block.asItem());
		if (!player.level().isClientSide) {
			ItemsRemainingRenderHandler.send(player, new ItemStack(block), count);
		}
	}

	public static boolean setBlock(ItemStack stack, BlockState state) {
		if (!state.isAir()) {
			// This stores a block state (instead of just the block ID) for legacy reasons.
			ItemNBTHelper.setCompound(stack, TAG_BLOCKSTATE, NbtUtils.writeBlockState(state.getBlock().defaultBlockState()));
			return true;
		}
		return false;
	}

	public static void setSize(ItemStack stack, int size) {
		ItemNBTHelper.setInt(stack, TAG_SIZE, size | 1);
	}

	public static int getSize(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_SIZE, 3) | 1;
	}

	public static Block getBlock(ItemStack stack, HolderGetter<Block> holderGetter) {
		return getBlockState(stack, holderGetter).getBlock();
	}

	public static BlockState getBlockState(ItemStack stack, HolderGetter<Block> holderGetter) {
		return NbtUtils.readBlockState(holderGetter, ItemNBTHelper.getCompound(stack, TAG_BLOCKSTATE, false));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tip, TooltipFlag flags) {
		if (context == null) {
			return;
		}

		Block block = getBlock(stack, context.registries().lookupOrThrow(Registries.BLOCK));
		int size = getSize(stack);

		tip.add(Component.literal(size + " x " + size));
		if (block != Blocks.AIR) {
			tip.add(new ItemStack(block).getHoverName().plainCopy().withStyle(ChatFormatting.GRAY));
		}
	}

}
