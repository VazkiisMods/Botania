/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity.mana;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntity;

public class ManaPumpBlockEntity extends BotaniaBlockEntity {
	private static final String TAG_ACTIVE = "active";

	public float innerRingPos;
	private boolean active = false;
	public boolean hasCart = false;
	public boolean hasCartOnTop = false;
	public float moving = 0F;

	public int comparator;
	public boolean hasRedstone = false;
	private int lastComparator = 0;

	public ManaPumpBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.PUMP, pos, state);
	}

	public static void commonTick(Level level, BlockPos worldPosition, BlockState state, ManaPumpBlockEntity self) {
		self.hasRedstone = level.hasNeighborSignal(worldPosition);

		float max = 8F;
		float min = 0F;

		float incr = max / 10F;

		if (self.innerRingPos < max && self.active && self.moving >= 0F) {
			self.innerRingPos += incr;
			self.moving = incr;
			if (self.innerRingPos >= max) {
				self.innerRingPos = Math.min(max, self.innerRingPos);
				self.moving = 0F;
				for (int x = 0; x < 2; x++) {
					level.addParticle(ParticleTypes.SMOKE, worldPosition.getX() + Math.random(), worldPosition.getY() + Math.random(), worldPosition.getZ() + Math.random(), 0, 0, 0);
				}
			}
		} else if (self.innerRingPos > min) {
			self.innerRingPos -= incr * 2;
			self.moving = -incr * 2;
			if (self.innerRingPos <= min) {
				self.innerRingPos = Math.max(min, self.innerRingPos);
				self.moving = 0F;
			}
		}

		if (!self.hasCartOnTop) {
			self.comparator = 0;
		}
		if (!self.hasCart && self.active) {
			self.setActive(false);
		}
		if (self.active && self.hasRedstone) {
			self.setActive(false);
		}

		self.hasCart = false;
		self.hasCartOnTop = false;

		if (self.comparator != self.lastComparator) {
			level.updateNeighbourForOutputSignal(worldPosition, state.getBlock());
		}
		self.lastComparator = self.comparator;
	}

	@Override
	public void writePacketNBT(@NotNull CompoundTag cmp, HolderLookup.Provider registries) {
		cmp.putBoolean(TAG_ACTIVE, active);
	}

	@Override
	public void readPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		active = cmp.getBoolean(TAG_ACTIVE);
	}

	public void setActive(boolean active) {
		if (!level.isClientSide) {
			boolean diff = this.active != active;
			this.active = active;
			if (diff) {
				VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
			}
		}
	}
}
