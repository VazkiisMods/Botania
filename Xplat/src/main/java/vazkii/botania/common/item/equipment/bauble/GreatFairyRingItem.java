/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.bauble;

import com.google.common.collect.Multimap;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.common.handler.PixieHandler;

public class GreatFairyRingItem extends BaubleItem {
	public GreatFairyRingItem(Properties props) {
		super(props);
	}

	@Override
	public Multimap<Holder<Attribute>, AttributeModifier> getEquippedAttributeModifiers(ItemStack stack) {
		Multimap<Holder<Attribute>, AttributeModifier> ret = super.getEquippedAttributeModifiers(stack);
		ret.put(PixieHandler.PIXIE_SPAWN_CHANCE, PixieHandler.makeModifier("ring_modifier", 0.25));
		return ret;
	}
}
