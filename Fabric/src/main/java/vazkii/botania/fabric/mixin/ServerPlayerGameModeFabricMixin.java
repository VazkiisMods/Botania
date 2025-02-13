/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.tool.VitreousPickaxeItem;
import vazkii.botania.common.item.equipment.tool.elementium.ElementiumShovelItem;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraTruncatorItem;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeFabricMixin {
	@Shadow
	@Final
	protected ServerPlayer player;

	@Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;playerWillDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/block/state/BlockState;"))
	private void onStartBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		ServerPlayer player = this.player;
		ItemStack stack = player.getMainHandItem();
		if (stack.is(BotaniaItems.terraAxe)) {
			((TerraTruncatorItem) BotaniaItems.terraAxe).onBlockStartBreak(stack, pos, player);
		} else if (stack.is(BotaniaItems.terraPick)) {
			((TerraShattererItem) BotaniaItems.terraPick).onBlockStartBreak(stack, pos, player);
		} else if (stack.is(BotaniaItems.elementiumShovel)) {
			((ElementiumShovelItem) BotaniaItems.elementiumShovel).onBlockStartBreak(stack, pos, player);
		} else if (stack.is(BotaniaItems.glassPick)) {
			((VitreousPickaxeItem) BotaniaItems.glassPick).onBlockStartBreak(stack, pos, player);
		}
	}
}
