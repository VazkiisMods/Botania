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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.item.BlockProvider;
import vazkii.botania.client.gui.ItemsRemainingRenderHandler;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.helper.PlayerHelper;

import java.util.List;

public class BlackHoleTalismanItem extends Item {
	public static final String TAG_ACTIVE = "active";
	public static final String TAG_BLOCK_NAME = "blockName";
	public static final String TAG_BLOCK_COUNT = "blockCount";

	public BlackHoleTalismanItem(Properties props) {
		super(props);
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (getBlock(stack) != null && player.isSecondaryUseActive()) {
			ItemNBTHelper.setBoolean(stack, TAG_ACTIVE, !ItemNBTHelper.getBoolean(stack, TAG_ACTIVE, false));
			player.playSound(BotaniaSounds.blackHoleTalismanConfigure, 1F, 1F);
			return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
		}

		return InteractionResultHolder.pass(stack);
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Direction side = ctx.getClickedFace();
		Player player = ctx.getPlayer();
		BlockState state = world.getBlockState(pos);
		ItemStack stack = ctx.getItemInHand();

		if (!state.isAir() && setBlock(stack, state.getBlock())) {
			return InteractionResult.sidedSuccess(world.isClientSide());
		} else {
			Block bBlock = getBlock(stack);

			if (bBlock == null) {
				return InteractionResult.PASS;
			}

			BlockEntity tile = world.getBlockEntity(pos);
			if (tile instanceof Container container) {
				if (!world.isClientSide) {
					ItemStack toAdd = new ItemStack(bBlock);
					int maxSize = toAdd.getMaxStackSize();
					toAdd.setCount(remove(stack, maxSize));
					ItemStack remainder = HopperBlockEntity.addItem(null, container, toAdd, side);
					if (!remainder.isEmpty()) {
						add(stack, remainder.getCount());
					}
				}
				return InteractionResult.sidedSuccess(world.isClientSide());
			} else {
				if (player == null || player.getAbilities().instabuild || getBlockCount(stack) > 0) {
					ItemStack toUse = new ItemStack(bBlock);
					InteractionResult result = PlayerHelper.substituteUse(ctx, toUse);

					if (result.consumesAction()) {
						if (!world.isClientSide) {
							remove(stack, 1);
							ItemsRemainingRenderHandler.send(player, toUse, getBlockCount(stack));
						}
						return result;
					}
				}
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		Block block = getBlock(itemstack);
		if (!entity.level().isClientSide && ItemNBTHelper.getBoolean(itemstack, TAG_ACTIVE, false) && block != null) {
			if (entity instanceof Player player) {
				suckFromPlayerInv(itemstack, block, player);
			}
		}
	}

	private static void suckFromPlayerInv(ItemStack talisman, Block toTake, Player player) {
		int highestIdx = -1;
		int[] counts = new int[player.getInventory().getContainerSize() - player.getInventory().armor.size()];

		for (int i = 0; i < counts.length; i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (stack.isEmpty()) {
				continue;
			}

			if (toTake.asItem() == stack.getItem()
					&& (stack.getTag() == null || stack.getTag().isEmpty())) {
				counts[i] = stack.getCount();
				if (highestIdx == -1) {
					highestIdx = i;
				} else {
					highestIdx = counts[i] > counts[highestIdx] && highestIdx > 8 ? i : highestIdx;
				}
			}
		}

		if (highestIdx != -1) {
			for (int i = 0; i < counts.length; i++) {
				int count = counts[i];

				if (count == 0) {
					continue;
				}

				add(talisman, count);
				player.getInventory().setItem(i, ItemStack.EMPTY);
			}
		}
	}

	@NotNull
	@Override
	public Component getName(@NotNull ItemStack stack) {
		Block block = getBlock(stack);
		ItemStack bstack = block == null ? ItemStack.EMPTY : new ItemStack(block);
		MutableComponent cand = super.getName(stack).copy();

		if (!bstack.isEmpty()) {
			cand.append(" (");
			cand.append(bstack.getHoverName().copy().withStyle(ChatFormatting.GREEN));
			cand.append(")");
		}

		return cand;
	}

	public static boolean setBlock(ItemStack stack, Block block) {
		if (block.asItem() != Items.AIR && (getBlock(stack) == null || getBlockCount(stack) == 0)) {
			ItemNBTHelper.setString(stack, TAG_BLOCK_NAME, BuiltInRegistries.BLOCK.getKey(block).toString());
			return true;
		}
		return false;
	}

	private static void add(ItemStack stack, int count) {
		int current = getBlockCount(stack);
		setCount(stack, current + count);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> stacks, TooltipFlag flags) {
		Block block = getBlock(stack);
		if (block != null) {
			int count = getBlockCount(stack);
			stacks.add(Component.literal(count + " ").append(new ItemStack(block).getHoverName()).withStyle(ChatFormatting.GRAY));
		}

		if (ItemNBTHelper.getBoolean(stack, TAG_ACTIVE, false)) {
			stacks.add(Component.translatable("botaniamisc.active"));
		} else {
			stacks.add(Component.translatable("botaniamisc.inactive"));
		}
	}

	public static void setCount(ItemStack stack, int count) {
		ItemNBTHelper.setInt(stack, TAG_BLOCK_COUNT, count);
	}

	public static int remove(ItemStack stack, int count) {
		int current = getBlockCount(stack);
		setCount(stack, Math.max(current - count, 0));

		return Math.min(current, count);
	}

	private static String getBlockName(ItemStack stack) {
		return ItemNBTHelper.getString(stack, TAG_BLOCK_NAME, "");
	}

	@Nullable
	public static Block getBlock(ItemStack stack) {
		ResourceLocation id = ResourceLocation.tryParse(getBlockName(stack));
		if (id != null) {
			return BuiltInRegistries.BLOCK.getOptional(id).orElse(null);
		}
		return null;
	}

	public static int getBlockCount(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_BLOCK_COUNT, 0);
	}

	public static class BlockProviderImpl implements BlockProvider {
		private final ItemStack stack;

		public BlockProviderImpl(ItemStack stack) {
			this.stack = stack;
		}

		@Override
		public boolean provideBlock(Player player, ItemStack requestor, Block block, boolean doit) {
			Block stored = getBlock(stack);
			if (stored == block) {
				int count = BlackHoleTalismanItem.getBlockCount(stack);
				if (count > 0) {
					if (doit) {
						setCount(stack, count - 1);
					}
					return true;
				}
			}

			return false;
		}

		@Override
		public int getBlockCount(Player player, ItemStack requestor, Block block) {
			Block stored = getBlock(stack);
			if (stored == block) {
				return BlackHoleTalismanItem.getBlockCount(stack);
			}
			return 0;
		}
	}

	@Override
	public boolean overrideStackedOnOther(@NotNull ItemStack talisman, @NotNull Slot slot,
			@NotNull ClickAction clickAction, @NotNull Player player) {
		if (clickAction == ClickAction.SECONDARY) {
			ItemStack toInsert = slot.getItem();
			Block blockToInsert = Block.byItem(toInsert.getItem());
			if (blockToInsert != Blocks.AIR) {
				Block existingBlock = getBlock(talisman);
				if (existingBlock == null || existingBlock == blockToInsert) {
					ItemStack taken = slot.safeTake(toInsert.getCount(), Integer.MAX_VALUE, player);
					if (existingBlock == null) {
						setBlock(talisman, blockToInsert);
						setCount(talisman, taken.getCount());
					} else {
						add(talisman, taken.getCount());
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean overrideOtherStackedOnMe(
			@NotNull ItemStack talisman, @NotNull ItemStack toInsert, @NotNull Slot slot,
			@NotNull ClickAction clickAction, @NotNull Player player, @NotNull SlotAccess cursorAccess) {
		if (clickAction == ClickAction.SECONDARY) {
			Block blockToInsert = Block.byItem(toInsert.getItem());
			if (blockToInsert != Blocks.AIR) {
				Block existingBlock = getBlock(talisman);
				if (existingBlock == null || existingBlock == blockToInsert) {
					if (existingBlock == null) {
						setBlock(talisman, blockToInsert);
						setCount(talisman, toInsert.getCount());
					} else {
						add(talisman, toInsert.getCount());
					}
					cursorAccess.set(ItemStack.EMPTY);
					return true;
				}
			}
		}
		return false;
	}
}
