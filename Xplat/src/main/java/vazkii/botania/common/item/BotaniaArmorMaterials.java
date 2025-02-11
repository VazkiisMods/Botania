/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 */

package vazkii.botania.common.item;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import vazkii.botania.common.handler.BotaniaSounds;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class BotaniaArmorMaterials {
	public static final Holder<ArmorMaterial> MANASTEEL = register("manasteel",
			Map.of(
					ArmorItem.Type.BOOTS, 2,
					ArmorItem.Type.LEGGINGS, 5,
					ArmorItem.Type.CHESTPLATE, 6,
					ArmorItem.Type.HELMET, 2
			),
			18, () -> Holder.direct(BotaniaSounds.equipManasteel), () -> Ingredient.of(BotaniaItems.manaSteel), 0);
	public static final Holder<ArmorMaterial> MANAWEAVE = register("manaweave",
			Map.of(
					ArmorItem.Type.BOOTS, 1,
					ArmorItem.Type.LEGGINGS, 2,
					ArmorItem.Type.CHESTPLATE, 3,
					ArmorItem.Type.HELMET, 1
			),
			18, () -> Holder.direct(BotaniaSounds.equipManaweave), () -> Ingredient.of(BotaniaItems.manaweaveCloth), 0);
	public static final Holder<ArmorMaterial> ELEMENTIUM = register("elementium",
			Map.of(
					ArmorItem.Type.BOOTS, 2,
					ArmorItem.Type.LEGGINGS, 5,
					ArmorItem.Type.CHESTPLATE, 6,
					ArmorItem.Type.HELMET, 2
			),
			18, () -> Holder.direct(BotaniaSounds.equipElementium), () -> Ingredient.of(BotaniaItems.elementium), 0);
	public static final Holder<ArmorMaterial> TERRASTEEL = register("terrasteel",
			Map.of(
					ArmorItem.Type.BOOTS, 3,
					ArmorItem.Type.LEGGINGS, 6,
					ArmorItem.Type.CHESTPLATE, 8,
					ArmorItem.Type.HELMET, 3
			),
			26, () -> Holder.direct(BotaniaSounds.equipTerrasteel), () -> Ingredient.of(BotaniaItems.terrasteel), 3);

	private static Holder<ArmorMaterial> register(
			String name,
			Map<ArmorItem.Type, Integer> defense,
			int enchantmentValue,
			Supplier<Holder<SoundEvent>> equipSound, //TODO I don't think this needs to be a supplier
			Supplier<Ingredient> repairIngredient,
			float toughness) {
		List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace(name)));
		return register(name, defense, enchantmentValue, equipSound.get(), toughness, repairIngredient, list);
	}

	private static Holder<ArmorMaterial> register(
			String name,
			Map<ArmorItem.Type, Integer> defense,
			int enchantmentValue,
			Holder<SoundEvent> equipSound,
			float toughness,
			Supplier<Ingredient> repairIngredient,
			List<ArmorMaterial.Layer> layers) {
		EnumMap<ArmorItem.Type, Integer> enummap = new EnumMap<>(ArmorItem.Type.class);

		for (ArmorItem.Type armoritem$type : ArmorItem.Type.values()) {
			enummap.put(armoritem$type, defense.get(armoritem$type));
		}

		return Registry.registerForHolder(
				BuiltInRegistries.ARMOR_MATERIAL,
				botaniaRL(name),
				new ArmorMaterial(enummap, enchantmentValue, equipSound, repairIngredient, layers, toughness, 0)
		);
	}
}
