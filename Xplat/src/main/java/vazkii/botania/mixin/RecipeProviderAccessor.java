/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.mixin;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecipeProvider.class)
public interface RecipeProviderAccessor {
	@Invoker("has")
	static Criterion<InventoryChangeTrigger.TriggerInstance> botania_has(ItemLike itemLike) {
		throw new IllegalStateException("Direct call to invoker method");
	}

	@Invoker("has")
	static Criterion<InventoryChangeTrigger.TriggerInstance> botania_has(TagKey<Item> tag) {
		throw new IllegalStateException("Direct call to invoker method");
	}

	@Invoker("inventoryTrigger")
	static Criterion<InventoryChangeTrigger.TriggerInstance> botania_inventoryTrigger(ItemPredicate.Builder... $$0) {
		throw new IllegalStateException("Direct call to invoker method");
	}
}
