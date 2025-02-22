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
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.BotaniaRegistries;
import vazkii.botania.api.brew.Brew;
import vazkii.botania.api.configdata.ConfigDataManager;
import vazkii.botania.api.corporea.CorporeaNodeDetector;
import vazkii.botania.api.internal.ManaNetwork;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.block.flower.functional.SolegnoliaBlockEntity;
import vazkii.botania.common.config.ConfigDataManagerImpl;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.handler.ManaNetworkHandler;
import vazkii.botania.common.helper.RegistryHelper;
import vazkii.botania.common.integration.corporea.CorporeaNodeDetectors;
import vazkii.botania.common.item.BotaniaArmorMaterials;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.relic.RingOfLokiItem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class BotaniaAPIImpl implements BotaniaAPI {

	private enum ItemTier implements Tier {
		MANASTEEL(300, 6.2F, 2, 20,
				() -> BotaniaItems.manaSteel, BlockTags.INCORRECT_FOR_DIAMOND_TOOL),
		ELEMENTIUM(720, 6.2F, 2, 20,
				() -> BotaniaItems.elementium, BlockTags.INCORRECT_FOR_DIAMOND_TOOL),
		TERRASTEEL(2300, 9, 4, 26,
				() -> BotaniaItems.terrasteel, BlockTags.INCORRECT_FOR_NETHERITE_TOOL);

		private final int maxUses;
		private final float efficiency;
		private final float attackDamage;
		private final int enchantability;
		private final Supplier<Item> repairItem;
		private final TagKey<Block> incorrectBlockForDrops;

		ItemTier(int maxUses, float efficiency, float attackDamage, int enchantability,
				Supplier<Item> repairItem, TagKey<Block> incorrectBlockForDrops) {
			this.maxUses = maxUses;
			this.efficiency = efficiency;
			this.attackDamage = attackDamage;
			this.enchantability = enchantability;
			this.repairItem = repairItem;
			this.incorrectBlockForDrops = incorrectBlockForDrops;
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
		public TagKey<Block> getIncorrectBlocksForDrops() {
			return incorrectBlockForDrops;
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
		return 4;
	}

	@NotNull
	@Override
	public Registry<Brew> getBrewRegistry() {
		return RegistryHelper.getRegistry(BotaniaRegistries.BREWS);
	}

	@Override
	public Holder<ArmorMaterial> getManasteelArmorMaterial() {
		return BotaniaArmorMaterials.MANASTEEL;
	}

	@Override
	public Holder<ArmorMaterial> getElementiumArmorMaterial() {
		return BotaniaArmorMaterials.ELEMENTIUM;
	}

	@Override
	public Holder<ArmorMaterial> getManaweaveArmorMaterial() {
		return BotaniaArmorMaterials.MANAWEAVE;
	}

	@Override
	public Holder<ArmorMaterial> getTerrasteelArmorMaterial() {
		return BotaniaArmorMaterials.TERRASTEEL;
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

	@NotNull
	@Override
	public ConfigDataManager getConfigData() {
		return configDataManager;
	}

	@Override
	public void setConfigData(ConfigDataManager configDataManager) {
		this.configDataManager = configDataManager;
	}
}
