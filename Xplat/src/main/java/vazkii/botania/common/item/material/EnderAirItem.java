/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.material;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.entity.EnderAirBottleEntity;
import vazkii.botania.common.entity.EnderAirEntity;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.item.BotaniaItems;

import java.util.List;

public class EnderAirItem extends Item implements ProjectileItem {
	public EnderAirItem(Properties props) {
		super(props);
	}

	public static InteractionResultHolder<ItemStack> onPlayerInteract(Player player, Level world, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (stack.isEmpty() || !stack.is(Items.GLASS_BOTTLE)) {
			return InteractionResultHolder.pass(stack);
		}

		if ((world.dimension() == Level.END && isClearFromDragonBreath(world, player.getBoundingBox().inflate(3.5)) && notAimingAtFluid(world, player))
				|| pickupFromEntity(world, player.getBoundingBox().inflate(1.0))) {

			if (!world.isClientSide) {
				ItemStack enderAir = new ItemStack(BotaniaItems.enderAirBottle);
				player.getInventory().placeItemBackInInventory(enderAir);
				stack.shrink(1);
				world.playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.5F, 1F);
				world.gameEvent(player, GameEvent.FLUID_PICKUP, player.position());
			}

			return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
		}

		return InteractionResultHolder.pass(stack);
	}

	private static boolean notAimingAtFluid(Level world, Player player) {
		BlockHitResult hitResult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.ANY);
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			BlockPos pos = hitResult.getBlockPos();
			return world.mayInteract(player, pos) && world.getFluidState(pos).isEmpty();
		}
		return true;
	}

	public static boolean isClearFromDragonBreath(Level world, AABB aabb) {
		List<AreaEffectCloud> list = world.getEntitiesOfClass(AreaEffectCloud.class,
				aabb, entity -> entity != null && entity.isAlive()
						&& entity.getParticle().getType() == ParticleTypes.DRAGON_BREATH);
		return list.isEmpty();
	}

	public static boolean pickupFromEntity(Level level, AABB area) {
		var entities = level.getEntitiesOfClass(EnderAirEntity.class, area, EntitySelector.ENTITY_STILL_ALIVE);
		if (!entities.isEmpty()) {
			entities.get(0).discard();
			return true;
		}
		return false;
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!player.getAbilities().instabuild) {
			stack.shrink(1);
		}

		world.playSound(null, player.getX(), player.getY(), player.getZ(), BotaniaSounds.enderAirThrow, SoundSource.PLAYERS, 1F, 0.4F / (player.getRandom().nextFloat() * 0.4F + 0.8F));

		if (!world.isClientSide) {
			EnderAirBottleEntity b = new EnderAirBottleEntity(player, world);
			b.shootFromRotation(player, player.getXRot(), player.getYRot(), 0F, 1.5F, 1F);
			world.addFreshEntity(b);
		}
		return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
	}

	@Override
	public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
		return new EnderAirBottleEntity(pos.x(), pos.y(), pos.z(), level);
	}
}
