package vazkii.botania.fabric.internal_caps;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.common.block.block_entity.red_string.RedStringContainerBlockEntity;

import java.util.Iterator;

public class RedStringContainerStorage implements Storage<ItemVariant> {
	private final RedStringContainerBlockEntity container;
	@Nullable
	private final Direction direction;

	private BlockApiCache<Storage<ItemVariant>, Direction> cache;
	private BlockPos cachePos;

	public RedStringContainerStorage(RedStringContainerBlockEntity container, @Nullable Direction direction) {
		this.container = container;
		this.direction = direction;
	}

	private Storage<ItemVariant> getStorage() {
		Level level = container.getLevel();
		BlockPos pos = container.getBinding();
		if (level.isClientSide || pos == null) {
			return Storage.empty();
		}

		if (!pos.equals(cachePos)) {
			cachePos = pos;
			cache = BlockApiCache.create(ItemStorage.SIDED, ((ServerLevel) level), pos);
		}

		Storage<ItemVariant> storage = cache.find(direction);
		if (storage == null) {
			return Storage.empty();
		}
		return storage;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return getStorage().insert(resource, maxAmount, transaction);
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return getStorage().extract(resource, maxAmount, transaction);
	}

	@NotNull
	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return getStorage().iterator();
	}

	@Override
	public boolean supportsInsertion() {
		// Since the binding target could change we can't know if "absolutely always 0" is correct.
		return true;
	}

	@Override
	public boolean supportsExtraction() {
		// Since the binding target could change we can't know if "absolutely always 0" is correct.
		// Corporea spark attachment also depends on this returning true for the UP direction.
		return true;
	}

	@Override
	public long getVersion() {
		return getStorage().getVersion();
	}
}
