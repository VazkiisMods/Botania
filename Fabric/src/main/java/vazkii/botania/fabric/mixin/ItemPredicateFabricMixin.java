/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.mixin;

import net.minecraft.advancements.critereon.ItemPredicate;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemPredicate.class)
public abstract class ItemPredicateFabricMixin {
	// TODO: probably worth dropping in favor of conventional item tag #c:tools/shears instead of trying to hack into record

//	@ModifyVariable(at = @At("HEAD"), method = "<init>(Lnet/minecraft/tags/TagKey;Ljava/util/Set;Lnet/minecraft/advancements/critereon/MinMaxBounds$Ints;Lnet/minecraft/advancements/critereon/MinMaxBounds$Ints;[Lnet/minecraft/advancements/critereon/EnchantmentPredicate;[Lnet/minecraft/advancements/critereon/EnchantmentPredicate;Lnet/minecraft/world/item/alchemy/Potion;Lnet/minecraft/advancements/critereon/NbtPredicate;)V", argsOnly = true)
//	private static Set<Item> addBotaniaShears(Set<Item> set) {
//		if (set != null && set.contains(Items.SHEARS)) {
//			set = new HashSet<>(set);
//			set.add(BotaniaItems.manasteelShears);
//			set.add(BotaniaItems.elementiumShears);
//			set = ImmutableSet.copyOf(set);
//		}
//		return set;
//	}
}
