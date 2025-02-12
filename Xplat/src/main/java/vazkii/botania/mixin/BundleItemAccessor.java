package vazkii.botania.mixin;

import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.stream.Stream;

@Mixin(BundleItem.class)
public interface BundleItemAccessor {
	@Invoker("getContents")
	static Stream<ItemStack> call_getContents(ItemStack stack) {
		throw new IllegalStateException();
	}
}
