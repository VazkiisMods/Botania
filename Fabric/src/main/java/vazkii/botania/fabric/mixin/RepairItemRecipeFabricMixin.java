package vazkii.botania.fabric.mixin;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import vazkii.botania.common.item.BotaniaItems;

@Mixin(RepairItemRecipe.class)
public class RepairItemRecipeFabricMixin {
	@Inject(
		at = @At("RETURN"),
		method = "Lnet/minecraft/world/item/crafting/RepairItemRecipe;matches(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Z",
		cancellable = true
	)
	private void preventSpellClothRepair(CraftingInput input, Level level, CallbackInfoReturnable<Boolean> cir) {
		/*todo
		if (craftingContainer.hasAnyMatching(s -> s.is(BotaniaItems.spellCloth))) {
			cir.setReturnValue(false);
		}

		 */
	}
}
