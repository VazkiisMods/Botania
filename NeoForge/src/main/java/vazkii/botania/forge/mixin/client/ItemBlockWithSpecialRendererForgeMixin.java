package vazkii.botania.forge.mixin.client;

import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import org.spongepowered.asm.mixin.Mixin;

import vazkii.botania.common.item.block.BlockItemWithSpecialRenderer;
import vazkii.botania.forge.client.ForgeBlockEntityItemRendererHelper;

import java.util.function.Consumer;

@Mixin(BlockItemWithSpecialRenderer.class)
public abstract class ItemBlockWithSpecialRendererForgeMixin extends Item {
	private ItemBlockWithSpecialRendererForgeMixin(Properties props) {
		super(props);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		ForgeBlockEntityItemRendererHelper.initItem(consumer);
	}
}
