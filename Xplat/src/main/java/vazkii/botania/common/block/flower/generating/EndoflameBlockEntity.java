/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.flower.generating;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.block.mana.ManaSpreaderBlock;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.DelayHelper;
import vazkii.botania.common.helper.EntityHelper;
import vazkii.botania.xplat.XplatAbstractions;

public class EndoflameBlockEntity extends GeneratingFlowerBlockEntity {
	private static final String TAG_BURN_TIME = "burnTime";
	private static final int FUEL_CAP = 32000;
	private static final int RANGE = 3;
	private static final int START_BURN_EVENT = 0;

	private int burnTime = 0;

	public EndoflameBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaFlowerBlocks.ENDOFLAME, pos, state);
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		boolean wasBurning = burnTime > 0;
		if (wasBurning) {
			burnTime--;
		}

		if (getLevel().isClientSide) {
			if (burnTime > 0 && getLevel().random.nextInt(10) == 0) {
				emitParticle(ParticleTypes.FLAME, 0.4 + Math.random() * 0.2, 0.7, 0.4 + Math.random() * 0.2, 0.0D, 0.0D, 0.0D);
			}
			return;
		} else {
			if (burnTime > 0 && ticksExisted % 2 == 0) {
				addMana(3);
			}
		}

		if (burnTime == 0) {
			if (getMana() < getMaxMana()) {

				for (ItemEntity item : getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(getEffectivePos()).inflate(RANGE))) {
					if (DelayHelper.canInteractWith(this, item)) {
						ItemStack stack = item.getItem();
						if (stack.getItem().hasCraftingRemainingItem()) {
							continue;
						}

						int burnTime = getBurnTime(stack);
						if (burnTime > 0 && stack.getCount() > 0) {
							this.burnTime = Math.min(FUEL_CAP, burnTime) / 2;

							EntityHelper.shrinkItem(item);
							getLevel().playSound(null, getEffectivePos(), BotaniaSounds.endoflame, SoundSource.BLOCKS, 1F, 1F);
							getLevel().blockEvent(getBlockPos(), getBlockState().getBlock(), START_BURN_EVENT, item.getId());
							getLevel().gameEvent(null, GameEvent.BLOCK_ACTIVATE, getBlockPos());
							sync();

							return;
						}
					}
				}
			}
			if (wasBurning) {
				getLevel().gameEvent(null, GameEvent.BLOCK_DEACTIVATE, getBlockPos());
			}
		}
	}

	@Override
	public boolean triggerEvent(int event, int param) {
		if (event == START_BURN_EVENT) {
			Entity e = getLevel().getEntity(param);
			if (e != null) {
				e.level().addParticle(ParticleTypes.LARGE_SMOKE, e.getX(), e.getY() + 0.1, e.getZ(), 0.0D, 0.0D, 0.0D);
				e.level().addParticle(ParticleTypes.FLAME, e.getX(), e.getY(), e.getZ(), 0.0D, 0.0D, 0.0D);
			}
			return true;
		} else {
			return super.triggerEvent(event, param);
		}
	}

	@Override
	public int getMaxMana() {
		return 300;
	}

	@Override
	public int getColor() {
		return 0x785000;
	}

	@Override
	public RadiusDescriptor getRadius() {
		return RadiusDescriptor.Rectangle.square(getEffectivePos(), RANGE);
	}

	@Override
	public void writeToPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.writeToPacketNBT(cmp, registries);

		cmp.putInt(TAG_BURN_TIME, burnTime);
	}

	@Override
	public void readFromPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.readFromPacketNBT(cmp, registries);

		burnTime = cmp.getInt(TAG_BURN_TIME);
	}

	private int getBurnTime(ItemStack stack) {
		if (stack.isEmpty() || Block.byItem(stack.getItem()) instanceof ManaSpreaderBlock) {
			return 0;
		} else {
			return XplatAbstractions.INSTANCE.getSmeltingBurnTime(stack);
		}
	}

}
