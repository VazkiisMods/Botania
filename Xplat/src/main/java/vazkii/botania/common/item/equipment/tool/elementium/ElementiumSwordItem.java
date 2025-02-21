/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.tool.elementium;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.handler.PixieHandler;
import vazkii.botania.common.item.equipment.tool.manasteel.ManasteelSwordItem;

public class ElementiumSwordItem extends ManasteelSwordItem {

	public ElementiumSwordItem(Properties props) {
		super(BotaniaAPI.instance().getElementiumItemTier(), props);
	}

	//TODO Very unsure if this works
	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers() {
		return super.getDefaultAttributeModifiers().withModifierAdded(PixieHandler.PIXIE_SPAWN_CHANCE,
				PixieHandler.makeModifier("sword_modifier", 0.05), //TODO I changed the name here to fit mc's way of naming the modifier. Check if this is alr!
				EquipmentSlotGroup.bySlot(EquipmentSlot.MAINHAND));
	}

}
