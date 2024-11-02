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
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.block.Avatar;
import vazkii.botania.api.item.AvatarWieldable;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.block_entity.BifrostBlockEntity;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.item.material.SelfReturningItem;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;

public class BifrostRodItem extends SelfReturningItem {

	private static final ResourceLocation avatarOverlay = ResourceLocation.parse(ResourcesLib.MODEL_AVATAR_RAINBOW);

	private static final int MANA_COST = 750;
	private static final int MANA_COST_AVATAR = 4;
	private static final int TIME = 600;

	public BifrostRodItem(Properties props) {
		super(props);
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!world.isClientSide && ManaItemHandler.instance().requestManaExactForTool(stack, player, MANA_COST, false)) {
			BlockState bifrost = BotaniaBlocks.bifrost.defaultBlockState();
			Vec3 vector = player.getLookAngle().normalize();

			double x = player.getX();
			double y = player.getY() - 1;
			double z = player.getZ();
			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos((int) x, (int) y, (int) z);

			double lastX = 0;
			double lastY = -1;
			double lastZ = 0;
			BlockPos.MutableBlockPos previousPos = new BlockPos.MutableBlockPos();

			int count = 0;
			boolean placedAny = false;

			boolean prof = ManaItemHandler.instance().hasProficiency(player, stack);
			int maxlen = prof ? 160 : 100;
			int time = prof ? (int) (TIME * 1.6) : TIME;

			BlockPos.MutableBlockPos placePos = new BlockPos.MutableBlockPos();

			while (count < maxlen) {
				previousPos.set(lastX, lastY, lastZ);

				if (!previousPos.equals(pos)) { // Occasionally moving to the next segment stays on the same location, skip it
					if (!world.isEmptyBlock(pos) && world.getBlockState(pos) != bifrost && count >= 4) {
						break; // Stop placing if you hit a wall (bifrost blocks are fine), but only after 4 segments.
					}
					if (world.isOutsideBuildHeight(pos.getY())) {
						break;
					}
					if (placeBridgeSegment(world, pos, placePos, time)) {
						placedAny = true;
					}
				}

				count++;

				lastX = x;
				lastY = y;
				lastZ = z;

				x += vector.x;
				y += vector.y;
				z += vector.z;
				pos.set(x, y, z);
			}

			if (placedAny) {
				world.playSound(null, player.getX(), player.getY(), player.getZ(), BotaniaSounds.bifrostRod, SoundSource.PLAYERS, 1F, 1F);
				player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
				ManaItemHandler.instance().requestManaExactForTool(stack, player, MANA_COST, false);
				player.getCooldowns().addCooldown(this, player.isCreative() ? 10 : TIME);
			}
		}

		return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
	}

	private static boolean placeBridgeSegment(Level world, BlockPos center, BlockPos.MutableBlockPos placePos, int time) {
		BlockState bifrost = BotaniaBlocks.bifrost.defaultBlockState();
		boolean placed = false;

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				placePos.set(center.getX() + i, center.getY(), center.getZ() + j);
				if (world.isEmptyBlock(placePos) || world.getBlockState(placePos) == bifrost) {
					world.setBlock(placePos, bifrost, Block.UPDATE_CLIENTS);

					BifrostBlockEntity tile = (BifrostBlockEntity) world.getBlockEntity(placePos);
					if (tile != null) {
						tile.ticks = time;
						placed = true;
					}
				}
			}
		}
		return placed;
	}

	public static class AvatarBehavior implements AvatarWieldable {
		@Override
		public void onAvatarUpdate(Avatar tile) {
			BlockEntity te = (BlockEntity) tile;
			Level world = te.getLevel();
			ManaReceiver receiver = XplatAbstractions.INSTANCE.findManaReceiver(world, te.getBlockPos(), te.getBlockState(), te, null);

			if (world.isClientSide || receiver.getCurrentMana() < MANA_COST_AVATAR * 25
					|| !tile.isEnabled() || world.isOutsideBuildHeight(te.getBlockPos().getY() - 1)) {
				return;
			}

			BlockPos tePos = te.getBlockPos();
			int w = 1;
			int h = 1;
			int l = 20;

			AABB axis = null;
			switch (world.getBlockState(tePos).getValue(BlockStateProperties.HORIZONTAL_FACING)) {
				case NORTH -> axis = AABB.encapsulatingFullBlocks(tePos.offset(-w, -h, -l), tePos.offset(w + 1, h, 0));
				case SOUTH -> axis = AABB.encapsulatingFullBlocks(tePos.offset(-w, -h, 1), tePos.offset(w + 1, h, l + 1));
				case WEST -> axis = AABB.encapsulatingFullBlocks(tePos.offset(-l, -h, -w), tePos.offset(0, h, w + 1));
				case EAST -> axis = AABB.encapsulatingFullBlocks(tePos.offset(1, -h, -w), tePos.offset(l + 1, h, w + 1));
				default -> {}
			}

			List<Player> players = world.getEntitiesOfClass(Player.class, axis);
			for (Player p : players) {
				int px = Mth.floor(p.getX());
				int py = Mth.floor(p.getY()) - 1;
				int pz = Mth.floor(p.getZ());
				int dist = 5;
				int diff = dist / 2;

				for (int i = 0; i < dist; i++) {
					for (int j = 0; j < dist; j++) {
						int ex = px + i - diff;
						int ez = pz + j - diff;

						if (!axis.contains(new Vec3(ex + 0.5, py + 1, ez + 0.5))) {
							continue;
						}
						BlockPos pos = new BlockPos(ex, py, ez);
						BlockState state = world.getBlockState(pos);
						if (state.isAir()) {
							if (world.setBlockAndUpdate(pos, BotaniaBlocks.bifrost.defaultBlockState())) {
								BifrostBlockEntity bifrostBlockEntity = (BifrostBlockEntity) world.getBlockEntity(pos);
								bifrostBlockEntity.ticks = 10;
								receiver.receiveMana(-MANA_COST_AVATAR);
							}
						} else if (state.is(BotaniaBlocks.bifrost)) {
							BifrostBlockEntity bifrostBlockEntity = (BifrostBlockEntity) world.getBlockEntity(pos);
							if (bifrostBlockEntity.ticks < 2) {
								bifrostBlockEntity.ticks += 10;
								receiver.receiveMana(-MANA_COST_AVATAR);
							}
						}
					}
				}
			}

		}

		@Override
		public ResourceLocation getOverlayResource(Avatar tile) {
			return avatarOverlay;
		}
	}

}
