/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.rod;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.annotations.SoftImplement;
import vazkii.botania.common.entity.ThrownItemEntity;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.helper.MathHelper;
import vazkii.botania.common.helper.VecHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.lib.BotaniaTags;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ShadedMesaRodItem extends Item {
	private static final TagKey<EntityType<?>> BLACKLIST = BotaniaTags.Entities.SHADED_MESA_BLACKLIST;
	private static final float RANGE = 3F;
	private static final int COST = 2;
	private static final Predicate<Entity> CAN_TARGET = e -> !e.isSpectator() && e.isAlive() && !e.getType().is(BLACKLIST);

	private static final String TAG_TICKS_TILL_EXPIRE = "ticksTillExpire";
	private static final String TAG_TICKS_COOLDOWN = "ticksCooldown";
	private static final String TAG_TARGET = "target";
	private static final String TAG_DIST = "dist";

	public ShadedMesaRodItem(Properties props) {
		super(props);
	}

	@SoftImplement("IItemExtension")
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return reequipAnimation(oldStack, newStack);
	}

	@SoftImplement("FabricItem")
	public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
		return reequipAnimation(oldStack, newStack);
	}

	private boolean reequipAnimation(ItemStack before, ItemStack after) {
		return !after.is(this);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean held) {
		if (!(entity instanceof Player)) {
			return;
		}

		int ticksTillExpire = ItemNBTHelper.getInt(stack, TAG_TICKS_TILL_EXPIRE, 0);
		int ticksCooldown = ItemNBTHelper.getInt(stack, TAG_TICKS_COOLDOWN, 0);

		if (ticksTillExpire == 0) {
			ItemNBTHelper.setInt(stack, TAG_TARGET, -1);
			ItemNBTHelper.setDouble(stack, TAG_DIST, -1);
		}

		if (ticksCooldown > 0) {
			ticksCooldown--;
		}

		if (ticksTillExpire >= 0) {
			ticksTillExpire--;
		}
		ItemNBTHelper.setInt(stack, TAG_TICKS_TILL_EXPIRE, ticksTillExpire);
		ItemNBTHelper.setInt(stack, TAG_TICKS_COOLDOWN, ticksCooldown);
	}

	@SoftImplement("IItemExtension")
	public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
		if (entity instanceof Player player) {
			leftClick(player);
		}
		return false;
	}

	// Prevent damaging the entity you just held with the rod
	@SoftImplement("IItemExtension")
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
		return ItemNBTHelper.getInt(stack, TAG_TICKS_TILL_EXPIRE, 0) != 0;
	}

	// Calls hook above on Fabric
	public static InteractionResult onAttack(Player player, Level level, InteractionHand hand, Entity target, @Nullable EntityHitResult hit) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.is(BotaniaItems.gravityRod) && ((ShadedMesaRodItem) stack.getItem()).onLeftClickEntity(stack, player, target)) {
			return InteractionResult.FAIL;
		}
		return InteractionResult.PASS;
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		int targetID = ItemNBTHelper.getInt(stack, TAG_TARGET, -1);
		int ticksCooldown = ItemNBTHelper.getInt(stack, TAG_TICKS_COOLDOWN, 0);
		double length = ItemNBTHelper.getDouble(stack, TAG_DIST, -1);

		if (ticksCooldown == 0) {
			Entity target = null;
			if (targetID != -1 && player.level().getEntity(targetID) != null) {
				Entity taritem = player.level().getEntity(targetID);

				boolean found = false;
				Vec3 targetVec = VecHelper.fromEntityCenter(player);
				List<Entity> entities = new ArrayList<>();
				int distance = 1;
				while (entities.size() == 0 && distance < 25) {
					targetVec = targetVec.add(player.getLookAngle().scale(distance)).add(0, 0.5, 0);
					entities = player.level().getEntities(player, VecHelper.boxForRange(targetVec, RANGE), CAN_TARGET);
					distance++;
					if (entities.contains(taritem)) {
						found = true;
					}
				}

				if (found) {
					target = player.level().getEntity(targetID);
				}
			}

			if (target == null) {
				Vec3 targetVec = VecHelper.fromEntityCenter(player);
				List<Entity> entities = new ArrayList<>();
				int distance = 1;
				while (entities.size() == 0 && distance < 25) {
					targetVec = targetVec.add(player.getLookAngle().scale(distance)).add(0, 0.5, 0);
					entities = player.level().getEntities(player, VecHelper.boxForRange(targetVec, RANGE), CAN_TARGET);
					distance++;
				}

				if (entities.size() > 0) {
					target = entities.get(0);
					length = 5.5D;
					if (target instanceof ItemEntity) {
						length = 2.0D;
					}
				}
			}

			if (target != null) {
				if (target.getType().is(BLACKLIST)) {
					return InteractionResultHolder.fail(stack);
				}

				if (ManaItemHandler.instance().requestManaExactForTool(stack, player, COST, true)) {
					boolean targetIsPlayer = target instanceof Player;

					if (target instanceof ItemEntity item) {
						item.setPickUpDelay(5);
					}

					if (target instanceof LivingEntity living) {
						living.fallDistance = 0.0F;
						if (living.getEffect(MobEffects.MOVEMENT_SLOWDOWN) == null) {
							living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
									targetIsPlayer ? 20 : 2, targetIsPlayer ? 1 : 3, true, true));
						}
					}

					Vec3 target3 = VecHelper.fromEntityCenter(player)
							.add(player.getLookAngle().scale(length)).add(0, 0.5, 0);
					if (target instanceof ItemEntity) {
						target3 = target3.add(0, 0.25, 0);
					}

					for (int i = 0; i < 4; i++) {
						float r = 0.5F + (float) Math.random() * 0.5F;
						float b = 0.5F + (float) Math.random() * 0.5F;
						float s = 0.2F + (float) Math.random() * 0.1F;
						float m = 0.1F;
						float xm = ((float) Math.random() - 0.5F) * m;
						float ym = ((float) Math.random() - 0.5F) * m;
						float zm = ((float) Math.random() - 0.5F) * m;
						WispParticleData data = WispParticleData.wisp(s, r, 0F, b);
						world.addParticle(data, target.getX() + target.getBbWidth() / 2, target.getY() + target.getBbHeight() / 2, target.getZ() + target.getBbWidth() / 2, xm, ym, zm);
					}

					MathHelper.setEntityMotionFromVector(target, target3, 0.3333333F);
					if (targetIsPlayer && target instanceof ServerPlayer p) {
						p.connection.send(new ClientboundSetEntityMotionPacket(p));
					}

					ItemNBTHelper.setInt(stack, TAG_TARGET, target.getId());
					ItemNBTHelper.setDouble(stack, TAG_DIST, length);
				}

				ItemNBTHelper.setInt(stack, TAG_TICKS_TILL_EXPIRE, 5);
				return InteractionResultHolder.consume(stack);
			}
		}
		return InteractionResultHolder.pass(stack);
	}

	private static void leftClick(Player player) {
		ItemStack stack = player.getMainHandItem();
		if (!stack.isEmpty() && stack.is(BotaniaItems.gravityRod)) {
			int targetID = ItemNBTHelper.getInt(stack, TAG_TARGET, -1);
			ItemNBTHelper.getDouble(stack, TAG_DIST, -1);

			if (targetID != -1 && player.level().getEntity(targetID) != null) {
				Entity target = player.level().getEntity(targetID);

				boolean found = false;
				Vec3 vec = VecHelper.fromEntityCenter(player);
				List<Entity> entities = new ArrayList<>();
				int distance = 1;
				while (entities.size() == 0 && distance < 25) {
					vec = vec.add(player.getLookAngle().scale(distance)).add(0, 0.5, 0);
					entities = player.level().getEntities(player, new AABB(vec.subtract(RANGE, RANGE, RANGE), vec.add(RANGE, RANGE, RANGE)), CAN_TARGET);
					distance++;
					if (entities.contains(target)) {
						found = true;
					}
				}

				if (found) {
					ItemNBTHelper.setInt(stack, TAG_TARGET, -1);
					ItemNBTHelper.setDouble(stack, TAG_DIST, -1);
					Vec3 moveVector = player.getLookAngle().normalize();
					if (target instanceof ItemEntity item) {
						item.setPickUpDelay(20);
						float mot = ManaItemHandler.instance().hasProficiency(player, stack) ? 2.25F : 1.5F;
						item.setDeltaMovement(moveVector.x * mot, moveVector.y, moveVector.z * mot);
						if (!player.level().isClientSide) {
							ThrownItemEntity thrown = new ThrownItemEntity(item.level(), item.getX(), item.getY(), item.getZ(), item);
							item.level().addFreshEntity(thrown);
						}
						item.discard();
					} else {
						if (target instanceof LivingEntity living) {
							living.setLastHurtByMob(player);
							living.setLastHurtByPlayer(player);
						}
						target.setDeltaMovement(moveVector.multiply(3, 1.5, 3));
					}
					ItemNBTHelper.setInt(stack, TAG_TICKS_COOLDOWN, 10);
				}
			}
		}
	}
}
