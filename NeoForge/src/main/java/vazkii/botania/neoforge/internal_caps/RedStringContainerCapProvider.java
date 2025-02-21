package vazkii.botania.neoforge.internal_caps;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

import org.jetbrains.annotations.Nullable;
import vazkii.botania.common.block.block_entity.red_string.RedStringContainerBlockEntity;

public class RedStringContainerCapProvider implements ICapabilityProvider<RedStringContainerBlockEntity, Direction, IItemHandler> {
	private static final Lazy<IItemHandler> EMPTY = Lazy.of(EmptyItemHandler::new);

	@Override
	public IItemHandler getCapability(RedStringContainerBlockEntity container, @Nullable Direction side) {
		BlockPos bindingPos = container.getBinding();
		if (bindingPos != null) {
			IItemHandler handler = container.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, container.getBlockPos(), side);
			if (handler != null) {
				return handler;
			}
		}
		return EMPTY.get();
	}
}
