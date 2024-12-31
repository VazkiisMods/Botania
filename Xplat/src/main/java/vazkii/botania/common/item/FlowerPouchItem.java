/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.client.gui.bag.FlowerPouchContainer;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.BotaniaDoubleFlowerBlock;
import vazkii.botania.common.block.BotaniaFlowerBlock;
import vazkii.botania.common.helper.EntityHelper;
import vazkii.botania.common.helper.InventoryHelper;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.stream.IntStream;

public class FlowerPouchItem extends Item {
	public static final int DYE_COUNT = 16;
	public static final int SIZE = 2 * DYE_COUNT;

	public FlowerPouchItem(Properties props) {
		super(props);
	}

	public static Item getFlowerForSlot(int slot) {
		if (slot < DYE_COUNT) {
			DyeColor color = DyeColor.byId(slot);
			return BotaniaBlocks.getFlower(color).asItem();
		} else {
			DyeColor color = DyeColor.byId(slot - DYE_COUNT);
			return BotaniaBlocks.getDoubleFlower(color).asItem();
		}
	}

	private static boolean isMysticalFlower(ItemStack stack) {
		Class<? extends Block> blockClass = Block.byItem(stack.getItem()).getClass();
		// Direct class compare needed because glimmering flowers also extend BotaniaFlowerBlock
		// And we might want to add glimmering tall flowers at some point
		return blockClass == BotaniaFlowerBlock.class || blockClass == BotaniaDoubleFlowerBlock.class;
	}

	public static SimpleContainer getInventory(ItemStack stack) {
		return new ItemBackedInventory(stack, SIZE) {
			@Override
			public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
				return stack.is(getFlowerForSlot(slot));
			}
		};
	}

	public static boolean onPickupItem(ItemEntity entity, Player player) {
		ItemStack entityStack = entity.getItem();
		Block block = Block.byItem(entityStack.getItem());
		if (isMysticalFlower(entityStack) && entityStack.getCount() > 0) {
			int slot;
			if (block instanceof BotaniaDoubleFlowerBlock flower) {
				slot = 16 + flower.color.getId();
			} else {
				slot = ((BotaniaFlowerBlock) block).color.getId();
			}

			for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
				if (i == player.getInventory().selected) {
					continue; // prevent item deletion
				}

				ItemStack bag = player.getInventory().getItem(i);
				if (!bag.isEmpty() && bag.is(BotaniaItems.flowerBag)) {
					SimpleContainer bagInv = getInventory(bag);
					ItemStack existing = bagInv.getItem(slot);
					int newCount = Math.min(existing.getCount() + entityStack.getCount(),
							Math.min(existing.getMaxStackSize(), bagInv.getMaxStackSize()));
					int numPickedUp = newCount - existing.getCount();

					if (numPickedUp > 0) {
						if (existing.isEmpty()) {
							bagInv.setItem(slot, entityStack.split(numPickedUp));
						} else {
							existing.grow(numPickedUp);
							entityStack.shrink(numPickedUp);
						}
						EntityHelper.syncItem(entity);
						bagInv.setChanged();

						player.take(entity, numPickedUp);

						return true;
					}
				}
			}
		}
		return false;
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		if (!world.isClientSide) {
			ItemStack stack = player.getItemInHand(hand);
			XplatAbstractions.INSTANCE.openMenu((ServerPlayer) player, new MenuProvider() {
				@Override
				public Component getDisplayName() {
					return stack.getHoverName();
				}

				@Override
				public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
					return new FlowerPouchContainer(syncId, inv, stack);
				}
			}, buf -> buf.writeBoolean(hand == InteractionHand.MAIN_HAND));
		}
		return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), world.isClientSide());
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Direction side = ctx.getClickedFace();

		BlockEntity tile = world.getBlockEntity(pos);
		if ((ctx.getPlayer() == null || ctx.getPlayer().isSecondaryUseActive())
				&& XplatAbstractions.INSTANCE.hasInventory(world, pos, side)) {
			if (!world.isClientSide) {
				Container bagInv = getInventory(ctx.getItemInHand());
				for (int i = 0; i < bagInv.getContainerSize(); i++) {
					ItemStack flower = bagInv.getItem(i);
					ItemStack rem = XplatAbstractions.INSTANCE.insertToInventory(world, pos, side, flower, false);
					bagInv.setItem(i, rem);
				}

			}

			return InteractionResult.sidedSuccess(world.isClientSide());
		}
		return InteractionResult.PASS;
	}

	@Override
	public void onDestroyed(@NotNull ItemEntity entity) {
		var container = getInventory(entity.getItem());
		var stream = IntStream.range(0, container.getContainerSize())
				.mapToObj(container::getItem)
				.filter(s -> !s.isEmpty());
		ItemUtils.onContainerDestroyed(entity, stream);
		container.clearContent();
	}

	@Override
	public boolean overrideStackedOnOther(
			@NotNull ItemStack bag, @NotNull Slot slot,
			@NotNull ClickAction clickAction, @NotNull Player player) {
		return InventoryHelper.overrideStackedOnOther(
				FlowerPouchItem::getInventory,
				player.containerMenu instanceof FlowerPouchContainer,
				bag, slot, clickAction, player);
	}

	@Override
	public boolean overrideOtherStackedOnMe(
			@NotNull ItemStack bag, @NotNull ItemStack toInsert,
			@NotNull Slot slot, @NotNull ClickAction clickAction,
			@NotNull Player player, @NotNull SlotAccess cursorAccess) {
		return InventoryHelper.overrideOtherStackedOnMe(
				FlowerPouchItem::getInventory,
				player.containerMenu instanceof FlowerPouchContainer,
				bag, toInsert, clickAction, cursorAccess);
	}
}
