/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.data;

import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.BotaniaDoubleFlowerBlock;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.block.BotaniaGrassBlock;
import vazkii.botania.common.block.flower.generating.*;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.lib.LibBlockNames;
import vazkii.botania.common.lib.LibMisc;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class BlockLootProvider implements DataProvider {
	private static final LootItemCondition.Builder SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item()
			.hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
	private static final Function<Block, LootTable.Builder> SKIP = b -> {
		throw new RuntimeException("shouldn't be executed");
	};

	private final PackOutput.PathProvider pathProvider;
	private final Map<Block, Function<Block, LootTable.Builder>> functionTable = new HashMap<>();

	public BlockLootProvider(PackOutput packOutput) {
		this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables/blocks");

		for (Block b : BuiltInRegistries.BLOCK) {
			ResourceLocation id = BuiltInRegistries.BLOCK.getKey(b);
			if (!LibMisc.MOD_ID.equals(id.getNamespace())) {
				continue;
			}
			if (b instanceof SlabBlock) {
				functionTable.put(b, BlockLootProvider::genSlab);
			} else if (b instanceof BotaniaDoubleFlowerBlock) {
				functionTable.put(b, BlockLootProvider::genDoubleFlower);
			} else if (b instanceof BotaniaGrassBlock) {
				functionTable.put(b, BlockLootProvider::genAltGrass);
			} else if (b instanceof FlowerPotBlock flowerPot) {
				functionTable.put(b, block -> createPotAndPlantItemTable(flowerPot.getPotted()));
			} else if (id.getPath().matches(LibBlockNames.METAMORPHIC_PREFIX + "\\w+" + "_stone")) {
				functionTable.put(b, BlockLootProvider::genMetamorphicStone);
			}
		}

		// Empty
		functionTable.put(BotaniaBlocks.bifrost, BlockLootProvider::empty);
		functionTable.put(BotaniaBlocks.cocoon, BlockLootProvider::empty);
		functionTable.put(BotaniaBlocks.fakeAir, BlockLootProvider::empty);
		functionTable.put(BotaniaBlocks.manaFlame, BlockLootProvider::empty);

		// Redirects
		functionTable.put(BotaniaBlocks.cacophonium, b -> genRegular(Blocks.NOTE_BLOCK));
		functionTable.put(BotaniaBlocks.enchantedSoil, b -> genRegular(Blocks.DIRT));
		functionTable.put(BotaniaBlocks.enchanter, b -> genRegular(Blocks.LAPIS_BLOCK));

		// Special
		functionTable.put(BotaniaBlocks.cellBlock, BlockLootProvider::genCellBlock);
		functionTable.put(BotaniaBlocks.root, BlockLootProvider::genRoot);
		functionTable.put(BotaniaBlocks.solidVines, BlockLootProvider::genSolidVine);
		functionTable.put(BotaniaBlocks.tinyPotato, BlockLootProvider::genTinyPotato);

		// Flower NBT saving
		functionTable.put(BotaniaFlowerBlocks.gourmaryllis, b -> genCopyNbt(b, GourmaryllisBlockEntity.TAG_LAST_FOODS, GourmaryllisBlockEntity.TAG_LAST_FOOD_COUNT, GourmaryllisBlockEntity.TAG_STREAK_LENGTH));
		functionTable.put(BotaniaFlowerBlocks.gourmaryllisFloating, b -> genCopyNbt(b, GourmaryllisBlockEntity.TAG_LAST_FOODS, GourmaryllisBlockEntity.TAG_LAST_FOOD_COUNT, GourmaryllisBlockEntity.TAG_STREAK_LENGTH));
		functionTable.put(BotaniaFlowerBlocks.hydroangeas, b -> genCopyNbt(b, HydroangeasBlockEntity.TAG_COOLDOWN, HydroangeasBlockEntity.TAG_PASSIVE_DECAY_TICKS));
		functionTable.put(BotaniaFlowerBlocks.hydroangeasFloating, b -> genCopyNbt(b, HydroangeasBlockEntity.TAG_COOLDOWN, HydroangeasBlockEntity.TAG_PASSIVE_DECAY_TICKS));
		functionTable.put(BotaniaFlowerBlocks.munchdew, b -> genCopyNbt(b, MunchdewBlockEntity.TAG_COOLDOWN));
		functionTable.put(BotaniaFlowerBlocks.munchdewFloating, b -> genCopyNbt(b, MunchdewBlockEntity.TAG_COOLDOWN));
		functionTable.put(BotaniaFlowerBlocks.rafflowsia, b -> genCopyNbt(b, RafflowsiaBlockEntity.TAG_LAST_FLOWERS, RafflowsiaBlockEntity.TAG_LAST_FLOWER_TIMES));
		functionTable.put(BotaniaFlowerBlocks.rafflowsiaFloating, b -> genCopyNbt(b, RafflowsiaBlockEntity.TAG_LAST_FLOWERS, RafflowsiaBlockEntity.TAG_LAST_FLOWER_TIMES));
		functionTable.put(BotaniaFlowerBlocks.spectrolus, b -> genCopyNbt(b, SpectrolusBlockEntity.TAG_NEXT_COLOR));
		functionTable.put(BotaniaFlowerBlocks.spectrolusFloating, b -> genCopyNbt(b, SpectrolusBlockEntity.TAG_NEXT_COLOR));
		functionTable.put(BotaniaFlowerBlocks.thermalily, b -> genCopyNbt(b, HydroangeasBlockEntity.TAG_COOLDOWN));
		functionTable.put(BotaniaFlowerBlocks.thermalilyFloating, b -> genCopyNbt(b, HydroangeasBlockEntity.TAG_COOLDOWN));
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

		for (Block b : BuiltInRegistries.BLOCK) {
			ResourceLocation id = BuiltInRegistries.BLOCK.getKey(b);
			if (!LibMisc.MOD_ID.equals(id.getNamespace())) {
				continue;
			}
			Function<Block, LootTable.Builder> func = functionTable.getOrDefault(b, BlockLootProvider::genRegular);
			if (func != SKIP) {
				tables.put(id, func.apply(b));
			}
		}

		List<CompletableFuture<?>> output = new ArrayList<>();
		for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
			Path path = pathProvider.json(e.getKey());
			LootTable lootTable = e.getValue().setParamSet(LootContextParamSets.BLOCK).build();
			output.add(DataProvider.saveStable(cache, LootTable.CODEC, lootTable, path));
		}
		return CompletableFuture.allOf(output.toArray(CompletableFuture[]::new));
	}

	protected static LootTable.Builder empty(Block b) {
		return LootTable.lootTable();
	}

	@Nullable
	protected static LootTable.Builder skip(Block b) {
		return null;
	}

	protected static LootTable.Builder genCopyNbt(Block b, String... tags) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b);
		CopyNbtFunction.Builder func = CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY);
		for (String tag : tags) {
			func = func.copy(tag, "BlockEntityTag." + tag);
		}
		LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry)
				.when(ExplosionCondition.survivesExplosion())
				.apply(func);
		return LootTable.lootTable().withPool(pool);
	}

	protected static LootTable.Builder genCellBlock(Block b) {
		ItemPredicate.Builder silkPred = ItemPredicate.Builder.item()
				.hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1)));
		LootPoolEntryContainer.Builder<?> silk = LootItem.lootTableItem(b)
				.when(MatchTool.toolMatches(silkPred));
		return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(silk));
	}

	protected static LootTable.Builder genTinyPotato(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b)
				.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY));
		LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry)
				.when(ExplosionCondition.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	protected static LootTable.Builder genMetamorphicStone(Block b) {
		String cobbleName = BuiltInRegistries.BLOCK.getKey(b).getPath().replaceAll("_stone", "_cobblestone");
		Block cobble = BuiltInRegistries.BLOCK.getOptional(prefix(cobbleName)).get();
		return genSilkDrop(b, cobble);
	}

	protected static LootTable.Builder genSilkDrop(ItemLike silkDrop, ItemLike normalDrop) {
		LootPoolEntryContainer.Builder<?> cobbleDrop = LootItem.lootTableItem(normalDrop).when(ExplosionCondition.survivesExplosion());
		LootPoolEntryContainer.Builder<?> stoneDrop = LootItem.lootTableItem(silkDrop).when(SILK_TOUCH);

		return LootTable.lootTable().withPool(
				LootPool.lootPool().setRolls(ConstantValue.exactly(1))
						.add(stoneDrop.otherwise(cobbleDrop)));
	}

	protected static LootTable.Builder genSolidVine(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootTableReference.lootTableReference(new ResourceLocation("blocks/vine"));
		return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry));
	}

	protected static LootTable.Builder genRoot(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(BotaniaItems.livingroot)
				.apply(SetItemCountFunction.setCount(ConstantValue.exactly(4)))
				.apply(ApplyExplosionDecay.explosionDecay());
		return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry));
	}

	protected static LootTable.Builder genSlab(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b)
				.apply(SetItemCountFunction.setCount(ConstantValue.exactly(2))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE))))
				.apply(ApplyExplosionDecay.explosionDecay());
		return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry));
	}

	protected static LootTable.Builder genDoubleFlower(Block b) {
		var entry = LootItem.lootTableItem(b)
				.when(ExplosionCondition.survivesExplosion())
				.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b)
						.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(TallFlowerBlock.HALF, DoubleBlockHalf.LOWER)));
		return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(entry));
	}

	protected static LootTable.Builder genAltGrass(Block b) {
		LootPoolEntryContainer.Builder<?> silk = LootItem.lootTableItem(b)
				.when(SILK_TOUCH);
		LootPoolEntryContainer.Builder<?> dirt = LootItem.lootTableItem(Blocks.DIRT)
				.when(ExplosionCondition.survivesExplosion());
		LootPoolEntryContainer.Builder<?> entry = AlternativesEntry.alternatives(silk, dirt);
		LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry);
		return LootTable.lootTable().withPool(pool);
	}

	protected static LootTable.Builder genRegular(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b);
		LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry)
				.when(ExplosionCondition.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	protected static LootTable.Builder createPotAndPlantItemTable(ItemLike plant) {
		// based on BlockLootSubProvider.createPotFlowerItemTable(ItemLike)
		final var potPool = LootPool.lootPool().add(LootItem.lootTableItem(Blocks.FLOWER_POT))
				.setRolls(ConstantValue.exactly(1.0f))
				.when(ExplosionCondition.survivesExplosion());
		final var plantPool = LootPool.lootPool().add(LootItem.lootTableItem(plant))
				.setRolls(ConstantValue.exactly(1.0f))
				.when(ExplosionCondition.survivesExplosion());
		return LootTable.lootTable().withPool(potPool).withPool(plantPool);
	}

	@NotNull
	@Override
	public String getName() {
		return "Botania block loot tables";
	}
}
