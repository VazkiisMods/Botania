/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.entity.VineBallEntity;
import vazkii.botania.common.handler.BotaniaSounds;

public class VineBallItem extends Item implements ProjectileItem {

	public VineBallItem(Properties builder) {
		super(builder);
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		if (!player.getAbilities().instabuild) {
			player.getItemInHand(hand).shrink(1);
		}

		world.playSound(null, player.getX(), player.getY(), player.getZ(), BotaniaSounds.vineBallThrow, SoundSource.NEUTRAL, 1F, 0.4F / (player.getRandom().nextFloat() * 0.4F + 0.8F));

		if (!world.isClientSide) {
			VineBallEntity ball = new VineBallEntity(player, true);
			ball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
			world.addFreshEntity(ball);
		}

		return InteractionResultHolder.success(player.getItemInHand(hand));
	}

	@Override
	public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
		return new VineBallEntity(pos.x(), pos.y(), pos.z(), level);
	}
}
