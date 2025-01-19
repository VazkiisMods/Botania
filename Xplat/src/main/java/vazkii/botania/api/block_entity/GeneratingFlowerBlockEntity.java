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
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.internal.ManaNetwork;
import vazkii.botania.api.mana.ManaCollector;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

/**
 * The basic class for a Generating Flower.
 */
public abstract class GeneratingFlowerBlockEntity extends BindableSpecialFlowerBlockEntity<ManaCollector> {
	private static final ResourceLocation SPREADER_ID = botaniaRL("mana_spreader");

	public static final int LINK_RANGE = 6;
	private static final String TAG_MANA = "mana";

	private int mana;

	public GeneratingFlowerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, ManaCollector.class);
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		if (getLevel().isClientSide) {
			double particleChance = 1F - (double) getMana() / (double) getMaxMana() / 3.5F;
			int color = getColor();
			float red = (color >> 16 & 0xFF) / 255F;
			float green = (color >> 8 & 0xFF) / 255F;
			float blue = (color & 0xFF) / 255F;

			if (Math.random() > particleChance) {
				Vec3 offset = getLevel().getBlockState(getBlockPos()).getOffset(getLevel(), getBlockPos());
				double x = getBlockPos().getX() + offset.x;
				double y = getBlockPos().getY() + offset.y;
				double z = getBlockPos().getZ() + offset.z;
				BotaniaAPI.instance().sparkleFX(getLevel(), x + 0.3 + Math.random() * 0.5, y + 0.5 + Math.random() * 0.5, z + 0.3 + Math.random() * 0.5, red, green, blue, (float) Math.random(), 5);
			}
		}
		emptyManaIntoCollector();
	}

	@Override
	public int getBindingRadius() {
		return LINK_RANGE;
	}

	@Nullable
	@Override
	public BlockPos findClosestTarget() {
		ManaNetwork network = BotaniaAPI.instance().getManaNetworkInstance();
		var closestCollector = network.getClosestCollector(getBlockPos(), getLevel(), getBindingRadius());
		return closestCollector == null ? null : closestCollector.getManaReceiverPos();
	}

	public void emptyManaIntoCollector() {
		ManaCollector collector = findBoundTile();
		if (collector != null && !collector.isFull() && getMana() > 0) {
			int manaval = Math.min(getMana(), collector.getMaxMana() - collector.getCurrentMana());
			addMana(-manaval);
			collector.receiveMana(manaval);
			sync();
		}
	}

	@Override
	public int getMana() {
		return mana;
	}

	@Override
	public void addMana(int mana) {
		this.mana = Math.min(getMaxMana(), this.getMana() + mana);
		setChanged();
	}

	@Override
	public ItemStack getDefaultHudIcon() {
		return BuiltInRegistries.ITEM.getOptional(SPREADER_ID).map(ItemStack::new).orElse(ItemStack.EMPTY);
	}

	@Override
	public void readFromPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.readFromPacketNBT(cmp, registries);
		mana = cmp.getInt(TAG_MANA);
	}

	@Override
	public void writeToPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.writeToPacketNBT(cmp, registries);
		cmp.putInt(TAG_MANA, getMana());
	}
}
