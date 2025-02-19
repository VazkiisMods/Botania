package vazkii.botania.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.PowderSnowBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.item.BotaniaItems;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {
	@WrapOperation(method = "canEntityWalkOnPowderSnow", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
	private static boolean canWalkOnPowderSnowWithBotaniaItems(ItemStack stack, Item item, Operation<Boolean> original, @Local(argsOnly = true) Entity entity) {
		return original.call(stack, item) || stack.is(BotaniaItems.manaweaveBoots)
				|| !EquipmentHandler.findOrEmpty(BotaniaItems.icePendant, (LivingEntity) entity).isEmpty();
	}
}
