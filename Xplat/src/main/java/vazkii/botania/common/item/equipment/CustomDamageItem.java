package vazkii.botania.common.item.equipment;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.common.annotations.SoftImplement;

import java.util.function.Consumer;

public interface CustomDamageItem {
	@SoftImplement("IItemExtension")
	<T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> breakCallback);
}
