package vazkii.botania.mixin;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPattern;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import vazkii.botania.common.item.ItemWithBannerPattern;

import java.util.List;

@Mixin(LoomMenu.class)
public abstract class LoomMenuMixin extends AbstractContainerMenu {
	protected LoomMenuMixin(@Nullable MenuType<?> menuType, int containerId) {
		super(menuType, containerId);
	}

	@Shadow @Final private HolderGetter<BannerPattern> patternGetter;

	@Inject(at = @At("HEAD"), method = "getSelectablePatterns(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", cancellable = true)
	private void handleBotaniaPatternItems(ItemStack stack, CallbackInfoReturnable<List<Holder<BannerPattern>>> cir) {
		if (stack.getItem() instanceof ItemWithBannerPattern p) {
			cir.setReturnValue(this.patternGetter.get(p.getBannerPattern())
					.map(ImmutableList::copyOf).orElse(ImmutableList.of()));
		}
	}

	@Shadow
	@Final
	Slot patternSlot;

	@Inject(
		at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"),
		method = "quickMoveStack", cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT
	)
	private void handleBotaniaPatternQuickMoveStack(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir, ItemStack dummyStack,
			Slot slot, ItemStack movedStack) {
		if (movedStack.getItem() instanceof ItemWithBannerPattern
				&& moveItemStackTo(movedStack, this.patternSlot.index, this.patternSlot.index + 1, false)) {
			cir.setReturnValue(ItemStack.EMPTY);
		}
	}
}
