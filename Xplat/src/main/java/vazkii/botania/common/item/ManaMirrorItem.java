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
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.api.item.CoordBoundItem;
import vazkii.botania.api.mana.ManaBarTooltip;
import vazkii.botania.api.mana.ManaPool;
import vazkii.botania.common.component.BotaniaDataComponents;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.Optional;

public class ManaMirrorItem extends Item {

	private static final DummyPool fallbackPool = new DummyPool();

	public ManaMirrorItem(Properties props) {
		super(props);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return stack.has(BotaniaDataComponents.MANA_POOL_POS);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		var manaItem = XplatAbstractions.INSTANCE.findManaItem(stack);
		return Math.round(13 * ManaBarTooltip.getFractionForDisplay(manaItem));
	}

	@Override
	public int getBarColor(ItemStack stack) {
		var manaItem = XplatAbstractions.INSTANCE.findManaItem(stack);
		return Mth.hsvToRgb(ManaBarTooltip.getFractionForDisplay(manaItem) / 3.0F, 1.0F, 1.0F);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		if (world.isClientSide) {
			return;
		}

		ManaPool pool = getManaPool(world.getServer(), stack);
		if (!(pool instanceof DummyPool)) {
			if (pool == null) {
				setMana(stack, 0);
			} else {
				pool.receiveMana(getManaBacklog(stack));
				clearManaBacklog(stack);
				setMana(stack, pool.getCurrentMana());
				setMaxMana(stack, pool.getMaxMana());
			}
		}
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level world = ctx.getLevel();
		Player player = ctx.getPlayer();

		if (player != null && player.isSecondaryUseActive()) {
			var receiver = XplatAbstractions.INSTANCE.findManaReceiver(world, ctx.getClickedPos(), null);
			if (receiver instanceof ManaPool pool) {
				if (!world.isClientSide) {
					bindPool(ctx.getItemInHand(), pool);
					world.playSound(null, player.getX(), player.getY(), player.getZ(), BotaniaSounds.ding, SoundSource.PLAYERS, 1F, 1F);
				}
				return InteractionResult.sidedSuccess(world.isClientSide());
			}
		}

		return InteractionResult.PASS;
	}

	protected static void setMana(ItemStack stack, int mana) {
		stack.set(BotaniaDataComponents.MANA, mana);
	}

	protected static void setMaxMana(ItemStack stack, int maxMana) {
		stack.set(BotaniaDataComponents.MAX_MANA, maxMana);
	}

	protected static int getManaBacklog(ItemStack stack) {
		return stack.getOrDefault(BotaniaDataComponents.MANA_BACKLOG, 0);
	}

	protected static void clearManaBacklog(ItemStack stack) {
		stack.set(BotaniaDataComponents.MANA_BACKLOG, 0);
	}

	public void bindPool(ItemStack stack, ManaPool pool) {
		GlobalPos pos = GlobalPos.of(pool.getManaReceiverLevel().dimension(), pool.getManaReceiverPos());
		stack.set(BotaniaDataComponents.MANA_POOL_POS, pos);
	}

	@Nullable
	private static GlobalPos getBoundPos(ItemStack stack) {
		return stack.get(BotaniaDataComponents.MANA_POOL_POS);
	}

	@Nullable
	private ManaPool getManaPool(@Nullable MinecraftServer server, ItemStack stack) {
		if (server == null) {
			return fallbackPool;
		}

		GlobalPos pos = getBoundPos(stack);
		if (pos == null) {
			return fallbackPool;
		}

		ResourceKey<Level> type = pos.dimension();
		Level world = server.getLevel(type);
		if (world != null) {
			var receiver = XplatAbstractions.INSTANCE.findManaReceiver(world, pos.pos(), null);
			if (receiver instanceof ManaPool pool) {
				return pool;
			}
		}

		return null;
	}

	private static class DummyPool implements ManaPool {

		@Override
		public boolean isFull() {
			return false;
		}

		@Override
		public void receiveMana(int mana) {}

		@Override
		public boolean canReceiveManaFromBursts() {
			return false;
		}

		@Override
		public Level getManaReceiverLevel() {
			return null;
		}

		@Override
		public BlockPos getManaReceiverPos() {
			return ManaBurst.NO_SOURCE;
		}

		@Override
		public int getCurrentMana() {
			return 0;
		}

		@Override
		public int getMaxMana() {
			return 0;
		}

		@Override
		public boolean isOutputtingPower() {
			return false;
		}

		@Override
		public Optional<DyeColor> getColor() {
			return Optional.empty();
		}

		@Override
		public void setColor(Optional<DyeColor> color) {}

	}

	public static class CoordBoundItemImpl implements CoordBoundItem {
		private final ItemStack stack;

		public CoordBoundItemImpl(ItemStack stack) {
			this.stack = stack;
		}

		@Nullable
		@Override
		public BlockPos getBinding(Level world) {
			GlobalPos pos = getBoundPos(stack);
			if (pos == null) {
				return null;
			}

			if (pos.dimension() == world.dimension()) {
				return pos.pos();
			}

			return null;
		}
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return Optional.of(ManaBarTooltip.fromManaItem(stack));
	}

}
