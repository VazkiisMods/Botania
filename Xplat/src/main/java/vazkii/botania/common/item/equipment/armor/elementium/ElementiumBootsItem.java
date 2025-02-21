/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.armor.elementium;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import vazkii.botania.common.handler.PixieHandler;

public class ElementiumBootsItem extends ElementiumArmorItem {

	public ElementiumBootsItem(Properties props) {
		super(Type.BOOTS, props);
	}

	//TODO Very unsure if this works
	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers() {
		return super.getDefaultAttributeModifiers().withModifierAdded(PixieHandler.PIXIE_SPAWN_CHANCE,
				PixieHandler.makeModifier("armor." + this.type.getName(), 0.09), //TODO I changed the name here to fit mc's way of naming the modifier. Check if this is alr!
				EquipmentSlotGroup.bySlot(type.getSlot()));
	}

}
