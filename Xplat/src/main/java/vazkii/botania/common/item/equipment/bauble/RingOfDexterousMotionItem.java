/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.bauble;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.network.serverbound.DodgePacket;
import vazkii.botania.xplat.ClientXplatAbstractions;

public class RingOfDexterousMotionItem extends BaubleItem {

	public static final int MAX_CD = 20;

	private static boolean oldLeftDown, oldRightDown, oldForwardDown, oldBackDown;
	private static int leftDown, rightDown, forwardDown, backDown;

	public RingOfDexterousMotionItem(Properties props) {
		super(props);
	}

	public static class ClientLogic {
		public static void onKeyDown() {
			Minecraft mc = Minecraft.getInstance();
			if (mc.player == null) {
				return;
			}

			ItemStack ringStack = EquipmentHandler.findOrEmpty(BotaniaItems.dodgeRing, mc.player);
			if (ringStack.isEmpty() || mc.player.getCooldowns().isOnCooldown(ringStack.getItem())) {
				return;
			}

			int threshold = 5;
			if (mc.options.keyLeft.isDown() && !oldLeftDown) {
				int oldLeft = leftDown;
				leftDown = ClientTickHandler.ticksInGame;

				if (leftDown - oldLeft < threshold) {
					dodge(mc.player, Direction.WEST);
				}
			} else if (mc.options.keyRight.isDown() && !oldRightDown) {
				int oldRight = rightDown;
				rightDown = ClientTickHandler.ticksInGame;

				if (rightDown - oldRight < threshold) {
					dodge(mc.player, Direction.EAST);
				}
			} else if (mc.options.keyUp.isDown() && !oldForwardDown) {
				int oldForward = forwardDown;
				forwardDown = ClientTickHandler.ticksInGame;

				if (forwardDown - oldForward < threshold) {
					dodge(mc.player, Direction.NORTH);
				}
			} else if (mc.options.keyDown.isDown() && !oldBackDown) {
				int oldBack = backDown;
				backDown = ClientTickHandler.ticksInGame;

				if (backDown - oldBack < threshold) {
					dodge(mc.player, Direction.SOUTH);
				}
			}

			oldLeftDown = mc.options.keyLeft.isDown();
			oldRightDown = mc.options.keyRight.isDown();
			oldForwardDown = mc.options.keyUp.isDown();
			oldBackDown = mc.options.keyDown.isDown();
		}

		public static void renderHUD(GuiGraphics gui, Player player, ItemStack stack, float pticks) {
			int xo = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - 20;
			int y = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 + 20;

			if (!player.getAbilities().flying) {
				int width = (int) (player.getCooldowns().getCooldownPercent(stack.getItem(), pticks) * 40);
				RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
				if (width > 0) {
					gui.fill(xo, y - 2, xo + 40, y - 1, 0x88000000);
					gui.fill(xo, y - 2, xo + width, y - 1, 0xFFFFFFFF);
				}
			}

			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		}
	}

	private static void dodge(Player player, Direction dir) {
		if (player.getAbilities().flying || !player.onGround() || dir == Direction.UP || dir == Direction.DOWN) {
			return;
		}

		float yaw = player.getYRot();
		float x = Mth.sin(-yaw * 0.017453292F - (float) Math.PI);
		float z = Mth.cos(-yaw * 0.017453292F - (float) Math.PI);
		if (dir == Direction.NORTH || dir == Direction.SOUTH) {
			x = Mth.cos(-yaw * 0.017453292F);
			z = Mth.sin(yaw * 0.017453292F);
		}
		Vec3 lookVec = new Vec3(x, 0, z);
		Vec3 sideVec = lookVec.cross(new Vec3(0, dir == Direction.WEST || dir == Direction.NORTH ? 1 : (dir == Direction.EAST || dir == Direction.SOUTH ? -1 : 0), 0)).scale(1.25);

		player.setDeltaMovement(sideVec);

		ClientXplatAbstractions.INSTANCE.sendToServer(DodgePacket.INSTANCE);
	}
}
