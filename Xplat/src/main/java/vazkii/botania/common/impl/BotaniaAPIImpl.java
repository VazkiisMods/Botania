/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.BotaniaRegistries;
import vazkii.botania.api.brew.Brew;
import vazkii.botania.api.configdata.ConfigDataManager;
import vazkii.botania.api.corporea.CorporeaNodeDetector;
import vazkii.botania.api.internal.ManaNetwork;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.block.flower.functional.SolegnoliaBlockEntity;
import vazkii.botania.common.config.ConfigDataManagerImpl;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.handler.ManaNetworkHandler;
import vazkii.botania.common.helper.RegistryHelper;
import vazkii.botania.common.integration.corporea.CorporeaNodeDetectors;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.relic.RingOfLokiItem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class BotaniaAPIImpl implements BotaniaAPI {

	private enum ArmorMaterial implements net.minecraft.world.item.ArmorMaterial {
		MANASTEEL("manasteel", 16,
				Map.of(
						ArmorItem.Type.BOOTS, 2,
						ArmorItem.Type.LEGGINGS, 5,
						ArmorItem.Type.CHESTPLATE, 6,
						ArmorItem.Type.HELMET, 2
				),
				18, () -> BotaniaSounds.equipManasteel, () -> BotaniaItems.manaSteel, 0),
		MANAWEAVE("manaweave", 5,
				Map.of(
						ArmorItem.Type.BOOTS, 1,
						ArmorItem.Type.LEGGINGS, 2,
						ArmorItem.Type.CHESTPLATE, 3,
						ArmorItem.Type.HELMET, 1
				),
				18, () -> BotaniaSounds.equipManaweave, () -> BotaniaItems.manaweaveCloth, 0),
		ELEMENTIUM("elementium", 18,
				Map.of(
						ArmorItem.Type.BOOTS, 2,
						ArmorItem.Type.LEGGINGS, 5,
						ArmorItem.Type.CHESTPLATE, 6,
						ArmorItem.Type.HELMET, 2
				),
				18, () -> BotaniaSounds.equipElementium, () -> BotaniaItems.elementium, 0),
		TERRASTEEL("terrasteel", 34,
				Map.of(
						ArmorItem.Type.BOOTS, 3,
						ArmorItem.Type.LEGGINGS, 6,
						ArmorItem.Type.CHESTPLATE, 8,
						ArmorItem.Type.HELMET, 3
				),
				26, () -> BotaniaSounds.equipTerrasteel, () -> BotaniaItems.terrasteel, 3);

		private final String name;
		private final int durabilityMultiplier;
		private final Map<ArmorItem.Type, Integer> damageReduction;
		private final int enchantability;
		private final Supplier<SoundEvent> equipSound;
		private final Supplier<Item> repairItem;
		private final float toughness;

		ArmorMaterial(String name, int durabilityMultiplier, Map<ArmorItem.Type, Integer> damageReduction,
				int enchantability, Supplier<SoundEvent> equipSound, Supplier<Item> repairItem, float toughness) {
			this.name = name;
			this.durabilityMultiplier = durabilityMultiplier;
			this.damageReduction = damageReduction;
			this.enchantability = enchantability;
			this.equipSound = equipSound;
			this.repairItem = repairItem;
			this.toughness = toughness;
		}

		@Override
		public int getDurabilityForType(ArmorItem.Type slot) {
			// [VanillaCopy] ArmorMaterials
			int base = switch (slot) {
				case BOOTS -> 13;
				case LEGGINGS -> 15;
				case CHESTPLATE -> 16;
				case HELMET -> 11;
			};
			return durabilityMultiplier * base;
		}

		@Override
		public int getDefenseForType(ArmorItem.Type slot) {
			return this.damageReduction.get(slot);
		}

		@Override
		public int getEnchantmentValue() {
			return enchantability;
		}

		@NotNull
		@Override
		public SoundEvent getEquipSound() {
			return equipSound.get();
		}

		@NotNull
		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.of(repairItem.get());
		}

		@NotNull
		@Override
		public String getName() {
			return name;
		}

		@Override
		public float getToughness() {
			return toughness;
		}

		@Override
		public float getKnockbackResistance() {
			return 0;
		}
	}

	private enum ItemTier implements Tier {
		MANASTEEL(300, 6.2F, 2, 3, 20, () -> BotaniaItems.manaSteel),
		ELEMENTIUM(720, 6.2F, 2, 3, 20, () -> BotaniaItems.elementium),
		TERRASTEEL(2300, 9, 4, 4, 26, () -> BotaniaItems.terrasteel);

		private final int maxUses;
		private final float efficiency;
		private final float attackDamage;
		private final int harvestLevel;
		private final int enchantability;
		private final Supplier<Item> repairItem;

		ItemTier(int maxUses, float efficiency, float attackDamage, int harvestLevel, int enchantability, Supplier<Item> repairItem) {
			this.maxUses = maxUses;
			this.efficiency = efficiency;
			this.attackDamage = attackDamage;
			this.harvestLevel = harvestLevel;
			this.enchantability = enchantability;
			this.repairItem = repairItem;
		}

		@Override
		public int getUses() {
			return maxUses;
		}

		@Override
		public float getSpeed() {
			return efficiency;
		}

		@Override
		public float getAttackDamageBonus() {
			return attackDamage;
		}

		@Override
		public int getLevel() {
			return harvestLevel;
		}

		@Override
		public int getEnchantmentValue() {
			return enchantability;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.of(repairItem.get());
		}
	}

	private ConfigDataManager configDataManager = new ConfigDataManagerImpl();

	@Override
	public int apiVersion() {
		return 3;
	}

	@Nullable
	@Override
	public Registry<Brew> getBrewRegistry() {
		return RegistryHelper.getRegistry(BotaniaRegistries.BREWS);
	}

	@Override
	public net.minecraft.world.item.ArmorMaterial getManasteelArmorMaterial() {
		return ArmorMaterial.MANASTEEL;
	}

	@Override
	public net.minecraft.world.item.ArmorMaterial getElementiumArmorMaterial() {
		return ArmorMaterial.ELEMENTIUM;
	}

	@Override
	public net.minecraft.world.item.ArmorMaterial getManaweaveArmorMaterial() {
		return ArmorMaterial.MANAWEAVE;
	}

	@Override
	public net.minecraft.world.item.ArmorMaterial getTerrasteelArmorMaterial() {
		return ArmorMaterial.TERRASTEEL;
	}

	@Override
	public Tier getManasteelItemTier() {
		return ItemTier.MANASTEEL;
	}

	@Override
	public Tier getElementiumItemTier() {
		return ItemTier.ELEMENTIUM;
	}

	@Override
	public Tier getTerrasteelItemTier() {
		return ItemTier.TERRASTEEL;
	}

	@Override
	public ManaNetwork getManaNetworkInstance() {
		return ManaNetworkHandler.instance;
	}

	@Override
	public Container getAccessoriesInventory(Player player) {
		return EquipmentHandler.getAllWorn(player);
	}

	@Override
	public void breakOnAllCursors(Player player, ItemStack stack, BlockPos pos, Direction side) {
		RingOfLokiItem.breakOnAllCursors(player, stack, pos, side);
	}

	@Override
	public boolean hasSolegnoliaAround(Entity e) {
		return SolegnoliaBlockEntity.hasSolegnoliaAround(e);
	}

	@Override
	public void sparkleFX(Level world, double x, double y, double z, float r, float g, float b, float size, int m) {
		SparkleParticleData data = SparkleParticleData.sparkle(size, r, g, b, m);
		world.addParticle(data, x, y, z, 0, 0, 0);
	}

	private final Map<ResourceLocation, Function<DyeColor, Block>> paintableBlocks = new ConcurrentHashMap<>();

	@Override
	public Map<ResourceLocation, Function<DyeColor, Block>> getPaintableBlocks() {
		return Collections.unmodifiableMap(paintableBlocks);
	}

	@Override
	public void registerPaintableBlock(ResourceLocation block, Function<DyeColor, Block> transformer) {
		paintableBlocks.put(block, transformer);
	}

	@Override
	public void registerCorporeaNodeDetector(CorporeaNodeDetector detector) {
		CorporeaNodeDetectors.register(detector);
	}

	@Override
	public ConfigDataManager getConfigData() {
		return configDataManager;
	}

	@Override
	public void setConfigData(ConfigDataManager configDataManager) {
		this.configDataManager = configDataManager;
	}
}
