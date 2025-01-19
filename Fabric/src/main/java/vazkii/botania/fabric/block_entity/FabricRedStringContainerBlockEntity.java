package vazkii.botania.fabric.block_entity;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.block.block_entity.red_string.RedStringContainerBlockEntity;
import vazkii.botania.fabric.internal_caps.RedStringContainerStorage;

import java.util.EnumMap;

public class FabricRedStringContainerBlockEntity extends RedStringContainerBlockEntity {
	private RedStringContainerStorage storage;
	private final EnumMap<Direction, RedStringContainerStorage> directionalStorages = new EnumMap<>(Direction.class);
	private BlockPos clientPos;

	public FabricRedStringContainerBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	public static Storage<ItemVariant> getStorage(RedStringContainerBlockEntity container, Direction direction) {
		if (container instanceof FabricRedStringContainerBlockEntity c) {
			return c.storage(direction);
		}
		return null;
	}

	public Storage<ItemVariant> storage(@Nullable Direction direction) {
		if (direction == null) {
			if (storage == null) {
				storage = new RedStringContainerStorage(this, null);
			}
			return storage;
		}
		return directionalStorages.computeIfAbsent(direction, dir -> new RedStringContainerStorage(this, dir));
	}

	@Override
	public void onBound(BlockPos pos) {
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}

	@Override
	public void writePacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		// We cannot query for the storage api on the client - so we send the binding position.
		BlockPos binding = getBinding();
		if (binding == null) {
			// hack: empty NBT gets the packet ignored but we don't want that
			cmp.putByte("-", (byte) 0);
			return;
		}
		cmp.putInt("bindX", binding.getX());
		cmp.putInt("bindY", binding.getY());
		cmp.putInt("bindZ", binding.getZ());
	}

	@Override
	public void readPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		if (cmp.contains("bindX")) {
			clientPos = new BlockPos(cmp.getInt("bindX"), cmp.getInt("bindY"), cmp.getInt("bindZ"));
		} else {
			clientPos = null;
		}
	}

	@Nullable
	@Override
	public BlockPos getBinding() {
		return level.isClientSide ? clientPos : super.getBinding();
	}
}
