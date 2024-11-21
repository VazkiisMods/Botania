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
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.advancements.UseItemSuccessTrigger;
import vazkii.botania.common.entity.BotaniaEntities;
import vazkii.botania.common.entity.PinkWitherEntity;
import vazkii.botania.common.handler.BotaniaSounds;

import java.util.List;

public class PinkinatorItem extends Item {

	public PinkinatorItem(Properties builder) {
		super(builder);
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		int range = 16;
		List<WitherBoss> withers = world.getEntitiesOfClass(WitherBoss.class, new AABB(player.getX() - range, player.getY() - range, player.getZ() - range, player.getX() + range, player.getY() + range, player.getZ() + range));
		for (WitherBoss wither : withers) {
			if (wither.isAlive() && !(wither instanceof PinkWitherEntity)) {
				if (!world.isClientSide) {
					wither.discard();
					PinkWitherEntity pink = BotaniaEntities.PINK_WITHER.create(world);
					pink.moveTo(wither.getX(), wither.getY(), wither.getZ(), wither.getYRot(), wither.getXRot());
					pink.setNoAi(wither.isNoAi());
					if (wither.hasCustomName()) {
						pink.setCustomName(wither.getCustomName());
						pink.setCustomNameVisible(wither.isCustomNameVisible());
					}
					pink.finalizeSpawn((ServerLevelAccessor) world, world.getCurrentDifficultyAt(pink.blockPosition()), MobSpawnType.CONVERSION, null, null);
					world.addFreshEntity(pink);
					pink.spawnAnim();
					pink.playSound(BotaniaSounds.pinkinator, 1F, (1F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);
					UseItemSuccessTrigger.INSTANCE.trigger((ServerPlayer) player, stack, (ServerLevel) world, player.getX(), player.getY(), player.getZ());
					stack.shrink(1);
				}
				return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
			}
		}

		return InteractionResultHolder.pass(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
		tooltip.add(Component.translatable("botaniamisc.pinkinatorDesc").withStyle(ChatFormatting.GRAY));
	}

}
