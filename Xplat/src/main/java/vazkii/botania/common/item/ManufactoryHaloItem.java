/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.ItemNBTHelper;

import java.util.List;

public class ManufactoryHaloItem extends AssemblyHaloItem {
	public static final String TAG_ACTIVE = "active";

	private static final ResourceLocation glowTexture = ResourceLocation.parse(ResourcesLib.MISC_GLOW_CYAN);

	public ManufactoryHaloItem(Item.Properties props) {
		super(props);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int pos, boolean equipped) {
		super.inventoryTick(stack, world, entity, pos, equipped);

		if (!world.isClientSide && entity instanceof Player player && !equipped && isActive(stack)) {

			for (int i = 1; i < SEGMENTS; i++) {
				tryCraft(player, stack, i, false);
			}
		}
	}

	@Override
	public ResourceLocation getGlowResource(ItemStack stack) {
		return isActive(stack) ? glowTexture : super.getGlowResource(stack);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, TooltipContext context, @NotNull List<Component> stacks, @NotNull TooltipFlag flags) {
		if (isActive(stack)) {
			stacks.add(Component.translatable("botaniamisc.active"));
		} else {
			stacks.add(Component.translatable("botaniamisc.inactive"));
		}
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (getSegmentLookedAt(stack, player) == 0 && player.isSecondaryUseActive()) {
			togglePassive(stack, player, world);
			return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
		}

		return super.use(world, player, hand);
	}

	@Override
	public boolean overrideOtherStackedOnMe(@NotNull ItemStack stack, @NotNull ItemStack cursor, @NotNull Slot slot,
			@NotNull ClickAction click, Player player, @NotNull SlotAccess access) {
		Level world = player.level();
		if (click == ClickAction.SECONDARY && slot.allowModification(player) && cursor.isEmpty()) {
			togglePassive(stack, player, world);
			access.set(cursor);
			return true;
		}
		return false;
	}

	private void togglePassive(ItemStack stack, LivingEntity living, Level world) {
		ItemNBTHelper.setBoolean(stack, TAG_ACTIVE, !isActive(stack));
		if (living instanceof Player player && world != null) {
			world.playSound(player, player.getX(), player.getY(), player.getZ(), BotaniaSounds.manufactoryHaloConfigure, SoundSource.NEUTRAL, 1F, 1F);
		}
	}

	private static boolean isActive(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_ACTIVE, true);
	}
}
