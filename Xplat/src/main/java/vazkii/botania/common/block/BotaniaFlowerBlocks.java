/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import vazkii.botania.api.block.WandHUD;
import vazkii.botania.api.block_entity.BindableSpecialFlowerBlockEntity;
import vazkii.botania.api.block_entity.FunctionalFlowerBlockEntity;
import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;
import vazkii.botania.api.block_entity.SpecialFlowerBlockEntity;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.flower.ManastarBlockEntity;
import vazkii.botania.common.block.flower.PureDaisyBlockEntity;
import vazkii.botania.common.block.flower.functional.*;
import vazkii.botania.common.block.flower.generating.*;
import vazkii.botania.common.brew.BotaniaMobEffects;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.block.SpecialFlowerBlockItem;
import vazkii.botania.common.lib.LibBlockNames;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class BotaniaFlowerBlocks {
	private static final BlockBehaviour.Properties FLOWER_PROPS = BlockBehaviour.Properties.copy(Blocks.POPPY);
	private static final BlockBehaviour.Properties FLOATING_PROPS = BotaniaBlocks.FLOATING_PROPS;

	public static final Block pureDaisy = createSpecialFlowerBlock(BotaniaMobEffects.clear, 1, FLOWER_PROPS, () -> BotaniaFlowerBlocks.PURE_DAISY);
	public static final Block pureDaisyFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.PURE_DAISY);
	public static final Block pureDaisyPotted = BotaniaBlocks.flowerPot(pureDaisy, 0);

	public static final Block manastar = createSpecialFlowerBlock(MobEffects.GLOWING, 10, FLOWER_PROPS, () -> BotaniaFlowerBlocks.MANASTAR);
	public static final Block manastarFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.MANASTAR);
	public static final Block manastarPotted = BotaniaBlocks.flowerPot(manastar, 0);

	public static final Block hydroangeas = createSpecialFlowerBlock(MobEffects.UNLUCK, 10, FLOWER_PROPS, () -> BotaniaFlowerBlocks.HYDROANGEAS);
	public static final Block hydroangeasFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.HYDROANGEAS);
	public static final Block hydroangeasPotted = BotaniaBlocks.flowerPot(hydroangeas, 0);

	public static final Block endoflame = createSpecialFlowerBlock(MobEffects.MOVEMENT_SLOWDOWN, 10, FLOWER_PROPS, () -> BotaniaFlowerBlocks.ENDOFLAME);
	public static final Block endoflameFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.ENDOFLAME);
	public static final Block endoflamePotted = BotaniaBlocks.flowerPot(endoflame, 0);

	public static final Block thermalily = createSpecialFlowerBlock(MobEffects.FIRE_RESISTANCE, 120, FLOWER_PROPS, () -> BotaniaFlowerBlocks.THERMALILY);
	public static final Block thermalilyFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.THERMALILY);
	public static final Block thermalilyPotted = BotaniaBlocks.flowerPot(thermalily, 0);

	public static final Block rosaArcana = createSpecialFlowerBlock(MobEffects.LUCK, 64, FLOWER_PROPS, () -> BotaniaFlowerBlocks.ROSA_ARCANA);
	public static final Block rosaArcanaFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.ROSA_ARCANA);
	public static final Block rosaArcanaPotted = BotaniaBlocks.flowerPot(rosaArcana, 0);

	public static final Block munchdew = createSpecialFlowerBlock(MobEffects.SLOW_FALLING, 300, FLOWER_PROPS, () -> BotaniaFlowerBlocks.MUNCHDEW);
	public static final Block munchdewFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.MUNCHDEW);
	public static final Block munchdewPotted = BotaniaBlocks.flowerPot(munchdew, 0);

	public static final Block entropinnyum = createSpecialFlowerBlock(MobEffects.DAMAGE_RESISTANCE, 72, FLOWER_PROPS, () -> BotaniaFlowerBlocks.ENTROPINNYUM);
	public static final Block entropinnyumFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.ENTROPINNYUM);
	public static final Block entropinnyumPotted = BotaniaBlocks.flowerPot(entropinnyum, 0);

	public static final Block kekimurus = createSpecialFlowerBlock(MobEffects.SATURATION, 15, FLOWER_PROPS, () -> BotaniaFlowerBlocks.KEKIMURUS);
	public static final Block kekimurusFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.KEKIMURUS);
	public static final Block kekimurusPotted = BotaniaBlocks.flowerPot(kekimurus, 0);

	public static final Block gourmaryllis = createSpecialFlowerBlock(MobEffects.HUNGER, 180, FLOWER_PROPS, () -> BotaniaFlowerBlocks.GOURMARYLLIS);
	public static final Block gourmaryllisFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.GOURMARYLLIS);
	public static final Block gourmaryllisPotted = BotaniaBlocks.flowerPot(gourmaryllis, 0);

	public static final Block narslimmus = createSpecialFlowerBlock(BotaniaMobEffects.featherfeet, 240, FLOWER_PROPS, () -> BotaniaFlowerBlocks.NARSLIMMUS);
	public static final Block narslimmusFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.NARSLIMMUS);
	public static final Block narslimmusPotted = BotaniaBlocks.flowerPot(narslimmus, 0);

	public static final Block spectrolus = createSpecialFlowerBlock(MobEffects.BLINDNESS, 240, FLOWER_PROPS, () -> BotaniaFlowerBlocks.SPECTROLUS);
	public static final Block spectrolusFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.SPECTROLUS);
	public static final Block spectrolusPotted = BotaniaBlocks.flowerPot(spectrolus, 0);

	public static final Block dandelifeon = createSpecialFlowerBlock(MobEffects.CONFUSION, 240, FLOWER_PROPS, () -> BotaniaFlowerBlocks.DANDELIFEON);
	public static final Block dandelifeonFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.DANDELIFEON);
	public static final Block dandelifeonPotted = BotaniaBlocks.flowerPot(dandelifeon, 0);

	public static final Block rafflowsia = createSpecialFlowerBlock(MobEffects.HEALTH_BOOST, 18, FLOWER_PROPS, () -> BotaniaFlowerBlocks.RAFFLOWSIA);
	public static final Block rafflowsiaFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.RAFFLOWSIA);
	public static final Block rafflowsiaPotted = BotaniaBlocks.flowerPot(rafflowsia, 0);

	public static final Block shulkMeNot = createSpecialFlowerBlock(MobEffects.LEVITATION, 72, FLOWER_PROPS, () -> BotaniaFlowerBlocks.SHULK_ME_NOT);
	public static final Block shulkMeNotFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.SHULK_ME_NOT);
	public static final Block shulkMeNotPotted = BotaniaBlocks.flowerPot(shulkMeNot, 0);

	public static final Block bellethorn = createSpecialFlowerBlock(MobEffects.WITHER, 10, FLOWER_PROPS, () -> BotaniaFlowerBlocks.BELLETHORNE);
	public static final Block bellethornChibi = createSpecialFlowerBlock(MobEffects.WITHER, 10, FLOWER_PROPS, () -> BotaniaFlowerBlocks.BELLETHORNE_CHIBI);
	public static final Block bellethornFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.BELLETHORNE);
	public static final Block bellethornChibiFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.BELLETHORNE_CHIBI);
	public static final Block bellethornPotted = BotaniaBlocks.flowerPot(bellethorn, 0);
	public static final Block bellethornChibiPotted = BotaniaBlocks.flowerPot(bellethornChibi, 0);

	public static final Block bergamute = createSpecialFlowerBlock(MobEffects.BLINDNESS, 10, FLOWER_PROPS, () -> BotaniaFlowerBlocks.BERGAMUTE);
	public static final Block bergamuteFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.BERGAMUTE);
	public static final Block bergamutePotted = BotaniaBlocks.flowerPot(bergamute, 0);

	public static final Block dreadthorn = createSpecialFlowerBlock(MobEffects.WITHER, 10, FLOWER_PROPS, () -> BotaniaFlowerBlocks.DREADTHORN);
	public static final Block dreadthornFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.DREADTHORN);
	public static final Block dreadthornPotted = BotaniaBlocks.flowerPot(dreadthorn, 0);

	public static final Block heiseiDream = createSpecialFlowerBlock(BotaniaMobEffects.soulCross, 300, FLOWER_PROPS, () -> BotaniaFlowerBlocks.HEISEI_DREAM);
	public static final Block heiseiDreamFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.HEISEI_DREAM);
	public static final Block heiseiDreamPotted = BotaniaBlocks.flowerPot(heiseiDream, 0);

	public static final Block tigerseye = createSpecialFlowerBlock(MobEffects.DAMAGE_BOOST, 90, FLOWER_PROPS, () -> BotaniaFlowerBlocks.TIGERSEYE);
	public static final Block tigerseyeFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.TIGERSEYE);
	public static final Block tigerseyePotted = BotaniaBlocks.flowerPot(tigerseye, 0);

	public static final Block jadedAmaranthus = createSpecialFlowerBlock(MobEffects.HEAL, 1, FLOWER_PROPS, () -> BotaniaFlowerBlocks.JADED_AMARANTHUS);
	public static final Block jadedAmaranthusFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.JADED_AMARANTHUS);
	public static final Block jadedAmaranthusPotted = BotaniaBlocks.flowerPot(jadedAmaranthus, 0);

	public static final Block orechid = createSpecialFlowerBlock(MobEffects.DIG_SPEED, 10, FLOWER_PROPS, () -> BotaniaFlowerBlocks.ORECHID);
	public static final Block orechidFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.ORECHID);
	public static final Block orechidPotted = BotaniaBlocks.flowerPot(orechid, 0);

	public static final Block fallenKanade = createSpecialFlowerBlock(MobEffects.REGENERATION, 90, FLOWER_PROPS, () -> BotaniaFlowerBlocks.FALLEN_KANADE);
	public static final Block fallenKanadeFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.FALLEN_KANADE);
	public static final Block fallenKanadePotted = BotaniaBlocks.flowerPot(fallenKanade, 0);

	public static final Block exoflame = createSpecialFlowerBlock(MobEffects.MOVEMENT_SPEED, 240, FLOWER_PROPS, () -> BotaniaFlowerBlocks.EXOFLAME);
	public static final Block exoflameFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.EXOFLAME);
	public static final Block exoflamePotted = BotaniaBlocks.flowerPot(exoflame, 0);

	public static final Block agricarnation = createSpecialFlowerBlock(MobEffects.ABSORPTION, 48, FLOWER_PROPS, () -> BotaniaFlowerBlocks.AGRICARNATION);
	public static final Block agricarnationChibi = createSpecialFlowerBlock(MobEffects.ABSORPTION, 48, FLOWER_PROPS, () -> BotaniaFlowerBlocks.AGRICARNATION_CHIBI);
	public static final Block agricarnationFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.AGRICARNATION);
	public static final Block agricarnationChibiFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.AGRICARNATION_CHIBI);
	public static final Block agricarnationPotted = BotaniaBlocks.flowerPot(agricarnation, 0);
	public static final Block agricarnationChibiPotted = BotaniaBlocks.flowerPot(agricarnationChibi, 0);

	public static final Block hopperhock = createSpecialFlowerBlock(MobEffects.MOVEMENT_SPEED, 30, FLOWER_PROPS, () -> BotaniaFlowerBlocks.HOPPERHOCK);
	public static final Block hopperhockChibi = createSpecialFlowerBlock(MobEffects.MOVEMENT_SPEED, 30, FLOWER_PROPS, () -> BotaniaFlowerBlocks.HOPPERHOCK_CHIBI);
	public static final Block hopperhockFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.HOPPERHOCK);
	public static final Block hopperhockChibiFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.HOPPERHOCK_CHIBI);
	public static final Block hopperhockPotted = BotaniaBlocks.flowerPot(hopperhock, 0);
	public static final Block hopperhockChibiPotted = BotaniaBlocks.flowerPot(hopperhockChibi, 0);

	public static final Block tangleberrie = createSpecialFlowerBlock(BotaniaMobEffects.bloodthrst, 120, FLOWER_PROPS, () -> BotaniaFlowerBlocks.TANGLEBERRIE);
	public static final Block tangleberrieChibi = createSpecialFlowerBlock(BotaniaMobEffects.bloodthrst, 120, FLOWER_PROPS, () -> BotaniaFlowerBlocks.TANGLEBERRIE_CHIBI);
	public static final Block tangleberrieFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.TANGLEBERRIE);
	public static final Block tangleberrieChibiFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.TANGLEBERRIE_CHIBI);
	public static final Block tangleberriePotted = BotaniaBlocks.flowerPot(tangleberrie, 0);
	public static final Block tangleberrieChibiPotted = BotaniaBlocks.flowerPot(tangleberrieChibi, 0);

	public static final Block jiyuulia = createSpecialFlowerBlock(BotaniaMobEffects.emptiness, 120, FLOWER_PROPS, () -> BotaniaFlowerBlocks.JIYUULIA);
	public static final Block jiyuuliaChibi = createSpecialFlowerBlock(BotaniaMobEffects.emptiness, 120, FLOWER_PROPS, () -> BotaniaFlowerBlocks.JIYUULIA_CHIBI);
	public static final Block jiyuuliaFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.JIYUULIA);
	public static final Block jiyuuliaChibiFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.JIYUULIA_CHIBI);
	public static final Block jiyuuliaPotted = BotaniaBlocks.flowerPot(jiyuulia, 0);
	public static final Block jiyuuliaChibiPotted = BotaniaBlocks.flowerPot(jiyuuliaChibi, 0);

	public static final Block rannuncarpus = createSpecialFlowerBlock(MobEffects.JUMP, 30, FLOWER_PROPS, () -> BotaniaFlowerBlocks.RANNUNCARPUS);
	public static final Block rannuncarpusChibi = createSpecialFlowerBlock(MobEffects.JUMP, 30, FLOWER_PROPS, () -> BotaniaFlowerBlocks.RANNUNCARPUS_CHIBI);
	public static final Block rannuncarpusFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.RANNUNCARPUS);
	public static final Block rannuncarpusChibiFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.RANNUNCARPUS_CHIBI);
	public static final Block rannuncarpusPotted = BotaniaBlocks.flowerPot(rannuncarpus, 0);
	public static final Block rannuncarpusChibiPotted = BotaniaBlocks.flowerPot(rannuncarpusChibi, 0);

	public static final Block hyacidus = createSpecialFlowerBlock(MobEffects.POISON, 48, FLOWER_PROPS, () -> BotaniaFlowerBlocks.HYACIDUS);
	public static final Block hyacidusFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.HYACIDUS);
	public static final Block hyacidusPotted = BotaniaBlocks.flowerPot(hyacidus, 0);

	public static final Block pollidisiac = createSpecialFlowerBlock(MobEffects.DIG_SPEED, 369, FLOWER_PROPS, () -> BotaniaFlowerBlocks.POLLIDISIAC);
	public static final Block pollidisiacFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.POLLIDISIAC);
	public static final Block pollidisiacPotted = BotaniaBlocks.flowerPot(pollidisiac, 0);

	public static final Block clayconia = createSpecialFlowerBlock(MobEffects.WEAKNESS, 30, FLOWER_PROPS, () -> BotaniaFlowerBlocks.CLAYCONIA);
	public static final Block clayconiaChibi = createSpecialFlowerBlock(MobEffects.WEAKNESS, 30, FLOWER_PROPS, () -> BotaniaFlowerBlocks.CLAYCONIA_CHIBI);
	public static final Block clayconiaFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.CLAYCONIA);
	public static final Block clayconiaChibiFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.CLAYCONIA_CHIBI);
	public static final Block clayconiaPotted = BotaniaBlocks.flowerPot(clayconia, 0);
	public static final Block clayconiaChibiPotted = BotaniaBlocks.flowerPot(clayconiaChibi, 0);

	public static final Block loonium = createSpecialFlowerBlock(BotaniaMobEffects.allure, 900, FLOWER_PROPS, () -> BotaniaFlowerBlocks.LOONIUM);
	public static final Block looniumFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.LOONIUM);
	public static final Block looniumPotted = BotaniaBlocks.flowerPot(loonium, 0);

	public static final Block daffomill = createSpecialFlowerBlock(MobEffects.LEVITATION, 6, FLOWER_PROPS, () -> BotaniaFlowerBlocks.DAFFOMILL);
	public static final Block daffomillFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.DAFFOMILL);
	public static final Block daffomillPotted = BotaniaBlocks.flowerPot(daffomill, 0);

	public static final Block vinculotus = createSpecialFlowerBlock(MobEffects.NIGHT_VISION, 900, FLOWER_PROPS, () -> BotaniaFlowerBlocks.VINCULOTUS);
	public static final Block vinculotusFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.VINCULOTUS);
	public static final Block vinculotusPotted = BotaniaBlocks.flowerPot(vinculotus, 0);

	public static final Block spectranthemum = createSpecialFlowerBlock(MobEffects.INVISIBILITY, 360, FLOWER_PROPS, () -> BotaniaFlowerBlocks.SPECTRANTHEMUM);
	public static final Block spectranthemumFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.SPECTRANTHEMUM);
	public static final Block spectranthemumPotted = BotaniaBlocks.flowerPot(spectranthemum, 0);

	public static final Block medumone = createSpecialFlowerBlock(MobEffects.MOVEMENT_SLOWDOWN, 3600, FLOWER_PROPS, () -> BotaniaFlowerBlocks.MEDUMONE);
	public static final Block medumoneFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.MEDUMONE);
	public static final Block medumonePotted = BotaniaBlocks.flowerPot(medumone, 0);

	public static final Block marimorphosis = createSpecialFlowerBlock(MobEffects.DIG_SLOWDOWN, 60, FLOWER_PROPS, () -> BotaniaFlowerBlocks.MARIMORPHOSIS);
	public static final Block marimorphosisChibi = createSpecialFlowerBlock(MobEffects.DIG_SLOWDOWN, 60, FLOWER_PROPS, () -> BotaniaFlowerBlocks.MARIMORPHOSIS_CHIBI);
	public static final Block marimorphosisFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.MARIMORPHOSIS);
	public static final Block marimorphosisChibiFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.MARIMORPHOSIS_CHIBI);
	public static final Block marimorphosisPotted = BotaniaBlocks.flowerPot(marimorphosis, 0);
	public static final Block marimorphosisChibiPotted = BotaniaBlocks.flowerPot(marimorphosisChibi, 0);

	public static final Block bubbell = createSpecialFlowerBlock(MobEffects.WATER_BREATHING, 240, FLOWER_PROPS, () -> BotaniaFlowerBlocks.BUBBELL);
	public static final Block bubbellChibi = createSpecialFlowerBlock(MobEffects.WATER_BREATHING, 240, FLOWER_PROPS, () -> BotaniaFlowerBlocks.BUBBELL_CHIBI);
	public static final Block bubbellFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.BUBBELL);
	public static final Block bubbellChibiFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.BUBBELL_CHIBI);
	public static final Block bubbellPotted = BotaniaBlocks.flowerPot(bubbell, 0);
	public static final Block bubbellChibiPotted = BotaniaBlocks.flowerPot(bubbellChibi, 0);

	public static final Block solegnolia = createSpecialFlowerBlock(MobEffects.HARM, 1, FLOWER_PROPS, () -> BotaniaFlowerBlocks.SOLEGNOLIA);
	public static final Block solegnoliaChibi = createSpecialFlowerBlock(MobEffects.HARM, 1, FLOWER_PROPS, () -> BotaniaFlowerBlocks.SOLEGNOLIA_CHIBI);
	public static final Block solegnoliaFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.SOLEGNOLIA);
	public static final Block solegnoliaChibiFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.SOLEGNOLIA_CHIBI);
	public static final Block solegnoliaPotted = BotaniaBlocks.flowerPot(solegnolia, 0);
	public static final Block solegnoliaChibiPotted = BotaniaBlocks.flowerPot(solegnoliaChibi, 0);

	public static final Block orechidIgnem = createSpecialFlowerBlock(MobEffects.FIRE_RESISTANCE, 600, FLOWER_PROPS, () -> BotaniaFlowerBlocks.ORECHID_IGNEM);
	public static final Block orechidIgnemFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.ORECHID_IGNEM);
	public static final Block orechidIgnemPotted = BotaniaBlocks.flowerPot(orechidIgnem, 0);

	public static final Block labellia = createSpecialFlowerBlock(MobEffects.FIRE_RESISTANCE, 600, FLOWER_PROPS, () -> BotaniaFlowerBlocks.LABELLIA);
	public static final Block labelliaFloating = new FloatingSpecialFlowerBlock(FLOATING_PROPS, () -> BotaniaFlowerBlocks.LABELLIA);
	public static final Block labelliaPotted = BotaniaBlocks.flowerPot(labellia, 0);

	public static final BlockEntityType<PureDaisyBlockEntity> PURE_DAISY = XplatAbstractions.INSTANCE.createBlockEntityType(PureDaisyBlockEntity::new, pureDaisy, pureDaisyFloating);
	public static final BlockEntityType<ManastarBlockEntity> MANASTAR = XplatAbstractions.INSTANCE.createBlockEntityType(ManastarBlockEntity::new, manastar, manastarFloating);
	public static final BlockEntityType<HydroangeasBlockEntity> HYDROANGEAS = XplatAbstractions.INSTANCE.createBlockEntityType(HydroangeasBlockEntity::new, hydroangeas, hydroangeasFloating);
	public static final BlockEntityType<EndoflameBlockEntity> ENDOFLAME = XplatAbstractions.INSTANCE.createBlockEntityType(EndoflameBlockEntity::new, endoflame, endoflameFloating);
	public static final BlockEntityType<ThermalilyBlockEntity> THERMALILY = XplatAbstractions.INSTANCE.createBlockEntityType(ThermalilyBlockEntity::new, thermalily, thermalilyFloating);
	public static final BlockEntityType<RosaArcanaBlockEntity> ROSA_ARCANA = XplatAbstractions.INSTANCE.createBlockEntityType(RosaArcanaBlockEntity::new, rosaArcana, rosaArcanaFloating);
	public static final BlockEntityType<MunchdewBlockEntity> MUNCHDEW = XplatAbstractions.INSTANCE.createBlockEntityType(MunchdewBlockEntity::new, munchdew, munchdewFloating);
	public static final BlockEntityType<EntropinnyumBlockEntity> ENTROPINNYUM = XplatAbstractions.INSTANCE.createBlockEntityType(EntropinnyumBlockEntity::new, entropinnyum, entropinnyumFloating);
	public static final BlockEntityType<KekimurusBlockEntity> KEKIMURUS = XplatAbstractions.INSTANCE.createBlockEntityType(KekimurusBlockEntity::new, kekimurus, kekimurusFloating);
	public static final BlockEntityType<GourmaryllisBlockEntity> GOURMARYLLIS = XplatAbstractions.INSTANCE.createBlockEntityType(GourmaryllisBlockEntity::new, gourmaryllis, gourmaryllisFloating);
	public static final BlockEntityType<NarslimmusBlockEntity> NARSLIMMUS = XplatAbstractions.INSTANCE.createBlockEntityType(NarslimmusBlockEntity::new, narslimmus, narslimmusFloating);
	public static final BlockEntityType<SpectrolusBlockEntity> SPECTROLUS = XplatAbstractions.INSTANCE.createBlockEntityType(SpectrolusBlockEntity::new, spectrolus, spectrolusFloating);
	public static final BlockEntityType<DandelifeonBlockEntity> DANDELIFEON = XplatAbstractions.INSTANCE.createBlockEntityType(DandelifeonBlockEntity::new, dandelifeon, dandelifeonFloating);
	public static final BlockEntityType<RafflowsiaBlockEntity> RAFFLOWSIA = XplatAbstractions.INSTANCE.createBlockEntityType(RafflowsiaBlockEntity::new, rafflowsia, rafflowsiaFloating);
	public static final BlockEntityType<ShulkMeNotBlockEntity> SHULK_ME_NOT = XplatAbstractions.INSTANCE.createBlockEntityType(ShulkMeNotBlockEntity::new, shulkMeNot, shulkMeNotFloating);
	public static final BlockEntityType<BellethornBlockEntity> BELLETHORNE = XplatAbstractions.INSTANCE.createBlockEntityType(BellethornBlockEntity::new, bellethorn, bellethornFloating);
	public static final BlockEntityType<BellethornBlockEntity.Mini> BELLETHORNE_CHIBI = XplatAbstractions.INSTANCE.createBlockEntityType(BellethornBlockEntity.Mini::new, bellethornChibi, bellethornChibiFloating);
	public static final BlockEntityType<BergamuteBlockEntity> BERGAMUTE = XplatAbstractions.INSTANCE.createBlockEntityType(BergamuteBlockEntity::new, bergamute, bergamuteFloating);
	public static final BlockEntityType<DreadthornBlockEntity> DREADTHORN = XplatAbstractions.INSTANCE.createBlockEntityType(DreadthornBlockEntity::new, dreadthorn, dreadthornFloating);
	public static final BlockEntityType<HeiseiDreamBlockEntity> HEISEI_DREAM = XplatAbstractions.INSTANCE.createBlockEntityType(HeiseiDreamBlockEntity::new, heiseiDream, heiseiDreamFloating);
	public static final BlockEntityType<TigerseyeBlockEntity> TIGERSEYE = XplatAbstractions.INSTANCE.createBlockEntityType(TigerseyeBlockEntity::new, tigerseye, tigerseyeFloating);
	public static final BlockEntityType<JadedAmaranthusBlockEntity> JADED_AMARANTHUS = XplatAbstractions.INSTANCE.createBlockEntityType(JadedAmaranthusBlockEntity::new, jadedAmaranthus, jadedAmaranthusFloating);
	public static final BlockEntityType<OrechidBlockEntity> ORECHID = XplatAbstractions.INSTANCE.createBlockEntityType(OrechidBlockEntity::new, orechid, orechidFloating);
	public static final BlockEntityType<FallenKanadeBlockEntity> FALLEN_KANADE = XplatAbstractions.INSTANCE.createBlockEntityType(FallenKanadeBlockEntity::new, fallenKanade, fallenKanadeFloating);
	public static final BlockEntityType<ExoflameBlockEntity> EXOFLAME = XplatAbstractions.INSTANCE.createBlockEntityType(ExoflameBlockEntity::new, exoflame, exoflameFloating);
	public static final BlockEntityType<AgricarnationBlockEntity> AGRICARNATION = XplatAbstractions.INSTANCE.createBlockEntityType(AgricarnationBlockEntity::new, agricarnation, agricarnationFloating);
	public static final BlockEntityType<AgricarnationBlockEntity.Mini> AGRICARNATION_CHIBI = XplatAbstractions.INSTANCE.createBlockEntityType(AgricarnationBlockEntity.Mini::new, agricarnationChibi, agricarnationChibiFloating);
	public static final BlockEntityType<HopperhockBlockEntity> HOPPERHOCK = XplatAbstractions.INSTANCE.createBlockEntityType(HopperhockBlockEntity::new, hopperhock, hopperhockFloating);
	public static final BlockEntityType<HopperhockBlockEntity.Mini> HOPPERHOCK_CHIBI = XplatAbstractions.INSTANCE.createBlockEntityType(HopperhockBlockEntity.Mini::new, hopperhockChibi, hopperhockChibiFloating);
	public static final BlockEntityType<TangleberrieBlockEntity> TANGLEBERRIE = XplatAbstractions.INSTANCE.createBlockEntityType(TangleberrieBlockEntity::new, tangleberrie, tangleberrieFloating);
	public static final BlockEntityType<TangleberrieBlockEntity.Mini> TANGLEBERRIE_CHIBI = XplatAbstractions.INSTANCE.createBlockEntityType(TangleberrieBlockEntity.Mini::new, tangleberrieChibi, tangleberrieChibiFloating);
	public static final BlockEntityType<JiyuuliaBlockEntity> JIYUULIA = XplatAbstractions.INSTANCE.createBlockEntityType(JiyuuliaBlockEntity::new, jiyuulia, jiyuuliaFloating);
	public static final BlockEntityType<JiyuuliaBlockEntity.Mini> JIYUULIA_CHIBI = XplatAbstractions.INSTANCE.createBlockEntityType(JiyuuliaBlockEntity.Mini::new, jiyuuliaChibi, jiyuuliaChibiFloating);
	public static final BlockEntityType<RannuncarpusBlockEntity> RANNUNCARPUS = XplatAbstractions.INSTANCE.createBlockEntityType(RannuncarpusBlockEntity::new, rannuncarpus, rannuncarpusFloating);
	public static final BlockEntityType<RannuncarpusBlockEntity.Mini> RANNUNCARPUS_CHIBI = XplatAbstractions.INSTANCE.createBlockEntityType(RannuncarpusBlockEntity.Mini::new, rannuncarpusChibi, rannuncarpusChibiFloating);
	public static final BlockEntityType<HyacidusBlockEntity> HYACIDUS = XplatAbstractions.INSTANCE.createBlockEntityType(HyacidusBlockEntity::new, hyacidus, hyacidusFloating);
	public static final BlockEntityType<LabelliaBlockEntity> LABELLIA = XplatAbstractions.INSTANCE.createBlockEntityType(LabelliaBlockEntity::new, labellia, labelliaFloating);
	public static final BlockEntityType<PollidisiacBlockEntity> POLLIDISIAC = XplatAbstractions.INSTANCE.createBlockEntityType(PollidisiacBlockEntity::new, pollidisiac, pollidisiacFloating);
	public static final BlockEntityType<ClayconiaBlockEntity> CLAYCONIA = XplatAbstractions.INSTANCE.createBlockEntityType(ClayconiaBlockEntity::new, clayconia, clayconiaFloating);
	public static final BlockEntityType<ClayconiaBlockEntity.Mini> CLAYCONIA_CHIBI = XplatAbstractions.INSTANCE.createBlockEntityType(ClayconiaBlockEntity.Mini::new, clayconiaChibi, clayconiaChibiFloating);
	public static final BlockEntityType<LooniumBlockEntity> LOONIUM = XplatAbstractions.INSTANCE.createBlockEntityType(LooniumBlockEntity::new, loonium, looniumFloating);
	public static final BlockEntityType<DaffomillBlockEntity> DAFFOMILL = XplatAbstractions.INSTANCE.createBlockEntityType(DaffomillBlockEntity::new, daffomill, daffomillFloating);
	public static final BlockEntityType<VinculotusBlockEntity> VINCULOTUS = XplatAbstractions.INSTANCE.createBlockEntityType(VinculotusBlockEntity::new, vinculotus, vinculotusFloating);
	public static final BlockEntityType<SpectranthemumBlockEntity> SPECTRANTHEMUM = XplatAbstractions.INSTANCE.createBlockEntityType(SpectranthemumBlockEntity::new, spectranthemum, spectranthemumFloating);
	public static final BlockEntityType<MedumoneBlockEntity> MEDUMONE = XplatAbstractions.INSTANCE.createBlockEntityType(MedumoneBlockEntity::new, medumone, medumoneFloating);
	public static final BlockEntityType<MarimorphosisBlockEntity> MARIMORPHOSIS = XplatAbstractions.INSTANCE.createBlockEntityType(MarimorphosisBlockEntity::new, marimorphosis, marimorphosisFloating);
	public static final BlockEntityType<MarimorphosisBlockEntity.Mini> MARIMORPHOSIS_CHIBI = XplatAbstractions.INSTANCE.createBlockEntityType(MarimorphosisBlockEntity.Mini::new, marimorphosisChibi, marimorphosisChibiFloating);
	public static final BlockEntityType<BubbellBlockEntity> BUBBELL = XplatAbstractions.INSTANCE.createBlockEntityType(BubbellBlockEntity::new, bubbell, bubbellFloating);
	public static final BlockEntityType<BubbellBlockEntity.Mini> BUBBELL_CHIBI = XplatAbstractions.INSTANCE.createBlockEntityType(BubbellBlockEntity.Mini::new, bubbellChibi, bubbellChibiFloating);
	public static final BlockEntityType<SolegnoliaBlockEntity> SOLEGNOLIA = XplatAbstractions.INSTANCE.createBlockEntityType(SolegnoliaBlockEntity::new, solegnolia, solegnoliaFloating);
	public static final BlockEntityType<SolegnoliaBlockEntity.Mini> SOLEGNOLIA_CHIBI = XplatAbstractions.INSTANCE.createBlockEntityType(SolegnoliaBlockEntity.Mini::new, solegnoliaChibi, solegnoliaChibiFloating);
	public static final BlockEntityType<OrechidIgnemBlockEntity> ORECHID_IGNEM = XplatAbstractions.INSTANCE.createBlockEntityType(OrechidIgnemBlockEntity::new, orechidIgnem, orechidIgnemFloating);

	private static ResourceLocation floating(ResourceLocation orig) {
		return new ResourceLocation(orig.getNamespace(), "floating_" + orig.getPath());
	}

	private static ResourceLocation potted(ResourceLocation orig) {
		return new ResourceLocation(orig.getNamespace(), "potted_" + orig.getPath());
	}

	private static ResourceLocation chibi(ResourceLocation orig) {
		return new ResourceLocation(orig.getNamespace(), orig.getPath() + "_chibi");
	}

	private static ResourceLocation getId(Block b) {
		return BuiltInRegistries.BLOCK.getKey(b);
	}

	private static FlowerBlock createSpecialFlowerBlock(
			MobEffect effect, int effectDuration,
			BlockBehaviour.Properties props,
			Supplier<BlockEntityType<? extends SpecialFlowerBlockEntity>> beType) {
		return XplatAbstractions.INSTANCE.createSpecialFlowerBlock(
				effect, effectDuration, props, beType
		);
	}

	public static void registerBlocks(BiConsumer<Block, ResourceLocation> r) {
		r.accept(pureDaisy, LibBlockNames.SUBTILE_PUREDAISY);
		r.accept(pureDaisyFloating, floating(LibBlockNames.SUBTILE_PUREDAISY));
		r.accept(pureDaisyPotted, potted(LibBlockNames.SUBTILE_PUREDAISY));

		r.accept(manastar, LibBlockNames.SUBTILE_MANASTAR);
		r.accept(manastarFloating, floating(LibBlockNames.SUBTILE_MANASTAR));
		r.accept(manastarPotted, potted(LibBlockNames.SUBTILE_MANASTAR));

		r.accept(hydroangeas, LibBlockNames.SUBTILE_HYDROANGEAS);
		r.accept(hydroangeasFloating, floating(LibBlockNames.SUBTILE_HYDROANGEAS));
		r.accept(hydroangeasPotted, potted(LibBlockNames.SUBTILE_HYDROANGEAS));

		r.accept(endoflame, LibBlockNames.SUBTILE_ENDOFLAME);
		r.accept(endoflameFloating, floating(LibBlockNames.SUBTILE_ENDOFLAME));
		r.accept(endoflamePotted, potted(LibBlockNames.SUBTILE_ENDOFLAME));

		r.accept(thermalily, LibBlockNames.SUBTILE_THERMALILY);
		r.accept(thermalilyFloating, floating(LibBlockNames.SUBTILE_THERMALILY));
		r.accept(thermalilyPotted, potted(LibBlockNames.SUBTILE_THERMALILY));

		r.accept(rosaArcana, LibBlockNames.SUBTILE_ARCANE_ROSE);
		r.accept(rosaArcanaFloating, floating(LibBlockNames.SUBTILE_ARCANE_ROSE));
		r.accept(rosaArcanaPotted, potted(LibBlockNames.SUBTILE_ARCANE_ROSE));

		r.accept(munchdew, LibBlockNames.SUBTILE_MUNCHDEW);
		r.accept(munchdewFloating, floating(LibBlockNames.SUBTILE_MUNCHDEW));
		r.accept(munchdewPotted, potted(LibBlockNames.SUBTILE_MUNCHDEW));

		r.accept(entropinnyum, LibBlockNames.SUBTILE_ENTROPINNYUM);
		r.accept(entropinnyumFloating, floating(LibBlockNames.SUBTILE_ENTROPINNYUM));
		r.accept(entropinnyumPotted, potted(LibBlockNames.SUBTILE_ENTROPINNYUM));

		r.accept(kekimurus, LibBlockNames.SUBTILE_KEKIMURUS);
		r.accept(kekimurusFloating, floating(LibBlockNames.SUBTILE_KEKIMURUS));
		r.accept(kekimurusPotted, potted(LibBlockNames.SUBTILE_KEKIMURUS));

		r.accept(gourmaryllis, LibBlockNames.SUBTILE_GOURMARYLLIS);
		r.accept(gourmaryllisFloating, floating(LibBlockNames.SUBTILE_GOURMARYLLIS));
		r.accept(gourmaryllisPotted, potted(LibBlockNames.SUBTILE_GOURMARYLLIS));

		r.accept(narslimmus, LibBlockNames.SUBTILE_NARSLIMMUS);
		r.accept(narslimmusFloating, floating(LibBlockNames.SUBTILE_NARSLIMMUS));
		r.accept(narslimmusPotted, potted(LibBlockNames.SUBTILE_NARSLIMMUS));

		r.accept(spectrolus, LibBlockNames.SUBTILE_SPECTROLUS);
		r.accept(spectrolusFloating, floating(LibBlockNames.SUBTILE_SPECTROLUS));
		r.accept(spectrolusPotted, potted(LibBlockNames.SUBTILE_SPECTROLUS));

		r.accept(dandelifeon, LibBlockNames.SUBTILE_DANDELIFEON);
		r.accept(dandelifeonFloating, floating(LibBlockNames.SUBTILE_DANDELIFEON));
		r.accept(dandelifeonPotted, potted(LibBlockNames.SUBTILE_DANDELIFEON));

		r.accept(rafflowsia, LibBlockNames.SUBTILE_RAFFLOWSIA);
		r.accept(rafflowsiaFloating, floating(LibBlockNames.SUBTILE_RAFFLOWSIA));
		r.accept(rafflowsiaPotted, potted(LibBlockNames.SUBTILE_RAFFLOWSIA));

		r.accept(shulkMeNot, LibBlockNames.SUBTILE_SHULK_ME_NOT);
		r.accept(shulkMeNotFloating, floating(LibBlockNames.SUBTILE_SHULK_ME_NOT));
		r.accept(shulkMeNotPotted, potted(LibBlockNames.SUBTILE_SHULK_ME_NOT));

		r.accept(bellethorn, LibBlockNames.SUBTILE_BELLETHORN);
		r.accept(bellethornChibi, chibi(LibBlockNames.SUBTILE_BELLETHORN));
		r.accept(bellethornFloating, floating(LibBlockNames.SUBTILE_BELLETHORN));
		r.accept(bellethornChibiFloating, chibi(floating(LibBlockNames.SUBTILE_BELLETHORN)));
		r.accept(bellethornPotted, potted(LibBlockNames.SUBTILE_BELLETHORN));
		r.accept(bellethornChibiPotted, potted(chibi(LibBlockNames.SUBTILE_BELLETHORN)));

		r.accept(bergamute, LibBlockNames.SUBTILE_BERGAMUTE);
		r.accept(bergamuteFloating, floating(LibBlockNames.SUBTILE_BERGAMUTE));
		r.accept(bergamutePotted, potted(LibBlockNames.SUBTILE_BERGAMUTE));

		r.accept(dreadthorn, LibBlockNames.SUBTILE_DREADTHORN);
		r.accept(dreadthornFloating, floating(LibBlockNames.SUBTILE_DREADTHORN));
		r.accept(dreadthornPotted, potted(LibBlockNames.SUBTILE_DREADTHORN));

		r.accept(heiseiDream, LibBlockNames.SUBTILE_HEISEI_DREAM);
		r.accept(heiseiDreamFloating, floating(LibBlockNames.SUBTILE_HEISEI_DREAM));
		r.accept(heiseiDreamPotted, potted(LibBlockNames.SUBTILE_HEISEI_DREAM));

		r.accept(tigerseye, LibBlockNames.SUBTILE_TIGERSEYE);
		r.accept(tigerseyeFloating, floating(LibBlockNames.SUBTILE_TIGERSEYE));
		r.accept(tigerseyePotted, potted(LibBlockNames.SUBTILE_TIGERSEYE));

		r.accept(jadedAmaranthus, LibBlockNames.SUBTILE_JADED_AMARANTHUS);
		r.accept(jadedAmaranthusFloating, floating(LibBlockNames.SUBTILE_JADED_AMARANTHUS));
		r.accept(jadedAmaranthusPotted, potted(LibBlockNames.SUBTILE_JADED_AMARANTHUS));

		r.accept(orechid, LibBlockNames.SUBTILE_ORECHID);
		r.accept(orechidFloating, floating(LibBlockNames.SUBTILE_ORECHID));
		r.accept(orechidPotted, potted(LibBlockNames.SUBTILE_ORECHID));

		r.accept(fallenKanade, LibBlockNames.SUBTILE_FALLEN_KANADE);
		r.accept(fallenKanadeFloating, floating(LibBlockNames.SUBTILE_FALLEN_KANADE));
		r.accept(fallenKanadePotted, potted(LibBlockNames.SUBTILE_FALLEN_KANADE));

		r.accept(exoflame, LibBlockNames.SUBTILE_EXOFLAME);
		r.accept(exoflameFloating, floating(LibBlockNames.SUBTILE_EXOFLAME));
		r.accept(exoflamePotted, potted(LibBlockNames.SUBTILE_EXOFLAME));

		r.accept(agricarnation, LibBlockNames.SUBTILE_AGRICARNATION);
		r.accept(agricarnationChibi, chibi(LibBlockNames.SUBTILE_AGRICARNATION));
		r.accept(agricarnationFloating, floating(LibBlockNames.SUBTILE_AGRICARNATION));
		r.accept(agricarnationChibiFloating, chibi(floating(LibBlockNames.SUBTILE_AGRICARNATION)));
		r.accept(agricarnationPotted, potted(LibBlockNames.SUBTILE_AGRICARNATION));
		r.accept(agricarnationChibiPotted, potted(chibi(LibBlockNames.SUBTILE_AGRICARNATION)));

		r.accept(hopperhock, LibBlockNames.SUBTILE_HOPPERHOCK);
		r.accept(hopperhockChibi, chibi(LibBlockNames.SUBTILE_HOPPERHOCK));
		r.accept(hopperhockFloating, floating(LibBlockNames.SUBTILE_HOPPERHOCK));
		r.accept(hopperhockChibiFloating, chibi(floating(LibBlockNames.SUBTILE_HOPPERHOCK)));
		r.accept(hopperhockPotted, potted(LibBlockNames.SUBTILE_HOPPERHOCK));
		r.accept(hopperhockChibiPotted, potted(chibi(LibBlockNames.SUBTILE_HOPPERHOCK)));

		r.accept(tangleberrie, LibBlockNames.SUBTILE_TANGLEBERRIE);
		r.accept(tangleberrieChibi, chibi(LibBlockNames.SUBTILE_TANGLEBERRIE));
		r.accept(tangleberrieFloating, floating(LibBlockNames.SUBTILE_TANGLEBERRIE));
		r.accept(tangleberrieChibiFloating, chibi(floating(LibBlockNames.SUBTILE_TANGLEBERRIE)));
		r.accept(tangleberriePotted, potted(LibBlockNames.SUBTILE_TANGLEBERRIE));
		r.accept(tangleberrieChibiPotted, potted(chibi(LibBlockNames.SUBTILE_TANGLEBERRIE)));

		r.accept(jiyuulia, LibBlockNames.SUBTILE_JIYUULIA);
		r.accept(jiyuuliaChibi, chibi(LibBlockNames.SUBTILE_JIYUULIA));
		r.accept(jiyuuliaFloating, floating(LibBlockNames.SUBTILE_JIYUULIA));
		r.accept(jiyuuliaChibiFloating, chibi(floating(LibBlockNames.SUBTILE_JIYUULIA)));
		r.accept(jiyuuliaPotted, potted(LibBlockNames.SUBTILE_JIYUULIA));
		r.accept(jiyuuliaChibiPotted, potted(chibi(LibBlockNames.SUBTILE_JIYUULIA)));

		r.accept(rannuncarpus, LibBlockNames.SUBTILE_RANNUNCARPUS);
		r.accept(rannuncarpusChibi, chibi(LibBlockNames.SUBTILE_RANNUNCARPUS));
		r.accept(rannuncarpusFloating, floating(LibBlockNames.SUBTILE_RANNUNCARPUS));
		r.accept(rannuncarpusChibiFloating, chibi(floating(LibBlockNames.SUBTILE_RANNUNCARPUS)));
		r.accept(rannuncarpusPotted, potted(LibBlockNames.SUBTILE_RANNUNCARPUS));
		r.accept(rannuncarpusChibiPotted, potted(chibi(LibBlockNames.SUBTILE_RANNUNCARPUS)));

		r.accept(hyacidus, LibBlockNames.SUBTILE_HYACIDUS);
		r.accept(hyacidusFloating, floating(LibBlockNames.SUBTILE_HYACIDUS));
		r.accept(hyacidusPotted, potted(LibBlockNames.SUBTILE_HYACIDUS));

		r.accept(pollidisiac, LibBlockNames.SUBTILE_POLLIDISIAC);
		r.accept(pollidisiacFloating, floating(LibBlockNames.SUBTILE_POLLIDISIAC));
		r.accept(pollidisiacPotted, potted(LibBlockNames.SUBTILE_POLLIDISIAC));

		r.accept(clayconia, LibBlockNames.SUBTILE_CLAYCONIA);
		r.accept(clayconiaChibi, chibi(LibBlockNames.SUBTILE_CLAYCONIA));
		r.accept(clayconiaFloating, floating(LibBlockNames.SUBTILE_CLAYCONIA));
		r.accept(clayconiaChibiFloating, chibi(floating(LibBlockNames.SUBTILE_CLAYCONIA)));
		r.accept(clayconiaPotted, potted(LibBlockNames.SUBTILE_CLAYCONIA));
		r.accept(clayconiaChibiPotted, potted(chibi(LibBlockNames.SUBTILE_CLAYCONIA)));

		r.accept(loonium, LibBlockNames.SUBTILE_LOONIUM);
		r.accept(looniumFloating, floating(LibBlockNames.SUBTILE_LOONIUM));
		r.accept(looniumPotted, potted(LibBlockNames.SUBTILE_LOONIUM));

		r.accept(daffomill, LibBlockNames.SUBTILE_DAFFOMILL);
		r.accept(daffomillFloating, floating(LibBlockNames.SUBTILE_DAFFOMILL));
		r.accept(daffomillPotted, potted(LibBlockNames.SUBTILE_DAFFOMILL));

		r.accept(vinculotus, LibBlockNames.SUBTILE_VINCULOTUS);
		r.accept(vinculotusFloating, floating(LibBlockNames.SUBTILE_VINCULOTUS));
		r.accept(vinculotusPotted, potted(LibBlockNames.SUBTILE_VINCULOTUS));

		r.accept(spectranthemum, LibBlockNames.SUBTILE_SPECTRANTHEMUM);
		r.accept(spectranthemumFloating, floating(LibBlockNames.SUBTILE_SPECTRANTHEMUM));
		r.accept(spectranthemumPotted, potted(LibBlockNames.SUBTILE_SPECTRANTHEMUM));

		r.accept(medumone, LibBlockNames.SUBTILE_MEDUMONE);
		r.accept(medumoneFloating, floating(LibBlockNames.SUBTILE_MEDUMONE));
		r.accept(medumonePotted, potted(LibBlockNames.SUBTILE_MEDUMONE));

		r.accept(marimorphosis, LibBlockNames.SUBTILE_MARIMORPHOSIS);
		r.accept(marimorphosisChibi, chibi(LibBlockNames.SUBTILE_MARIMORPHOSIS));
		r.accept(marimorphosisFloating, floating(LibBlockNames.SUBTILE_MARIMORPHOSIS));
		r.accept(marimorphosisChibiFloating, chibi(floating(LibBlockNames.SUBTILE_MARIMORPHOSIS)));
		r.accept(marimorphosisPotted, potted(LibBlockNames.SUBTILE_MARIMORPHOSIS));
		r.accept(marimorphosisChibiPotted, potted(chibi(LibBlockNames.SUBTILE_MARIMORPHOSIS)));

		r.accept(bubbell, LibBlockNames.SUBTILE_BUBBELL);
		r.accept(bubbellChibi, chibi(LibBlockNames.SUBTILE_BUBBELL));
		r.accept(bubbellFloating, floating(LibBlockNames.SUBTILE_BUBBELL));
		r.accept(bubbellChibiFloating, chibi(floating(LibBlockNames.SUBTILE_BUBBELL)));
		r.accept(bubbellPotted, potted(LibBlockNames.SUBTILE_BUBBELL));
		r.accept(bubbellChibiPotted, potted(chibi(LibBlockNames.SUBTILE_BUBBELL)));

		r.accept(solegnolia, LibBlockNames.SUBTILE_SOLEGNOLIA);
		r.accept(solegnoliaChibi, chibi(LibBlockNames.SUBTILE_SOLEGNOLIA));
		r.accept(solegnoliaFloating, floating(LibBlockNames.SUBTILE_SOLEGNOLIA));
		r.accept(solegnoliaChibiFloating, chibi(floating(LibBlockNames.SUBTILE_SOLEGNOLIA)));
		r.accept(solegnoliaPotted, potted(LibBlockNames.SUBTILE_SOLEGNOLIA));
		r.accept(solegnoliaChibiPotted, potted(chibi(LibBlockNames.SUBTILE_SOLEGNOLIA)));

		r.accept(orechidIgnem, LibBlockNames.SUBTILE_ORECHID_IGNEM);
		r.accept(orechidIgnemFloating, floating(LibBlockNames.SUBTILE_ORECHID_IGNEM));
		r.accept(orechidIgnemPotted, potted(LibBlockNames.SUBTILE_ORECHID_IGNEM));

		r.accept(labellia, LibBlockNames.SUBTILE_LABELLIA);
		r.accept(labelliaFloating, floating(LibBlockNames.SUBTILE_LABELLIA));
		r.accept(labelliaPotted, potted(LibBlockNames.SUBTILE_LABELLIA));
	}

	public static void registerItemBlocks(BiConsumer<Item, ResourceLocation> r) {
		Item.Properties props = BotaniaItems.defaultBuilder();

		r.accept(new SpecialFlowerBlockItem(pureDaisy, props), getId(pureDaisy));
		r.accept(new SpecialFlowerBlockItem(pureDaisyFloating, props), getId(pureDaisyFloating));

		r.accept(new SpecialFlowerBlockItem(manastar, props), getId(manastar));
		r.accept(new SpecialFlowerBlockItem(manastarFloating, props), getId(manastarFloating));

		r.accept(new SpecialFlowerBlockItem(hydroangeas, props), getId(hydroangeas));
		r.accept(new SpecialFlowerBlockItem(hydroangeasFloating, props), getId(hydroangeasFloating));

		r.accept(new SpecialFlowerBlockItem(endoflame, props), getId(endoflame));
		r.accept(new SpecialFlowerBlockItem(endoflameFloating, props), getId(endoflameFloating));

		r.accept(new SpecialFlowerBlockItem(thermalily, props), getId(thermalily));
		r.accept(new SpecialFlowerBlockItem(thermalilyFloating, props), getId(thermalilyFloating));

		r.accept(new SpecialFlowerBlockItem(rosaArcana, props), getId(rosaArcana));
		r.accept(new SpecialFlowerBlockItem(rosaArcanaFloating, props), getId(rosaArcanaFloating));

		r.accept(new SpecialFlowerBlockItem(munchdew, props), getId(munchdew));
		r.accept(new SpecialFlowerBlockItem(munchdewFloating, props), getId(munchdewFloating));

		r.accept(new SpecialFlowerBlockItem(entropinnyum, props), getId(entropinnyum));
		r.accept(new SpecialFlowerBlockItem(entropinnyumFloating, props), getId(entropinnyumFloating));

		r.accept(new SpecialFlowerBlockItem(kekimurus, props), getId(kekimurus));
		r.accept(new SpecialFlowerBlockItem(kekimurusFloating, props), getId(kekimurusFloating));

		r.accept(new SpecialFlowerBlockItem(gourmaryllis, props), getId(gourmaryllis));
		r.accept(new SpecialFlowerBlockItem(gourmaryllisFloating, props), getId(gourmaryllisFloating));

		r.accept(new SpecialFlowerBlockItem(narslimmus, props), getId(narslimmus));
		r.accept(new SpecialFlowerBlockItem(narslimmusFloating, props), getId(narslimmusFloating));

		r.accept(new SpecialFlowerBlockItem(spectrolus, props), getId(spectrolus));
		r.accept(new SpecialFlowerBlockItem(spectrolusFloating, props), getId(spectrolusFloating));

		r.accept(new SpecialFlowerBlockItem(dandelifeon, props), getId(dandelifeon));
		r.accept(new SpecialFlowerBlockItem(dandelifeonFloating, props), getId(dandelifeonFloating));

		r.accept(new SpecialFlowerBlockItem(rafflowsia, props), getId(rafflowsia));
		r.accept(new SpecialFlowerBlockItem(rafflowsiaFloating, props), getId(rafflowsiaFloating));

		r.accept(new SpecialFlowerBlockItem(shulkMeNot, props), getId(shulkMeNot));
		r.accept(new SpecialFlowerBlockItem(shulkMeNotFloating, props), getId(shulkMeNotFloating));

		r.accept(new SpecialFlowerBlockItem(bellethorn, props), getId(bellethorn));
		r.accept(new SpecialFlowerBlockItem(bellethornChibi, props), getId(bellethornChibi));
		r.accept(new SpecialFlowerBlockItem(bellethornFloating, props), getId(bellethornFloating));
		r.accept(new SpecialFlowerBlockItem(bellethornChibiFloating, props), getId(bellethornChibiFloating));

		r.accept(new SpecialFlowerBlockItem(bergamute, props), getId(bergamute));
		r.accept(new SpecialFlowerBlockItem(bergamuteFloating, props), getId(bergamuteFloating));

		r.accept(new SpecialFlowerBlockItem(dreadthorn, props), getId(dreadthorn));
		r.accept(new SpecialFlowerBlockItem(dreadthornFloating, props), getId(dreadthornFloating));

		r.accept(new SpecialFlowerBlockItem(heiseiDream, props), getId(heiseiDream));
		r.accept(new SpecialFlowerBlockItem(heiseiDreamFloating, props), getId(heiseiDreamFloating));

		r.accept(new SpecialFlowerBlockItem(tigerseye, props), getId(tigerseye));
		r.accept(new SpecialFlowerBlockItem(tigerseyeFloating, props), getId(tigerseyeFloating));

		r.accept(new SpecialFlowerBlockItem(jadedAmaranthus, props), getId(jadedAmaranthus));
		r.accept(new SpecialFlowerBlockItem(jadedAmaranthusFloating, props), getId(jadedAmaranthusFloating));

		r.accept(new SpecialFlowerBlockItem(orechid, props), getId(orechid));
		r.accept(new SpecialFlowerBlockItem(orechidFloating, props), getId(orechidFloating));

		r.accept(new SpecialFlowerBlockItem(fallenKanade, props), getId(fallenKanade));
		r.accept(new SpecialFlowerBlockItem(fallenKanadeFloating, props), getId(fallenKanadeFloating));

		r.accept(new SpecialFlowerBlockItem(exoflame, props), getId(exoflame));
		r.accept(new SpecialFlowerBlockItem(exoflameFloating, props), getId(exoflameFloating));

		r.accept(new SpecialFlowerBlockItem(agricarnation, props), getId(agricarnation));
		r.accept(new SpecialFlowerBlockItem(agricarnationChibi, props), getId(agricarnationChibi));
		r.accept(new SpecialFlowerBlockItem(agricarnationFloating, props), getId(agricarnationFloating));
		r.accept(new SpecialFlowerBlockItem(agricarnationChibiFloating, props), getId(agricarnationChibiFloating));

		r.accept(new SpecialFlowerBlockItem(hopperhock, props), getId(hopperhock));
		r.accept(new SpecialFlowerBlockItem(hopperhockChibi, props), getId(hopperhockChibi));
		r.accept(new SpecialFlowerBlockItem(hopperhockFloating, props), getId(hopperhockFloating));
		r.accept(new SpecialFlowerBlockItem(hopperhockChibiFloating, props), getId(hopperhockChibiFloating));

		r.accept(new SpecialFlowerBlockItem(tangleberrie, props), getId(tangleberrie));
		r.accept(new SpecialFlowerBlockItem(tangleberrieChibi, props), getId(tangleberrieChibi));
		r.accept(new SpecialFlowerBlockItem(tangleberrieFloating, props), getId(tangleberrieFloating));
		r.accept(new SpecialFlowerBlockItem(tangleberrieChibiFloating, props), getId(tangleberrieChibiFloating));

		r.accept(new SpecialFlowerBlockItem(jiyuulia, props), getId(jiyuulia));
		r.accept(new SpecialFlowerBlockItem(jiyuuliaChibi, props), getId(jiyuuliaChibi));
		r.accept(new SpecialFlowerBlockItem(jiyuuliaFloating, props), getId(jiyuuliaFloating));
		r.accept(new SpecialFlowerBlockItem(jiyuuliaChibiFloating, props), getId(jiyuuliaChibiFloating));

		r.accept(new SpecialFlowerBlockItem(rannuncarpus, props), getId(rannuncarpus));
		r.accept(new SpecialFlowerBlockItem(rannuncarpusChibi, props), getId(rannuncarpusChibi));
		r.accept(new SpecialFlowerBlockItem(rannuncarpusFloating, props), getId(rannuncarpusFloating));
		r.accept(new SpecialFlowerBlockItem(rannuncarpusChibiFloating, props), getId(rannuncarpusChibiFloating));

		r.accept(new SpecialFlowerBlockItem(hyacidus, props), getId(hyacidus));
		r.accept(new SpecialFlowerBlockItem(hyacidusFloating, props), getId(hyacidusFloating));

		r.accept(new SpecialFlowerBlockItem(pollidisiac, props), getId(pollidisiac));
		r.accept(new SpecialFlowerBlockItem(pollidisiacFloating, props), getId(pollidisiacFloating));

		r.accept(new SpecialFlowerBlockItem(clayconia, props), getId(clayconia));
		r.accept(new SpecialFlowerBlockItem(clayconiaChibi, props), getId(clayconiaChibi));
		r.accept(new SpecialFlowerBlockItem(clayconiaFloating, props), getId(clayconiaFloating));
		r.accept(new SpecialFlowerBlockItem(clayconiaChibiFloating, props), getId(clayconiaChibiFloating));

		r.accept(new SpecialFlowerBlockItem(loonium, props), getId(loonium));
		r.accept(new SpecialFlowerBlockItem(looniumFloating, props), getId(looniumFloating));

		r.accept(new SpecialFlowerBlockItem(daffomill, props), getId(daffomill));
		r.accept(new SpecialFlowerBlockItem(daffomillFloating, props), getId(daffomillFloating));

		r.accept(new SpecialFlowerBlockItem(vinculotus, props), getId(vinculotus));
		r.accept(new SpecialFlowerBlockItem(vinculotusFloating, props), getId(vinculotusFloating));

		r.accept(new SpecialFlowerBlockItem(spectranthemum, props), getId(spectranthemum));
		r.accept(new SpecialFlowerBlockItem(spectranthemumFloating, props), getId(spectranthemumFloating));

		r.accept(new SpecialFlowerBlockItem(medumone, props), getId(medumone));
		r.accept(new SpecialFlowerBlockItem(medumoneFloating, props), getId(medumoneFloating));

		r.accept(new SpecialFlowerBlockItem(marimorphosis, props), getId(marimorphosis));
		r.accept(new SpecialFlowerBlockItem(marimorphosisChibi, props), getId(marimorphosisChibi));
		r.accept(new SpecialFlowerBlockItem(marimorphosisFloating, props), getId(marimorphosisFloating));
		r.accept(new SpecialFlowerBlockItem(marimorphosisChibiFloating, props), getId(marimorphosisChibiFloating));

		r.accept(new SpecialFlowerBlockItem(bubbell, props), getId(bubbell));
		r.accept(new SpecialFlowerBlockItem(bubbellChibi, props), getId(bubbellChibi));
		r.accept(new SpecialFlowerBlockItem(bubbellFloating, props), getId(bubbellFloating));
		r.accept(new SpecialFlowerBlockItem(bubbellChibiFloating, props), getId(bubbellChibiFloating));

		r.accept(new SpecialFlowerBlockItem(solegnolia, props), getId(solegnolia));
		r.accept(new SpecialFlowerBlockItem(solegnoliaChibi, props), getId(solegnoliaChibi));
		r.accept(new SpecialFlowerBlockItem(solegnoliaFloating, props), getId(solegnoliaFloating));
		r.accept(new SpecialFlowerBlockItem(solegnoliaChibiFloating, props), getId(solegnoliaChibiFloating));

		r.accept(new SpecialFlowerBlockItem(orechidIgnem, props), getId(orechidIgnem));
		r.accept(new SpecialFlowerBlockItem(orechidIgnemFloating, props), getId(orechidIgnemFloating));

		r.accept(new SpecialFlowerBlockItem(labellia, props), getId(labellia));
		r.accept(new SpecialFlowerBlockItem(labelliaFloating, props), getId(labelliaFloating));
	}

	public static void registerTEs(BiConsumer<BlockEntityType<?>, ResourceLocation> r) {
		r.accept(PURE_DAISY, getId(pureDaisy));
		r.accept(MANASTAR, getId(manastar));
		r.accept(HYDROANGEAS, getId(hydroangeas));
		r.accept(ENDOFLAME, getId(endoflame));
		r.accept(THERMALILY, getId(thermalily));
		r.accept(ROSA_ARCANA, getId(rosaArcana));
		r.accept(MUNCHDEW, getId(munchdew));
		r.accept(ENTROPINNYUM, getId(entropinnyum));
		r.accept(KEKIMURUS, getId(kekimurus));
		r.accept(GOURMARYLLIS, getId(gourmaryllis));
		r.accept(NARSLIMMUS, getId(narslimmus));
		r.accept(SPECTROLUS, getId(spectrolus));
		r.accept(DANDELIFEON, getId(dandelifeon));
		r.accept(RAFFLOWSIA, getId(rafflowsia));
		r.accept(SHULK_ME_NOT, getId(shulkMeNot));
		r.accept(BELLETHORNE, getId(bellethorn));
		r.accept(BELLETHORNE_CHIBI, getId(bellethornChibi));
		r.accept(BERGAMUTE, getId(bergamute));
		r.accept(DREADTHORN, getId(dreadthorn));
		r.accept(HEISEI_DREAM, getId(heiseiDream));
		r.accept(TIGERSEYE, getId(tigerseye));
		r.accept(JADED_AMARANTHUS, getId(jadedAmaranthus));
		r.accept(ORECHID, getId(orechid));
		r.accept(FALLEN_KANADE, getId(fallenKanade));
		r.accept(EXOFLAME, getId(exoflame));
		r.accept(AGRICARNATION, getId(agricarnation));
		r.accept(AGRICARNATION_CHIBI, getId(agricarnationChibi));
		r.accept(HOPPERHOCK, getId(hopperhock));
		r.accept(HOPPERHOCK_CHIBI, getId(hopperhockChibi));
		r.accept(TANGLEBERRIE, getId(tangleberrie));
		r.accept(TANGLEBERRIE_CHIBI, getId(tangleberrieChibi));
		r.accept(JIYUULIA, getId(jiyuulia));
		r.accept(JIYUULIA_CHIBI, getId(jiyuuliaChibi));
		r.accept(RANNUNCARPUS, getId(rannuncarpus));
		r.accept(RANNUNCARPUS_CHIBI, getId(rannuncarpusChibi));
		r.accept(HYACIDUS, getId(hyacidus));
		r.accept(POLLIDISIAC, getId(pollidisiac));
		r.accept(CLAYCONIA, getId(clayconia));
		r.accept(CLAYCONIA_CHIBI, getId(clayconiaChibi));
		r.accept(LOONIUM, getId(loonium));
		r.accept(DAFFOMILL, getId(daffomill));
		r.accept(VINCULOTUS, getId(vinculotus));
		r.accept(SPECTRANTHEMUM, getId(spectranthemum));
		r.accept(MEDUMONE, getId(medumone));
		r.accept(MARIMORPHOSIS, getId(marimorphosis));
		r.accept(MARIMORPHOSIS_CHIBI, getId(marimorphosisChibi));
		r.accept(BUBBELL, getId(bubbell));
		r.accept(BUBBELL_CHIBI, getId(bubbellChibi));
		r.accept(SOLEGNOLIA, getId(solegnolia));
		r.accept(SOLEGNOLIA_CHIBI, getId(solegnoliaChibi));
		r.accept(ORECHID_IGNEM, getId(orechidIgnem));
		r.accept(LABELLIA, getId(labellia));
	}

	public static void registerWandHudCaps(BotaniaBlockEntities.BECapConsumer<WandHUD> consumer) {
		consumer.accept(be -> new SpectrolusBlockEntity.WandHud((SpectrolusBlockEntity) be), SPECTROLUS);
		consumer.accept(be -> new BindableSpecialFlowerBlockEntity.BindableFlowerWandHud<>((GeneratingFlowerBlockEntity) be),
				HYDROANGEAS, ENDOFLAME, THERMALILY, ROSA_ARCANA, MUNCHDEW, ENTROPINNYUM, KEKIMURUS, GOURMARYLLIS, NARSLIMMUS,
				DANDELIFEON, RAFFLOWSIA, SHULK_ME_NOT);

		consumer.accept(be -> new HopperhockBlockEntity.WandHud((HopperhockBlockEntity) be), HOPPERHOCK, HOPPERHOCK_CHIBI);
		consumer.accept(be -> new PollidisiacBlockEntity.WandHud((PollidisiacBlockEntity) be), POLLIDISIAC);
		consumer.accept(be -> new RannuncarpusBlockEntity.WandHud((RannuncarpusBlockEntity) be), RANNUNCARPUS, RANNUNCARPUS_CHIBI);
		consumer.accept(be -> new LooniumBlockEntity.WandHud((LooniumBlockEntity) be), LOONIUM);
		consumer.accept(be -> new BindableSpecialFlowerBlockEntity.BindableFlowerWandHud<>((FunctionalFlowerBlockEntity) be),
				BELLETHORNE, BELLETHORNE_CHIBI, DREADTHORN, HEISEI_DREAM, TIGERSEYE,
				JADED_AMARANTHUS, ORECHID, FALLEN_KANADE, EXOFLAME, AGRICARNATION, AGRICARNATION_CHIBI,
				TANGLEBERRIE, TANGLEBERRIE_CHIBI, JIYUULIA, JIYUULIA_CHIBI, HYACIDUS,
				CLAYCONIA, CLAYCONIA_CHIBI, DAFFOMILL, VINCULOTUS, SPECTRANTHEMUM, MEDUMONE,
				MARIMORPHOSIS, MARIMORPHOSIS_CHIBI, BUBBELL, BUBBELL_CHIBI, SOLEGNOLIA, SOLEGNOLIA_CHIBI,
				ORECHID_IGNEM, LABELLIA);
	}

	public static void registerFlowerPotPlants(BiConsumer<ResourceLocation, Supplier<? extends Block>> consumer) {
		registerBlocks((block, resourceLocation) -> {
			if (block instanceof FlowerPotBlock) {
				var id = getId(block);
				consumer.accept(new ResourceLocation(id.getNamespace(), id.getPath().substring(LibBlockNames.POTTED_PREFIX.length())), () -> block);
			}
		});
	}
}
