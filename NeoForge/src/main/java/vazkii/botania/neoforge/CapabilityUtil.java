package vazkii.botania.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStackSimple;

import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.common.internal_caps.SerializableComponent;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public final class CapabilityUtil {
	public static <T, U extends T> ICapabilityProvider makeProvider(Capability<T> cap, U instance) {
		Lazy<T> lazyInstanceButNotReally = Lazy.of(() -> instance);
		return new CapProvider<>(cap, lazyInstanceButNotReally);
	}

	public static <T extends SerializableComponent> ICapabilityProvider makeSavedProvider(Capability<T> cap, T instance) {
		return new CapProviderSerializable<>(cap, instance);
	}

	public static class WaterBowlFluidHandler extends FluidHandlerItemStackSimple.SwapEmpty {
		public WaterBowlFluidHandler(ItemStack stack) {
			super(stack, new ItemStack(Items.BOWL), FluidType.BUCKET_VOLUME);
			setFluid(new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME));
		}

		@Override
		public boolean canFillFluidType(FluidStack fluid) {
			return false;
		}

		@Override
		public boolean canDrainFluidType(FluidStack fluid) {
			return fluid.getFluid() == Fluids.WATER;
		}
	}

	public interface Provider<T> {
		@Nullable
		T find(Level level, BlockPos pos, BlockState state);
	}

	private static final Map<BlockCapability<?, ?>, Map<Block, Provider<?>>> BLOCK_LOOKASIDE = new IdentityHashMap<>();

	public static <T, C> void registerBlockLookaside(BlockCapability<T, C> cap, Provider<T> provider, Block... blocks) {
		var inner = BLOCK_LOOKASIDE.computeIfAbsent(cap, k -> new HashMap<>());
		for (var block : blocks) {
			inner.put(block, provider);
		}
	}

	// todo this might need to be exposed in the API
	@Nullable
	public static <T> T findCapability(BlockCapability<T, Direction> capability, Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
		return findCapability(capability, level, pos, state, be, null);
	}

	@Nullable
	public static <T> T findCapability(BlockCapability<T, Direction> capability, Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be, @Nullable Direction direction) {
		if (be != null) {
			var instance = be.getCapability(capability, direction);
			if (instance.isPresent()) {
				return instance.orElseThrow(NullPointerException::new);
			}
		}

		var provider = BLOCK_LOOKASIDE.getOrDefault(capability, Collections.emptyMap())
				.get(state.getBlock());
		if (provider != null) {
			@SuppressWarnings("unchecked") // provider was typechecked on register
			T instance = (T) provider.find(level, pos, state);
			return instance;
		}

		return null;
	}

	private CapabilityUtil() {}

	private static class CapProvider<T> implements ICapabilityProvider {
		protected final Capability<T> cap;
		protected final Lazy<T> lazyInstanceButNotReally;

		public CapProvider(Capability<T> cap, Lazy<T> instance) {
			this.cap = cap;
			this.lazyInstanceButNotReally = instance;
		}

		@NotNull
		@Override
		public <C> Lazy<C> getCapability(@NotNull Capability<C> queryCap, @Nullable Direction side) {
			return cap.orEmpty(queryCap, lazyInstanceButNotReally);
		}
	}

	private static class CapProviderSerializable<T extends SerializableComponent> extends CapProvider<T> implements INBTSerializable<CompoundTag> {
		public CapProviderSerializable(Capability<T> cap, T instance) {
			super(cap, Lazy.of(() -> instance));
		}

		@Override
		public CompoundTag serializeNBT(HolderLookup.Provider provider) {
			return lazyInstanceButNotReally.get().serializeNBT(provider);
		}

		@Override
		public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
			lazyInstanceButNotReally.get().deserializeNBT(provider, nbt);
		}
	}
}
