package vazkii.botania.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStackSimple;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.common.internal_caps.SerializableComponent;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public final class CapabilityUtil {
	public static <T, U extends T> ICapabilityProvider makeProvider(Capability<T> cap, U instance) {
		LazyOptional<T> lazyInstanceButNotReally = LazyOptional.of(() -> instance);
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

	private static final Map<Capability<?>, Map<Block, Provider<?>>> BLOCK_LOOKASIDE = new IdentityHashMap<>();

	public static <T> void registerBlockLookaside(Capability<T> cap, Provider<T> provider, Block... blocks) {
		var inner = BLOCK_LOOKASIDE.computeIfAbsent(cap, k -> new HashMap<>());
		for (var block : blocks) {
			inner.put(block, provider);
		}
	}

	// todo this might need to be exposed in the API
	@Nullable
	public static <T> T findCapability(Capability<T> capability, Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
		return findCapability(capability, level, pos, state, be, null);
	}

	@Nullable
	public static <T> T findCapability(Capability<T> capability, Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be, @Nullable Direction direction) {
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
		protected final LazyOptional<T> lazyInstanceButNotReally;

		public CapProvider(Capability<T> cap, LazyOptional<T> instance) {
			this.cap = cap;
			this.lazyInstanceButNotReally = instance;
		}

		@NotNull
		@Override
		public <C> LazyOptional<C> getCapability(@NotNull Capability<C> queryCap, @Nullable Direction side) {
			return cap.orEmpty(queryCap, lazyInstanceButNotReally);
		}
	}

	private static class CapProviderSerializable<T extends SerializableComponent> extends CapProvider<T> implements INBTSerializable<CompoundTag> {
		public CapProviderSerializable(Capability<T> cap, T instance) {
			super(cap, LazyOptional.of(() -> instance));
		}

		@Override
		public CompoundTag serializeNBT() {
			return lazyInstanceButNotReally.map(SerializableComponent::serializeNBT).orElse(null);
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			lazyInstanceButNotReally.ifPresent(i -> i.deserializeNBT(nbt));
		}
	}
}
