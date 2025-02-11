/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.armor.elementium;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.mana.ManaDiscountArmor;
import vazkii.botania.common.handler.PixieHandler;

public class ElementiumHelmItem extends ElementiumArmorItem implements ManaDiscountArmor {
	public ElementiumHelmItem(Properties props) {
		super(Type.HELMET, props);
	}

	//TODO Very unsure if this works
	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers() {
		return super.getDefaultAttributeModifiers().withModifierAdded(PixieHandler.PIXIE_SPAWN_CHANCE,
				PixieHandler.makeModifier("armor." + this.type.getName(), 0.11), //TODO I changed the name here to fit mc's way of naming the modifier. Check if this is alr!
				EquipmentSlotGroup.bySlot(type.getSlot()));
	}

	@Override
	public float getDiscount(ItemStack stack, int slot, Player player, @Nullable ItemStack tool) {
		return hasArmorSet(player) ? 0.1F : 0F;
	}

}
