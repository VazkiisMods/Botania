/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ServerLevelAccessor;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.mixin.AbstractHorseAccessor;

public class EquestrianVirusItem extends Item {
	public EquestrianVirusItem(Properties builder) {
		super(builder);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity living, InteractionHand hand) {
		if (living.isAlive() && living instanceof Horse horse) {
			if (player.level().isClientSide) {
				return InteractionResult.SUCCESS;
			}
			if (horse.isTamed()) {
				SimpleContainer inv = ((AbstractHorseAccessor) horse).getInventory();
				ItemStack saddle = inv.getItem(0);

				// Not all AbstractHorse's have saddles in slot 0
				if (!saddle.isEmpty() && !saddle.is(Items.SADDLE)) {
					horse.spawnAtLocation(saddle, 0);
					saddle = ItemStack.EMPTY;
				}

				for (int i = 1; i < inv.getContainerSize(); i++) {
					if (!inv.getItem(i).isEmpty()) {
						horse.spawnAtLocation(inv.getItem(i), 0);
					}
				}

				horse.discard();

				AbstractHorse newHorse = stack.is(BotaniaItems.necroVirus)
						? EntityType.ZOMBIE_HORSE.create(player.level())
						: EntityType.SKELETON_HORSE.create(player.level());
				newHorse.tameWithName(player);
				newHorse.absMoveTo(horse.getX(), horse.getY(), horse.getZ(), horse.getYRot(), horse.getXRot());

				// Put the saddle back
				if (!saddle.isEmpty()) {
					SimpleContainer newInv = ((AbstractHorseAccessor) newHorse).getInventory();
					newInv.setItem(0, saddle);
				}

				ResourceLocation virusId = BotaniaAPI.botaniaRL("ermergerd_virus");

				AttributeInstance movementSpeed = newHorse.getAttribute(Attributes.MOVEMENT_SPEED);
				movementSpeed.setBaseValue(horse.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue());
				movementSpeed.addPermanentModifier(new AttributeModifier(virusId, movementSpeed.getBaseValue(), AttributeModifier.Operation.ADD_VALUE));

				AttributeInstance health = newHorse.getAttribute(Attributes.MAX_HEALTH);
				health.setBaseValue(horse.getAttribute(Attributes.MAX_HEALTH).getBaseValue());
				health.addPermanentModifier(new AttributeModifier(virusId, health.getBaseValue(), AttributeModifier.Operation.ADD_VALUE));

				AttributeInstance jumpHeight = newHorse.getAttribute(Attributes.JUMP_STRENGTH);
				jumpHeight.setBaseValue(horse.getAttribute(Attributes.JUMP_STRENGTH).getBaseValue());
				jumpHeight.addPermanentModifier(new AttributeModifier(virusId, jumpHeight.getBaseValue() * 0.5, AttributeModifier.Operation.ADD_VALUE));

				newHorse.playSound(BotaniaSounds.virusInfect, 1.0F + living.level().random.nextFloat(), living.level().random.nextFloat() * 0.7F + 1.3F);
				newHorse.finalizeSpawn((ServerLevelAccessor) player.level(), player.level().getCurrentDifficultyAt(newHorse.blockPosition()), MobSpawnType.CONVERSION, null, null);
				newHorse.setAge(horse.getAge());
				player.level().addFreshEntity(newHorse);
				newHorse.spawnAnim();

				stack.shrink(1);
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	public static boolean onLivingHurt(LivingEntity entity, DamageSource source) {
		if (entity.isPassenger() && entity.getVehicle() instanceof LivingEntity vehicle) {
			entity = vehicle;
		}

		if ((entity instanceof ZombieHorse || entity instanceof SkeletonHorse)
				&& source == entity.damageSources().fall()
				&& ((AbstractHorse) entity).isTamed()) {
			return true;
		}

		return false;
	}
}
