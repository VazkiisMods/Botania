package vazkii.botania.forge.internal_caps;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyHandler;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.block.block_entity.red_string.RedStringContainerBlockEntity;

public class RedStringContainerCapProvider implements ICapabilityProvider {
	private static final LazyOptional<IItemHandler> EMPTY = LazyOptional.of(EmptyHandler::new);

	private final RedStringContainerBlockEntity container;

	public RedStringContainerCapProvider(RedStringContainerBlockEntity container) {
		this.container = container;
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
		if (cap == Capabilities.ITEM_HANDLER) {
			BlockEntity binding = container.getTileAtBinding();
			if (binding != null) {
				LazyOptional<?> optional = binding.getCapability(cap, side);
				if (optional.isPresent()) {
					return optional.cast();
				}
			}
			return EMPTY.cast();
		}
		return LazyOptional.empty();
	}
}
