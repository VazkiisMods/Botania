/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.bauble;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.helper.MathHelper;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.mixin.ItemEntityAccessor;
import vazkii.botania.xplat.BotaniaConfig;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;

public class RingOfMagnetizationItem extends BaubleItem {

	private final int range;

	public RingOfMagnetizationItem(Properties props) {
		this(props, 6);
	}

	public RingOfMagnetizationItem(Properties props, int range) {
		super(props);
		this.range = range;
	}

	public static void onTossItem(Player player) {
		ItemStack ring = EquipmentHandler.findOrEmpty(s -> s.getItem() instanceof RingOfMagnetizationItem, player);
		if (!ring.isEmpty()) {
			player.getCooldowns().addCooldown(ring.getItem(), 100);
		}
	}

	@Override
	public void onWornTick(ItemStack stack, LivingEntity living) {
		super.onWornTick(stack, living);

		if (living.isSpectator()) {
			return;
		}

		if (living instanceof Player player) {
			boolean isOnCooldown = player.getCooldowns().isOnCooldown(this);
			if (BotaniaAPI.instance().hasSolegnoliaAround(living)) {
				if (!isOnCooldown) {
					player.getCooldowns().addCooldown(this, 2);
				}
				return;
			}

			if (!isOnCooldown) {
				if (living.isShiftKeyDown() == BotaniaConfig.common().invertMagnetRing()) {
					double x = living.getX();
					double y = living.getY() + 0.75;
					double z = living.getZ();

					int range = ((RingOfMagnetizationItem) stack.getItem()).range;
					List<ItemEntity> items = living.level().getEntitiesOfClass(ItemEntity.class, new AABB(x - range, y - range, z - range, x + range, y + range, z + range));
					int pulled = 0;
					for (ItemEntity item : items) {
						if (((RingOfMagnetizationItem) stack.getItem()).canPullItem(item)) {
							if (pulled > 200) {
								break;
							}

							MathHelper.setEntityMotionFromVector(item, new Vec3(x, y, z), 0.45F);
							if (living.level().isClientSide) {
								boolean red = living.level().random.nextBoolean();
								float r = red ? 1F : 0F;
								float b = red ? 0F : 1F;
								SparkleParticleData data = SparkleParticleData.sparkle(1F, r, 0F, b, 3);
								living.level().addParticle(data, item.getX(), item.getY(), item.getZ(), 0, 0, 0);
							}
							pulled++;
						}
					}
				}
			}
		}
	}

	private boolean canPullItem(ItemEntity item) {
		int pickupDelay = ((ItemEntityAccessor) item).getPickupDelay();
		if (!item.isAlive() || pickupDelay >= 40
				|| BotaniaAPI.instance().hasSolegnoliaAround(item)
				|| XplatAbstractions.INSTANCE.preventsRemoteMovement(item)) {
			return false;
		}

		ItemStack stack = item.getItem();
		if (stack.isEmpty()
				|| XplatAbstractions.INSTANCE.findManaItem(stack) != null
				|| XplatAbstractions.INSTANCE.findRelic(stack) != null
				|| stack.is(BotaniaTags.Items.MAGNET_RING_BLACKLIST)) {
			return false;
		}

		BlockPos pos = item.blockPosition();

		if (item.level().getBlockState(pos).is(BotaniaTags.Blocks.MAGNET_RING_BLACKLIST)) {
			return false;
		}

		if (item.level().getBlockState(pos.below()).is(BotaniaTags.Blocks.MAGNET_RING_BLACKLIST)) {
			return false;
		}

		return true;
	}

}
