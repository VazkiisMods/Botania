/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.internal.ManaNetwork;
import vazkii.botania.api.mana.ManaPool;
import static vazkii.botania.api.BotaniaAPI.botaniaRL;

/**
 * The basic class for a Functional Flower.
 */
public abstract class FunctionalFlowerBlockEntity extends BindableSpecialFlowerBlockEntity<ManaPool> {
	private static final ResourceLocation POOL_ID = botaniaRL("mana_pool");

	public static final int LINK_RANGE = 10;
	private static final String TAG_MANA = "mana";

	private int mana;
	public int redstoneSignal = 0;

	public FunctionalFlowerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, ManaPool.class);
	}

	/**
	 * If set to true, redstoneSignal will be updated every tick.
	 */
	public boolean acceptsRedstone() {
		return false;
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		drawManaFromPool();

		redstoneSignal = 0;
		if (acceptsRedstone()) {
			for (Direction dir : Direction.values()) {
				int redstoneSide = getLevel().getSignal(getBlockPos().relative(dir), dir);
				redstoneSignal = Math.max(redstoneSignal, redstoneSide);
			}
		}

		if (getLevel().isClientSide) {
			double particleChance = 1F - (double) mana / (double) getMaxMana() / 3.5F;
			int color = getColor();
			float red = (color >> 16 & 0xFF) / 255F;
			float green = (color >> 8 & 0xFF) / 255F;
			float blue = (color & 0xFF) / 255F;
			if (Math.random() > particleChance) {
				BotaniaAPI.instance().sparkleFX(getLevel(), getBlockPos().getX() + 0.3 + Math.random() * 0.5, getBlockPos().getY() + 0.5 + Math.random() * 0.5, getBlockPos().getZ() + 0.3 + Math.random() * 0.5, red, green, blue, (float) Math.random(), 5);
			}
		}
	}

	@Override
	public int getBindingRadius() {
		return LINK_RANGE;
	}

	@Nullable
	@Override
	public BlockPos findClosestTarget() {
		ManaNetwork network = BotaniaAPI.instance().getManaNetworkInstance();
		var closestPool = network.getClosestPool(getBlockPos(), getLevel(), getBindingRadius());
		return closestPool == null ? null : closestPool.getManaReceiverPos();
	}

	public void drawManaFromPool() {
		ManaPool pool = findBoundTile();
		if (pool != null) {
			int manaInPool = pool.getCurrentMana();
			int manaMissing = getMaxMana() - mana;
			int manaToRemove = Math.min(manaMissing, manaInPool);
			pool.receiveMana(-manaToRemove);
			addMana(manaToRemove);
		}
	}

	@Override
	public int getMana() {
		return mana;
	}

	@Override
	public void addMana(int mana) {
		this.mana = Mth.clamp(this.mana + mana, 0, getMaxMana());
		setChanged();
	}

	@Override
	public ItemStack getDefaultHudIcon() {
		return BuiltInRegistries.ITEM.getOptional(POOL_ID).map(ItemStack::new).orElse(ItemStack.EMPTY);
	}

	@Override
	public void readFromPacketNBT(CompoundTag cmp) {
		super.readFromPacketNBT(cmp);
		mana = cmp.getInt(TAG_MANA);
	}

	@Override
	public void writeToPacketNBT(CompoundTag cmp) {
		super.writeToPacketNBT(cmp);
		cmp.putInt(TAG_MANA, mana);
	}
}
