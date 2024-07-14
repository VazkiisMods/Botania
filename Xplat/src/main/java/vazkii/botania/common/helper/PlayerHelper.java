/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.helper;

import com.mojang.datafixers.util.Pair;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class PlayerHelper {

	private static final Pattern FAKE_PLAYER_PATTERN = Pattern.compile("^(?:\\[.*]|ComputerCraft)$");
	private static Class<? extends Player> fakePlayerClass;

	public static void setFakePlayerClass(Class<? extends Player> fakePlayerClass) {
		PlayerHelper.fakePlayerClass = fakePlayerClass;
	}

	public static boolean isTruePlayer(@Nullable Entity e) {
		if (!(e instanceof Player player)) {
			return false;
		}

		String name = player.getName().getString();
		return (fakePlayerClass == null || !fakePlayerClass.isInstance(player)) && !FAKE_PLAYER_PATTERN.matcher(name).matches();
	}

	public static List<Player> getRealPlayersIn(Level level, AABB aabb) {
		return level.getEntitiesOfClass(Player.class, aabb, player -> isTruePlayer(player) && !player.isSpectator());
	}

	// Checks if either of the player's hands has an item.
	public static boolean hasAnyHeldItem(Player player) {
		return !player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty();
	}

	// Checks main hand, then off hand for this item.
	public static boolean hasHeldItem(Player player, Item item) {
		return !player.getMainHandItem().isEmpty() && player.getMainHandItem().is(item)
				|| !player.getOffhandItem().isEmpty() && player.getOffhandItem().is(item);
	}

	// Checks main hand, then off hand for this item class.
	public static boolean hasHeldItemClass(Player player, Class<?> template) {
		return !player.getMainHandItem().isEmpty() && template.isAssignableFrom(player.getMainHandItem().getItem().getClass())
				|| !player.getOffhandItem().isEmpty() && template.isAssignableFrom(player.getOffhandItem().getItem().getClass());
	}

	public static ItemStack getFirstHeldItem(LivingEntity living, Item item) {
		return getFirstHeldItem(living, s -> s.is(item));
	}

	public static ItemStack getFirstHeldItem(LivingEntity living, Predicate<ItemStack> pred) {
		ItemStack main = living.getMainHandItem();
		ItemStack offhand = living.getOffhandItem();
		if (!main.isEmpty() && pred.test(main)) {
			return main;
		} else if (!offhand.isEmpty() && pred.test(offhand)) {
			return offhand;
		} else {
			return ItemStack.EMPTY;
		}
	}

	public static ItemStack getFirstHeldItemClass(LivingEntity living, Class<?> template) {
		return getFirstHeldItem(living, s -> template.isAssignableFrom(s.getItem().getClass()));
	}

	public static ItemStack getAmmo(Player player, Predicate<ItemStack> ammoFunc) {
		// Mainly from ItemBow.findAmmo
		if (ammoFunc.test(player.getItemInHand(InteractionHand.OFF_HAND))) {
			return player.getItemInHand(InteractionHand.OFF_HAND);
		} else if (ammoFunc.test(player.getItemInHand(InteractionHand.MAIN_HAND))) {
			return player.getItemInHand(InteractionHand.MAIN_HAND);
		} else {
			for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
				ItemStack itemstack = player.getInventory().getItem(i);

				if (ammoFunc.test(itemstack)) {
					return itemstack;
				}
			}

			return ItemStack.EMPTY;
		}
	}

	public static boolean hasAmmo(Player player, Predicate<ItemStack> ammoFunc) {
		return !getAmmo(player, ammoFunc).isEmpty();
	}

	public static void consumeAmmo(Player player, Predicate<ItemStack> ammoFunc) {
		ItemStack ammo = getAmmo(player, ammoFunc);
		if (!ammo.isEmpty()) {
			ammo.shrink(1);
		}
	}

	public static ItemStack getItemFromInventory(Player player, Predicate<ItemStack> itemPred) {
		for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
			ItemStack item = player.getInventory().getItem(i);
			if (itemPred.test(item)) {
				return item;
			}
		}
		return ItemStack.EMPTY;
	}

	public static ItemStack getItemClassFromInventory(Player player, Class<?> template) {
		return getItemFromInventory(player, s -> template.isAssignableFrom(s.getItem().getClass()));
	}

	public static boolean hasAdvancement(ServerPlayer player, ResourceLocation advancementId) {
		PlayerAdvancements advancements = player.getAdvancements();
		ServerAdvancementManager manager = player.level().getServer().getAdvancements();
		AdvancementHolder advancement = manager.get(advancementId);
		return advancement != null && advancements.getOrStartProgress(advancement).isDone();
	}

	public static void grantCriterion(ServerPlayer player, ResourceLocation advancementId, String criterion) {
		PlayerAdvancements advancements = player.getAdvancements();
		ServerAdvancementManager manager = player.level().getServer().getAdvancements();
		AdvancementHolder advancement = manager.get(advancementId);
		if (advancement != null && !advancements.getOrStartProgress(advancement).isDone()) {
			advancements.award(advancement, criterion);
		}
	}

	public static InteractionResult substituteUse(UseOnContext ctx, ItemStack toUse) {
		return substituteUseTrackPos(ctx, toUse).getFirst();
	}

	/**
	 * Temporarily swap <code>toUse</code> into the hand of the player, use it, then swap it back out.
	 * This is to ensure that any code in mods' onItemUse that relies on player.getHeldItem(ctx.hand) == ctx.stack
	 * will work as intended.
	 * Properly handles null players, as long as the Item's onItemUse also handles them.
	 * 
	 * @return The usage result, as well as a properly offset block position pointing at where the block was placed
	 *         (if the item was a BlockItem)
	 */
	public static Pair<InteractionResult, BlockPos> substituteUseTrackPos(UseOnContext ctx, ItemStack toUse) {
		ItemStack save = ItemStack.EMPTY;
		BlockHitResult hit = new BlockHitResult(ctx.getClickLocation(), ctx.getClickedFace(), ctx.getClickedPos(), ctx.isInside());
		UseOnContext newCtx;

		if (ctx.getPlayer() != null) {
			save = ctx.getPlayer().getItemInHand(ctx.getHand());
			ctx.getPlayer().setItemInHand(ctx.getHand(), toUse);
			// Need to construct a new one still to refresh the itemstack
			newCtx = new UseOnContext(ctx.getPlayer(), ctx.getHand(), hit);
		} else {
			newCtx = new ItemUseContextWithNullPlayer(ctx.getLevel(), ctx.getHand(), toUse, hit);
		}

		BlockPos finalPos = new BlockPlaceContext(newCtx).getClickedPos();

		InteractionResult result = toUse.useOn(newCtx);

		if (ctx.getPlayer() != null) {
			ctx.getPlayer().setItemInHand(ctx.getHand(), save);
		}

		return Pair.of(result, finalPos);
	}

	// To expose protected ctor
	private static class ItemUseContextWithNullPlayer extends UseOnContext {
		public ItemUseContextWithNullPlayer(Level world, InteractionHand hand, ItemStack stack, BlockHitResult rayTraceResult) {
			super(world, null, hand, stack, rayTraceResult);
		}
	}

	private PlayerHelper() {}
}
