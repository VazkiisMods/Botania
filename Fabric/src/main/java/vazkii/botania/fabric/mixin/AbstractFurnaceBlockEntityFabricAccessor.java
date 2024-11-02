package vazkii.botania.fabric.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityFabricAccessor {
	@Invoker("canBurn")
	static boolean callCanBurn(RegistryAccess registryAccess, @Nullable RecipeHolder<?> recipe, NonNullList<ItemStack> items, int maxStackSize) {
		throw new IllegalStateException();
	}
}
