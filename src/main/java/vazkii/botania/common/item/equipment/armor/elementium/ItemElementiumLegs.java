package vazkii.botania.common.item.equipment.armor.elementium;

import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.core.handler.PixieHandler;

import javax.annotation.Nonnull;

public class ItemElementiumLegs extends ItemElementiumArmor {

	public ItemElementiumLegs(Properties props) {
		super(EquipmentSlotType.LEGS, props);
	}

	@Nonnull
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot) {
		Multimap<String, AttributeModifier> ret = super.getAttributeModifiers(slot);
		if (slot == getEquipmentSlot()) {
			ret.put(PixieHandler.PIXIE_SPAWN_CHANCE.getName(), PixieHandler.makeModifier(slot, "Armor modifier", 0.15));
		}
		return ret;
	}

}
