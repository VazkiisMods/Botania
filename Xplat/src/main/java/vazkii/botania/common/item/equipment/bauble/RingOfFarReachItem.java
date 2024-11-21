/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.bauble;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.xplat.XplatAbstractions;

public class RingOfFarReachItem extends BaubleItem {

	public RingOfFarReachItem(Properties props) {
		super(props);
	}

	@Override
	public Multimap<Holder<Attribute>, AttributeModifier> getEquippedAttributeModifiers(ItemStack stack) {
		Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
		attributes.put(Attributes.BLOCK_INTERACTION_RANGE,
				new AttributeModifier(BotaniaAPI.botaniaRL("reach_ring"), 3.5, AttributeModifier.Operation.ADD_VALUE));
		attributes.put(Attributes.ENTITY_INTERACTION_RANGE,
				new AttributeModifier(BotaniaAPI.botaniaRL("reach_ring"), 3.5, AttributeModifier.Operation.ADD_VALUE));
		return attributes;
	}
}
