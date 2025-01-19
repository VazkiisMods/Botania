/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity.corporea;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.block.Wandable;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.CorporeaRequestMatcher;
import vazkii.botania.api.corporea.CorporeaRequestor;
import vazkii.botania.api.corporea.CorporeaSpark;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;

import java.util.List;

public class CorporeaCrystalCubeBlockEntity extends BaseCorporeaBlockEntity implements CorporeaRequestor, Wandable {
	private static final String TAG_REQUEST_TARGET = "requestTarget";
	private static final String TAG_ITEM_COUNT = "itemCount";
	private static final String TAG_LOCK = "lock";

	private ItemStack requestTarget = ItemStack.EMPTY;
	private int itemCount = 0;
	private int ticks = 0;
	private int compValue = 0;
	public boolean locked = false;

	public CorporeaCrystalCubeBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.CORPOREA_CRYSTAL_CUBE, pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, CorporeaCrystalCubeBlockEntity self) {
		++self.ticks;
		if (self.ticks % 20 == 0) {
			self.updateCount();
		}
	}

	public void setRequestTarget(ItemStack stack) {
		if (!stack.isEmpty() && !locked) {
			requestTarget = stack.copyWithCount(1);
			setChanged();
			updateCount();
		}

	}

	public ItemStack getRequestTarget() {
		return requestTarget;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void doRequest(Player player) {
		if (level.isClientSide) {
			return;
		}

		CorporeaSpark spark = getSpark();
		if (spark != null && spark.getMaster() != null && !requestTarget.isEmpty()) {
			int count = player.isShiftKeyDown() ? requestTarget.getMaxStackSize() : 1;
			var matcher = CorporeaHelper.instance().createMatcher(requestTarget, true);
			doCorporeaRequest(matcher, count, spark, player);
		}
	}

	private void updateCount() {
		if (level.isClientSide) {
			return;
		}

		int sum = 0;
		CorporeaSpark spark = getSpark();
		if (spark != null && spark.getMaster() != null && !requestTarget.isEmpty()) {
			var matcher = CorporeaHelper.instance().createMatcher(requestTarget, true);
			List<ItemStack> stacks = CorporeaHelper.instance().requestItem(matcher, -1, spark, null, false).stacks();
			for (ItemStack stack : stacks) {
				sum += stack.getCount();
			}
		}

		setCount(sum);
	}

	private void setCount(int count) {
		int oldCount = this.itemCount;
		this.itemCount = count;
		if (this.itemCount != oldCount) {
			this.compValue = CorporeaHelper.instance().signalStrengthForRequestSize(itemCount);
			setChanged();
		}
	}

	@Override
	public void writePacketNBT(CompoundTag tag, HolderLookup.Provider registries) {
		super.writePacketNBT(tag, registries);
		if (!requestTarget.isEmpty()) {
			tag.put(TAG_REQUEST_TARGET, requestTarget.save(registries));
		}
		tag.putInt(TAG_ITEM_COUNT, itemCount);
		tag.putBoolean(TAG_LOCK, locked);
	}

	@Override
	public void readPacketNBT(CompoundTag tag, HolderLookup.Provider registries) {
		super.readPacketNBT(tag, registries);
		CompoundTag cmp = tag.getCompound(TAG_REQUEST_TARGET);
		requestTarget = ItemStack.parse(registries, cmp).orElse(ItemStack.EMPTY);
		setCount(tag.getInt(TAG_ITEM_COUNT));
		locked = tag.getBoolean(TAG_LOCK);
	}

	public int getComparatorValue() {
		return compValue;
	}

	@Override
	public void doCorporeaRequest(CorporeaRequestMatcher request, int count, CorporeaSpark spark, @Nullable LivingEntity entity) {
		if (!requestTarget.isEmpty()) {
			List<ItemStack> stacks = CorporeaHelper.instance().requestItem(request, count, spark, entity, true).stacks();
			spark.onItemsRequested(stacks);
			boolean did = false;
			int sum = 0;
			for (ItemStack reqStack : stacks) {
				ItemEntity item = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.5, worldPosition.getZ() + 0.5, reqStack);
				level.addFreshEntity(item);
				if (ItemStack.isSameItem(requestTarget, reqStack)) {
					sum += reqStack.getCount();
					did = true;
				}
			}

			if (did) {
				setCount(getItemCount() - sum);
			}
		}
	}

	@Override
	public boolean onUsedByWand(@Nullable Player player, ItemStack stack, Direction side) {
		if (player == null || player.isShiftKeyDown()) {
			this.locked = !this.locked;
			setChanged();
			return true;
		}
		return false;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level != null) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
		}
	}

	public static class Hud {
		public static void render(GuiGraphics gui, CorporeaCrystalCubeBlockEntity cube) {
			PoseStack ps = gui.pose();
			Minecraft mc = Minecraft.getInstance();
			ProfilerFiller profiler = mc.getProfiler();

			profiler.push("crystalCube");
			ItemStack target = cube.getRequestTarget();
			if (!target.isEmpty()) {
				String nameStr = target.getHoverName().getString();
				String countStr = cube.getItemCount() + "x";
				String lockedStr = I18n.get("botaniamisc.locked");

				int strlen = Math.max(mc.font.width(nameStr), mc.font.width(countStr));
				if (cube.locked) {
					strlen = Math.max(strlen, mc.font.width(lockedStr));
				}

				int centerX = mc.getWindow().getGuiScaledWidth() / 2;
				int centerY = mc.getWindow().getGuiScaledHeight() / 2;
				ps.pushPose();
				ps.translate(centerX, centerY, 0);

				RenderHelper.renderHUDBox(gui, 8, -11, strlen + 32, cube.locked ? 21 : 11);

				gui.drawString(mc.font, nameStr, 30, -9, 0x6666FF);
				gui.drawString(mc.font, countStr, 30, 1, 0xFFFFFF);
				if (cube.locked) {
					gui.drawString(mc.font, lockedStr, 30, 11, 0xFFAA00);
				}

				gui.renderItem(target, 10, -9);

				ps.popPose();
			}

			profiler.pop();
		}
	}
}
