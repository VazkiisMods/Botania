package vazkii.botania.neoforge.internal_caps;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.block.block_entity.red_string.RedStringContainerBlockEntity;

public class RedStringContainerCapProvider implements ICapabilityProvider<BlockEntity, Direction, IItemHandler> {
	private static final Lazy<IItemHandler> EMPTY = Lazy.of(EmptyItemHandler::new);

	private final RedStringContainerBlockEntity container;

	public RedStringContainerCapProvider(RedStringContainerBlockEntity container) {
		this.container = container;
	}

	@NotNull
	@Override
	public IItemHandler getCapability(@NotNull BlockEntity be, Direction side) {
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
