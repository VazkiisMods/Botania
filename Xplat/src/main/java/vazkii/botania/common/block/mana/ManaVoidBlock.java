/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.mana;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.api.mana.PoolOverlayProvider;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.block.BotaniaBlock;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class ManaVoidBlock extends BotaniaBlock implements PoolOverlayProvider {
	private static final int SPARKLE_EVENT = 0;
	private static final ResourceLocation OVERLAY_ICON = botaniaRL("block/mana_void_overlay");

	public ManaVoidBlock(Properties builder) {
		super(builder);
	}

	@Override
	public ResourceLocation getIcon(Level world, BlockPos pos) {
		return OVERLAY_ICON;
	}

	@Override
	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int payload) {
		if (id == SPARKLE_EVENT) {
			if (level.isClientSide) {
				for (int i = 0; i < 10; i++) {
					SparkleParticleData data = SparkleParticleData.sparkle(0.7F + 0.5F * (float) Math.random(), 0.2F, 0.2F, 0.2F, 5);
					level.addParticle(data, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0, 0, 0);
				}
			}
			return true;
		}
		return super.triggerEvent(state, level, pos, id, payload);
	}

	public static class ManaReceiverImpl implements ManaReceiver {
		private final Level level;
		private final BlockPos pos;
		private final BlockState state;

		public ManaReceiverImpl(Level level, BlockPos pos, BlockState state) {
			this.level = level;
			this.pos = pos;
			this.state = state;
		}

		@Override
		public Level getManaReceiverLevel() {
			return level;
		}

		@Override
		public BlockPos getManaReceiverPos() {
			return pos;
		}

		@Override
		public int getCurrentMana() {
			return 0;
		}

		@Override
		public boolean isFull() {
			return false;
		}

		@Override
		public void receiveMana(int mana) {
			if (mana > 0) {
				level.blockEvent(pos, state.getBlock(), SPARKLE_EVENT, 0);
			}
		}

		@Override
		public boolean canReceiveManaFromBursts() {
			return true;
		}
	}

}
