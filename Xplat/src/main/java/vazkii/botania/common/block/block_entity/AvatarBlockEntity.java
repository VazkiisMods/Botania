/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import vazkii.botania.api.block.Avatar;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AvatarBlockEntity extends SimpleInventoryBlockEntity implements Avatar, ManaReceiver {
	private static final int MAX_MANA = 6400;

	private static final String TAG_ENABLED = "enabled";
	private static final String TAG_TICKS_ELAPSED = "ticksElapsed";
	private static final String TAG_MANA = "mana";
	private static final String TAG_COOLDOWNS = "boostCooldowns";

	private boolean enabled;
	private int ticksElapsed;
	private int mana;
	private final Map<UUID, Integer> boostCooldowns = new HashMap<>();

	public AvatarBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.AVATAR, pos, state);
	}

	public static void commonTick(Level level, BlockPos worldPosition, BlockState state, AvatarBlockEntity self) {
		self.enabled = !level.hasNeighborSignal(worldPosition);

		ItemStack stack = self.getItemHandler().getItem(0);
		if (!stack.isEmpty()) {
			var wieldable = XplatAbstractions.INSTANCE.findAvatarWieldable(stack);
			if (wieldable != null) {
				wieldable.onAvatarUpdate(self);
			}
		}

		if (self.enabled) {
			self.ticksElapsed++;
		}
	}

	@Override
	public void writePacketNBT(CompoundTag tag, HolderLookup.Provider registries) {
		super.writePacketNBT(tag, registries);
		tag.putBoolean(TAG_ENABLED, enabled);
		tag.putInt(TAG_TICKS_ELAPSED, ticksElapsed);
		tag.putInt(TAG_MANA, mana);
		ListTag boostCooldowns = new ListTag();
		for (Map.Entry<UUID, Integer> e : this.boostCooldowns.entrySet()) {
			CompoundTag cmp = new CompoundTag();
			cmp.putUUID("id", e.getKey());
			cmp.putInt("cooldown", e.getValue());
			boostCooldowns.add(cmp);
		}
		tag.put(TAG_COOLDOWNS, boostCooldowns);
	}

	@Override
	public void readPacketNBT(CompoundTag tag, HolderLookup.Provider registries) {
		super.readPacketNBT(tag, registries);
		enabled = tag.getBoolean(TAG_ENABLED);
		ticksElapsed = tag.getInt(TAG_TICKS_ELAPSED);
		mana = tag.getInt(TAG_MANA);
		boostCooldowns.clear();
		ListTag boostCooldowns = tag.getList(TAG_COOLDOWNS, Tag.TAG_COMPOUND);
		for (Tag nbt : boostCooldowns) {
			CompoundTag cmp = ((CompoundTag) nbt);
			UUID id = cmp.getUUID("id");
			int cooldown = cmp.getInt("cooldown");
			this.boostCooldowns.put(id, cooldown);
		}
	}

	@Override
	protected SimpleContainer createItemHandler() {
		return new SimpleContainer(1) {
			@Override
			public int getMaxStackSize() {
				return 1;
			}
		};
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level != null && !level.isClientSide) {
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}

	@Override
	public boolean isFull() {
		return mana >= MAX_MANA;
	}

	@Override
	public void receiveMana(int mana) {
		this.mana = Math.min(3 * MAX_MANA, this.mana + mana);
	}

	@Override
	public boolean canReceiveManaFromBursts() {
		return !getItemHandler().getItem(0).isEmpty();
	}

	@Override
	public Level getManaReceiverLevel() {
		return getLevel();
	}

	@Override
	public BlockPos getManaReceiverPos() {
		return getBlockPos();
	}

	@Override
	public int getCurrentMana() {
		return mana;
	}

	@Override
	public Container getInventory() {
		return getItemHandler();
	}

	@Override
	public Direction getAvatarFacing() {
		return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public int getElapsedFunctionalTicks() {
		return ticksElapsed;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public Map<UUID, Integer> getBoostCooldowns() {
		return boostCooldowns;
	}
}
