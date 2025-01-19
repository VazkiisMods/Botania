/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.saveddata.SavedData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.ForcePushHelper;
import vazkii.botania.common.item.WandOfTheForestItem;
import vazkii.botania.common.item.lens.ForceLens;
import vazkii.botania.network.EffectType;
import vazkii.botania.network.clientbound.BotaniaEffectPacket;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.*;

public class ForceRelayBlock extends BotaniaBlock {

	public final Map<UUID, GlobalPos> activeBindingAttempts = new HashMap<>();

	public ForceRelayBlock(Properties builder) {
		super(builder.pushReaction(PushReaction.PUSH_ONLY));
	}

	@Override
	public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
		if (!world.isClientSide) {
			var data = WorldData.get(world);

			Direction movementContextDirection = ForcePushHelper.getMovementContextDirection();
			if (isMoving && (movementContextDirection != null || newState.is(Blocks.MOVING_PISTON))) {
				var pistonDirection = movementContextDirection != null
						? movementContextDirection
						: newState.getValue(MovingPistonBlock.FACING);
				// if being moved as part of a retracting sticky piston's block structure, reverse movement direction
				var moveDirection = ForcePushHelper.isExtendingMovementContext() ? pistonDirection : pistonDirection.getOpposite();

				var destPos = data.mapping.get(pos);
				if (destPos != null) {
					BlockPos newSrcPos = pos.relative(moveDirection);

					{
						// Move source side of our binding along
						data.mapping.remove(pos);
						data.mapping.put(newSrcPos, destPos);
						data.setDirty();
					}

					if (!newState.is(Blocks.MOVING_PISTON) || newState.getValue(MovingPistonBlock.TYPE) == PistonType.DEFAULT) {
						// Move the actual bound blocks
						if (ForceLens.moveBlocks(world, destPos.relative(moveDirection.getOpposite()), moveDirection, pos)) {
							// Move dest side of our binding
							data.mapping.put(newSrcPos, data.mapping.get(newSrcPos).relative(moveDirection));
						}
					}
				}
			} else {
				if (data.mapping.remove(pos) != null) {
					data.setDirty();
				}
			}
		}
	}

	public boolean onUsedByWand(@Nullable Player player, ItemStack stack, Level world, BlockPos pos) {
		if (world.isClientSide) {
			return false;
		}

		if (player == null || player.isShiftKeyDown()) {
			world.destroyBlock(pos, true);
		} else {
			GlobalPos clicked = GlobalPos.of(world.dimension(), pos.immutable());
			if (WandOfTheForestItem.getBindMode(stack)) {
				activeBindingAttempts.put(player.getUUID(), clicked);
				world.playSound(null, pos, BotaniaSounds.ding, SoundSource.BLOCKS, 0.5F, 1F);
			} else {
				var data = WorldData.get(world);
				if (XplatAbstractions.INSTANCE.isDevEnvironment()) {
					BotaniaAPI.LOGGER.info("PistonRelay pairs");
					for (var e : data.mapping.entrySet()) {
						BotaniaAPI.LOGGER.info("{} -> {}", e.getKey(), e.getValue());
					}
				}
				BlockPos dest = data.mapping.get(pos);
				if (dest != null) {
					XplatAbstractions.INSTANCE.sendToNear(world, pos, new BotaniaEffectPacket(EffectType.PARTICLE_BEAM,
							pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
							dest.getX(), dest.getY(), dest.getZ()));
				}
			}
		}

		return true;
	}

	public static class WorldData extends SavedData {

		private static final String ID = "PistonRelayPairs";
		public static final Factory<WorldData> FACTORY = new Factory<>(WorldData::new, WorldData::new, DataFixTypes.LEVEL);
		public final Map<BlockPos, BlockPos> mapping = new HashMap<>();

		public WorldData() {
			// initialize with empty data
		}

		public WorldData(CompoundTag cmp, HolderLookup.Provider registries) {
			ListTag list = cmp.getList("list", Tag.TAG_INT_ARRAY);
			for (int i = 0; i < list.size(); i += 2) {
				Tag from = list.get(i);
				Tag to = list.get(i + 1);
				BlockPos fromPos = BlockPos.CODEC.decode(NbtOps.INSTANCE, from).result().orElseThrow().getFirst();
				BlockPos toPos = BlockPos.CODEC.decode(NbtOps.INSTANCE, to).result().orElseThrow().getFirst();

				mapping.put(fromPos, toPos);
			}
		}

		@NotNull
		@Override
		public CompoundTag save(CompoundTag cmp, HolderLookup.Provider registries) {
			ListTag list = new ListTag();
			for (Map.Entry<BlockPos, BlockPos> e : mapping.entrySet()) {
				Tag from = BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, e.getKey()).result().orElseThrow();
				Tag to = BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, e.getValue()).result().orElseThrow();
				list.add(from);
				list.add(to);
			}
			cmp.put("list", list);
			return cmp;
		}

		public static WorldData get(Level world) {
			return ((ServerLevel) world).getDataStorage().computeIfAbsent(FACTORY, ID);
		}
	}
}
