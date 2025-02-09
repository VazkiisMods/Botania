/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.relic;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.item.Relic;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.entity.BabylonWeaponEntity;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.helper.VecHelper;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class KeyOfTheKingsLawItem extends RelicItem {

	private static final String TAG_WEAPONS_SPAWNED = "weaponsSpawned";
	private static final String TAG_CHARGING = "charging";

	public static final int WEAPON_TYPES = 12;

	public KeyOfTheKingsLawItem(Properties props) {
		super(props);
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		setCharging(stack, true);
		return ItemUtils.startUsingInstantly(world, player, hand);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level world, LivingEntity living, int time) {
		int spawned = getWeaponsSpawned(stack);
		if (spawned == 20) {
			setCharging(stack, false);
			setWeaponsSpawned(stack, 0);
		}
	}

	@Override
	public void onUseTick(Level world, LivingEntity living, ItemStack stack, int count) {
		int spawned = getWeaponsSpawned(stack);

		if (count != getUseDuration(stack, living) && spawned < 20 && !world.isClientSide && (!(living instanceof Player player) || ManaItemHandler.instance().requestManaExact(stack, player, 150, true))) {
			Vec3 look = living.getLookAngle().multiply(1, 0, 1);

			double playerRot = Math.toRadians(living.getYRot() + 90);
			if (look.x == 0 && look.z == 0) {
				look = new Vec3(Math.cos(playerRot), 0, Math.sin(playerRot));
			}

			look = look.normalize().scale(-2);

			int div = spawned / 5;
			int mod = spawned % 5;

			Vec3 pl = look.add(VecHelper.fromEntityCenter(living)).add(0, 1.6, div * 0.1);

			var rand = world.random;
			Vec3 axis = look.normalize().cross(new Vec3(-1, 0, -1)).normalize();

			double rot = mod * Math.PI / 4 - Math.PI / 2;

			Vec3 axis1 = VecHelper.rotate(axis.scale(div * 3.5 + 5), rot, look);
			if (axis1.y < 0) {
				axis1 = axis1.multiply(1, -1, 1);
			}

			Vec3 end = pl.add(axis1);

			BabylonWeaponEntity weapon = new BabylonWeaponEntity(living, world);
			weapon.setPos(end.x, end.y, end.z);
			weapon.setYRot(living.getYRot());
			weapon.setVariety(rand.nextInt(WEAPON_TYPES));
			weapon.setDelay(spawned);
			weapon.setRotation(Mth.wrapDegrees(-living.getYRot() + 180));

			world.addFreshEntity(weapon);
			weapon.playSound(BotaniaSounds.babylonSpawn, 1F, 1F + world.random.nextFloat() * 3F);
			setWeaponsSpawned(stack, spawned + 1);
		}
	}

	@NotNull
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 72000;
	}

	public static boolean isCharging(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_CHARGING, false);
	}

	public static int getWeaponsSpawned(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_WEAPONS_SPAWNED, 0);
	}

	public static void setCharging(ItemStack stack, boolean charging) {
		ItemNBTHelper.setBoolean(stack, TAG_CHARGING, charging);
	}

	public static void setWeaponsSpawned(ItemStack stack, int count) {
		ItemNBTHelper.setInt(stack, TAG_WEAPONS_SPAWNED, count);
	}

	public static Relic makeRelic(ItemStack stack) {
		return new RelicImpl(stack, botaniaRL("challenge/king_key"));
	}
}
