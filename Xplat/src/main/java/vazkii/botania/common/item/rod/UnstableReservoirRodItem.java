/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.rod;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.block.Avatar;
import vazkii.botania.api.item.AvatarWieldable;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.common.entity.BotaniaEntities;
import vazkii.botania.common.entity.MagicMissileEntity;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.xplat.XplatAbstractions;

public class UnstableReservoirRodItem extends Item {

	private static final ResourceLocation avatarOverlay = ResourceLocation.parse(ResourcesLib.MODEL_AVATAR_MISSILE);

	private static final int COST_PER = 120;
	private static final int COST_AVATAR = 40;

	public UnstableReservoirRodItem(Properties props) {
		super(props);
	}

	@NotNull
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public void onUseTick(Level world, LivingEntity living, ItemStack stack, int count) {
		if (!(living instanceof Player player)) {
			return;
		}

		if (count != getUseDuration(stack) && count % (ManaItemHandler.instance().hasProficiency(player, stack) ? 1 : 2) == 0 && ManaItemHandler.instance().requestManaExactForTool(stack, player, COST_PER, false)) {
			if (!world.isClientSide && spawnMissile(world, player, player.getX() + (Math.random() - 0.5 * 0.1), player.getY() + 2.4 + (Math.random() - 0.5 * 0.1), player.getZ() + (Math.random() - 0.5 * 0.1))) {
				ManaItemHandler.instance().requestManaExactForTool(stack, player, COST_PER, true);
			}

			SparkleParticleData data = SparkleParticleData.sparkle(6F, 1F, 0.4F, 1F, 6);
			world.addParticle(data, player.getX(), player.getY() + 2.4, player.getZ(), 0, 0, 0);
		}
	}

	public static boolean spawnMissile(Level world, LivingEntity thrower, double x, double y, double z) {
		MagicMissileEntity missile;
		if (thrower != null) {
			missile = new MagicMissileEntity(thrower, false);
		} else {
			missile = BotaniaEntities.MAGIC_MISSILE.create(world);
		}

		missile.setPos(x, y, z);
		if (missile.findTarget()) {
			if (!world.isClientSide) {
				missile.playSound(world.random.nextInt(100) == 0 ? BotaniaSounds.missileFunny : BotaniaSounds.missile, 1F, 0.8F + (float) Math.random() * 0.2F);
				world.addFreshEntity(missile);
			}

			return true;
		}
		return false;
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		return ItemUtils.startUsingInstantly(world, player, hand);
	}

	public static class AvatarBehavior implements AvatarWieldable {
		@Override
		public void onAvatarUpdate(Avatar tile) {
			BlockEntity te = (BlockEntity) tile;
			Level world = te.getLevel();
			BlockPos pos = te.getBlockPos();
			ManaReceiver receiver = XplatAbstractions.INSTANCE.findManaReceiver(world, te.getBlockPos(), te.getBlockState(), te, null);
			if (receiver != null && receiver.getCurrentMana() >= COST_AVATAR && tile.getElapsedFunctionalTicks() % 3 == 0 && tile.isEnabled()) {
				if (spawnMissile(world, null, pos.getX() + 0.5 + (Math.random() - 0.5 * 0.1), pos.getY() + 2.5 + (Math.random() - 0.5 * 0.1), pos.getZ() + (Math.random() - 0.5 * 0.1))) {
					if (!world.isClientSide) {
						receiver.receiveMana(-COST_AVATAR);
					}
					SparkleParticleData data = SparkleParticleData.sparkle(6F, 1F, 0.4F, 1F, 6);
					world.addParticle(data, pos.getX() + 0.5, pos.getY() + 2.5, pos.getZ() + 0.5, 0, 0, 0);
				}
			}
		}

		@Override
		public ResourceLocation getOverlayResource(Avatar tile) {
			return avatarOverlay;
		}
	}
}
