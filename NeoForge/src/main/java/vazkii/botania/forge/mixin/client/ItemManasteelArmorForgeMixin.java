package vazkii.botania.forge.mixin.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import org.spongepowered.asm.mixin.Mixin;

import vazkii.botania.client.model.armor.ArmorModels;
import vazkii.botania.common.item.equipment.armor.manasteel.ManasteelArmorItem;

import java.util.function.Consumer;

@Mixin(ManasteelArmorItem.class)
public abstract class ItemManasteelArmorForgeMixin extends Item {
	private ItemManasteelArmorForgeMixin(Properties props) {
		super(props);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
				return ArmorModels.get(stack);
			}
		});
	}
}
