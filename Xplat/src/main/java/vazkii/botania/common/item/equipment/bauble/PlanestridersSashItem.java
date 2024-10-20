/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.bauble;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.common.helper.ItemNBTHelper;

public class PlanestridersSashItem extends SojournersSashItem {

	private static final ResourceLocation texture = ResourceLocation.parse(ResourcesLib.MODEL_SPEED_UP_BELT);

	private static final String TAG_SPEED = "speed";
	private static final String TAG_OLD_X = "oldX";
	private static final String TAG_OLD_Y = "oldY";
	private static final String TAG_OLD_Z = "oldZ";

	public PlanestridersSashItem(Properties props) {
		super(props, 0F, 0.2F, 2F);
	}

	@Override
	public ResourceLocation getRenderTexture() {
		return texture;
	}

	@Override
	public float getSpeed(ItemStack stack) {
		return ItemNBTHelper.getFloat(stack, TAG_SPEED, 0F);
	}

	@Override
	public void onMovedTick(ItemStack stack, Player player) {
		float speed = getSpeed(stack);
		float newspeed = Math.min(0.25F, speed + 0.00035F);
		ItemNBTHelper.setFloat(stack, TAG_SPEED, newspeed);
		commitPositionAndCompare(stack, player);
	}

	@Override
	public void onNotMovingTick(ItemStack stack, Player player) {
		if (!commitPositionAndCompare(stack, player)) {
			ItemNBTHelper.setFloat(stack, TAG_SPEED, 0F);
		}
	}

	public boolean commitPositionAndCompare(ItemStack stack, Player player) {
		double oldX = ItemNBTHelper.getDouble(stack, TAG_OLD_X, 0);
		double oldY = ItemNBTHelper.getDouble(stack, TAG_OLD_Y, 0);
		double oldZ = ItemNBTHelper.getDouble(stack, TAG_OLD_Z, 0);

		ItemNBTHelper.setDouble(stack, TAG_OLD_X, player.getX());
		ItemNBTHelper.setDouble(stack, TAG_OLD_Y, player.getY());
		ItemNBTHelper.setDouble(stack, TAG_OLD_Z, player.getZ());

		return Math.abs(oldX - player.getX()) > 0.001 || Math.abs(oldY - player.getY()) > 0.001 || Math.abs(oldZ - player.getZ()) > 0.001;
	}

}
