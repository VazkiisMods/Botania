package vazkii.botania.forge;

import com.google.common.base.Suppliers;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.registries.RegisterEvent;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.BotaniaRegistries;
import vazkii.botania.api.block.HornHarvestable;
import vazkii.botania.api.block.Wandable;
import vazkii.botania.api.item.AvatarWieldable;
import vazkii.botania.api.item.BlockProvider;
import vazkii.botania.api.item.CoordBoundItem;
import vazkii.botania.api.item.Relic;
import vazkii.botania.api.mana.*;
import vazkii.botania.api.mana.spark.SparkAttachable;
import vazkii.botania.client.fx.BotaniaParticles;
import vazkii.botania.common.BotaniaStats;
import vazkii.botania.common.PlayerAccess;
import vazkii.botania.common.advancements.BotaniaCriteriaTriggers;
import vazkii.botania.common.block.*;
import vazkii.botania.common.block.block_entity.*;
import vazkii.botania.common.block.block_entity.BlockEntityConstants;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.corporea.CorporeaIndexBlockEntity;
import vazkii.botania.common.block.block_entity.mana.PowerGeneratorBlockEntity;
import vazkii.botania.common.block.block_entity.red_string.RedStringContainerBlockEntity;
import vazkii.botania.common.block.flower.functional.DaffomillBlockEntity;
import vazkii.botania.common.block.flower.functional.LooniumBlockEntity;
import vazkii.botania.common.block.flower.functional.TigerseyeBlockEntity;
import vazkii.botania.common.block.flower.functional.VinculotusBlockEntity;
import vazkii.botania.common.block.mana.DrumBlock;
import vazkii.botania.common.block.mana.ManaDetectorBlock;
import vazkii.botania.common.block.mana.ManaVoidBlock;
import vazkii.botania.common.block.red_string.RedStringInterceptorBlock;
import vazkii.botania.common.brew.BotaniaBrews;
import vazkii.botania.common.brew.BotaniaMobEffects;
import vazkii.botania.common.brew.effect.SoulCrossMobEffect;
import vazkii.botania.common.command.SkyblockCommand;
import vazkii.botania.common.config.ConfigDataManagerImpl;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;
import vazkii.botania.common.entity.BotaniaEntities;
import vazkii.botania.common.entity.GaiaGuardianEntity;
import vazkii.botania.common.handler.*;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.impl.BotaniaAPIImpl;
import vazkii.botania.common.impl.DefaultHornHarvestable;
import vazkii.botania.common.impl.corporea.DefaultCorporeaMatchers;
import vazkii.botania.common.integration.corporea.CorporeaNodeDetectors;
import vazkii.botania.common.item.*;
import vazkii.botania.common.item.equipment.armor.terrasteel.TerrasteelHelmItem;
import vazkii.botania.common.item.equipment.bauble.*;
import vazkii.botania.common.item.equipment.tool.elementium.ElementiumAxeItem;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraBladeItem;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraTruncatorItem;
import vazkii.botania.common.item.material.EnderAirItem;
import vazkii.botania.common.item.relic.*;
import vazkii.botania.common.item.rod.*;
import vazkii.botania.common.lib.LibMisc;
import vazkii.botania.common.loot.BotaniaLootModifiers;
import vazkii.botania.common.loot.LootHandler;
import vazkii.botania.common.world.BotaniaFeatures;
import vazkii.botania.common.world.SkyblockChunkGenerator;
import vazkii.botania.common.world.SkyblockWorldEvents;
import vazkii.botania.forge.integration.InventorySorterIntegration;
import vazkii.botania.forge.integration.corporea.ForgeCapCorporeaNodeDetector;
import vazkii.botania.forge.integration.curios.CurioIntegration;
import vazkii.botania.forge.internal_caps.RedStringContainerCapProvider;
import vazkii.botania.forge.network.ForgePacketHandler;
import vazkii.botania.forge.xplat.ForgeXplatImpl;
import vazkii.botania.xplat.XplatAbstractions;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

@Mod(LibMisc.MOD_ID)
public class ForgeCommonInitializer {
	public ForgeCommonInitializer() {
		coreInit();
		registryInit();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
	}

	public void commonSetup(FMLCommonSetupEvent evt) {
		ForgePacketHandler.init();
		registerEvents();

		evt.enqueueWork(BotaniaBlocks::addDispenserBehaviours);
		evt.enqueueWork(() -> {
			BiConsumer<ResourceLocation, Supplier<? extends Block>> consumer = (resourceLocation, blockSupplier) -> ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(resourceLocation, blockSupplier);
			BotaniaBlocks.registerFlowerPotPlants(consumer);
			BotaniaFlowerBlocks.registerFlowerPotPlants(consumer);
		});
		BotaniaBlocks.addAxeStripping();
		PaintableData.init();
		CompostingData.init((itemLike, chance) -> ComposterBlock.COMPOSTABLES.putIfAbsent(itemLike.asItem(), (float) chance));
		DefaultCorporeaMatchers.init();
		PlayerHelper.setFakePlayerClass(FakePlayer.class);

		PatchouliAPI.get().registerMultiblock(BuiltInRegistries.BLOCK.getKey(BotaniaBlocks.alfPortal), AlfheimPortalBlockEntity.MULTIBLOCK.get());
		PatchouliAPI.get().registerMultiblock(BuiltInRegistries.BLOCK.getKey(BotaniaBlocks.terraPlate), TerrestrialAgglomerationPlateBlockEntity.MULTIBLOCK.get());
		PatchouliAPI.get().registerMultiblock(BuiltInRegistries.BLOCK.getKey(BotaniaBlocks.enchanter), ManaEnchanterBlockEntity.MULTIBLOCK.get());
		PatchouliAPI.get().registerMultiblock(prefix("gaia_ritual"), GaiaGuardianEntity.ARENA_MULTIBLOCK.get());

		OrechidManager.registerListener();
		ConfigDataManagerImpl.registerListener();
		CraftyCrateBlockEntity.registerListener();
		CorporeaNodeDetectors.register(new ForgeCapCorporeaNodeDetector());
		if (ModList.get().isLoaded("inventorysorter")) {
			InventorySorterIntegration.init();
		}
	}

	private void coreInit() {
		ForgeBotaniaConfig.setup();
		EquipmentHandler.init();
	}

	private void registryInit() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		// Core item/block/BE
		bind(Registries.SOUND_EVENT, BotaniaSounds::init);
		bind(Registries.BLOCK, consumer -> {
			BotaniaBlocks.registerBlocks(consumer);
			BotaniaBlockFlammability.register();
		});
		bindForItems(BotaniaBlocks::registerItemBlocks);
		bind(Registries.BLOCK_ENTITY_TYPE, BotaniaBlockEntities::registerTiles);
		bindForItems(BotaniaItems::registerItems);
		bind(Registries.BLOCK, BotaniaFlowerBlocks::registerBlocks);
		bindForItems(BotaniaFlowerBlocks::registerItemBlocks);
		bind(Registries.BLOCK_ENTITY_TYPE, BotaniaFlowerBlocks::registerTEs);

		// GUI and Recipe
		bind(Registries.MENU, BotaniaItems::registerMenuTypes);
		bind(Registries.RECIPE_SERIALIZER, BotaniaItems::registerRecipeSerializers);
		bind(Registries.BANNER_PATTERN, BotaniaBannerPatterns::submitRegistrations);
		bind(Registries.RECIPE_TYPE, BotaniaRecipeTypes::submitRecipeTypes);
		bind(Registries.RECIPE_SERIALIZER, BotaniaRecipeTypes::submitRecipeSerializers);

		// Entities
		bind(Registries.ENTITY_TYPE, BotaniaEntities::registerEntities);
		modBus.addListener((EntityAttributeCreationEvent e) -> BotaniaEntities.registerAttributes((type, builder) -> e.put(type, builder.build())));
		modBus.addListener((EntityAttributeModificationEvent e) -> {
			e.add(EntityType.PLAYER, PixieHandler.PIXIE_SPAWN_CHANCE);
		});
		bind(Registries.ATTRIBUTE, PixieHandler::registerAttribute);

		// Potions
		bind(Registries.MOB_EFFECT, BotaniaMobEffects::registerPotions);
		bind(BotaniaRegistries.BREWS, BotaniaBrews::submitRegistrations);

		// Worldgen
		bind(Registries.FEATURE, BotaniaFeatures::registerFeatures);
		bind(Registries.CHUNK_GENERATOR, SkyblockChunkGenerator::submitRegistration);

		// Rest
		BotaniaCriteriaTriggers.init();
		bind(Registries.PARTICLE_TYPE, BotaniaParticles::registerParticles);

		bind(Registries.LOOT_CONDITION_TYPE, BotaniaLootModifiers::submitLootConditions);
		bind(Registries.LOOT_FUNCTION_TYPE, BotaniaLootModifiers::submitLootFunctions);
		// Vanilla's stat constructor does the registration too, so we use this
		// event only for timing, not for registering
		modBus.addListener((RegisterEvent evt) -> {
			if (evt.getRegistryKey().equals(Registries.CUSTOM_STAT)) {
				BotaniaStats.init();
			}
		});
		bind(Registries.CREATIVE_MODE_TAB, consumer -> {
			consumer.accept(CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.botania").withStyle(style -> style.withColor(ChatFormatting.WHITE)))
					.icon(() -> new ItemStack(BotaniaItems.lexicon))
					.withTabsBefore(CreativeModeTabs.NATURAL_BLOCKS)
					.backgroundSuffix("botania.png")
					.withSearchBar()
					.build(),
					BotaniaRegistries.BOTANIA_TAB_KEY.location());
		});
		modBus.addListener((BuildCreativeModeTabContentsEvent e) -> {
			if (e.getTabKey() == BotaniaRegistries.BOTANIA_TAB_KEY) {
				for (Item item : this.itemsToAddToCreativeTab) {
					if (item instanceof CustomCreativeTabContents cc) {
						cc.addToCreativeTab(item, e);
					} else if (item instanceof BlockItem bi && bi.getBlock() instanceof CustomCreativeTabContents cc) {
						cc.addToCreativeTab(item, e);
					} else {
						e.accept(item);
					}
				}
			}
		});
	}

	private static <T> void bind(ResourceKey<Registry<T>> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterEvent event) -> {
			if (registry.equals(event.getRegistryKey())) {
				source.accept((t, rl) -> event.register(registry, rl, () -> t));
			}
		});
	}

	private final Set<Item> itemsToAddToCreativeTab = new LinkedHashSet<>();

	private void bindForItems(Consumer<BiConsumer<Item, ResourceLocation>> source) {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterEvent event) -> {
			if (event.getRegistryKey().equals(Registries.ITEM)) {
				source.accept((t, rl) -> {
					itemsToAddToCreativeTab.add(t);
					event.register(Registries.ITEM, rl, () -> t);
				});
			}
		});
	}

	private void registerEvents() {
		IEventBus bus = MinecraftForge.EVENT_BUS;
		registerBlockLookasides();
		bus.addGenericListener(ItemStack.class, this::attachItemCaps);
		bus.addGenericListener(BlockEntity.class, this::attachBeCaps);

		int blazeTime = 2400 * (XplatAbstractions.INSTANCE.gogLoaded() ? 5 : 10);
		bus.addListener((FurnaceFuelBurnTimeEvent e) -> {
			if (e.getItemStack().is(BotaniaBlocks.blazeBlock.asItem())) {
				e.setBurnTime(blazeTime);
			}
		});

		if (XplatAbstractions.INSTANCE.gogLoaded()) {
			bus.addListener((PlayerInteractEvent.RightClickBlock e) -> {
				InteractionResult result = SkyblockWorldEvents.onPlayerInteract(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());
				if (result == InteractionResult.SUCCESS) {
					e.setCanceled(true);
					e.setCancellationResult(InteractionResult.SUCCESS);
				}
			});
		}
		bus.addListener((PlayerInteractEvent.LeftClickBlock e) -> ((ShiftingCrustRodItem) BotaniaItems.exchangeRod).onLeftClick(
				e.getEntity(), e.getLevel(), e.getHand(), e.getPos(), e.getFace()));
		bus.addListener((PlayerInteractEvent.LeftClickEmpty e) -> TerraBladeItem.leftClick(e.getItemStack()));
		bus.addListener((AttackEntityEvent e) -> TerraBladeItem.attackEntity(
				e.getEntity(), e.getEntity().level(), InteractionHand.MAIN_HAND, e.getTarget(), null));
		bus.addListener((RegisterCommandsEvent e) -> this.registerCommands(
				e.getDispatcher(), e.getCommandSelection() == Commands.CommandSelection.DEDICATED));
		bus.addListener((PlayerSleepInBedEvent e) -> {
			Player.BedSleepingProblem problem = SleepingHandler.trySleep(e.getEntity(), e.getPos());
			if (problem != null) {
				e.setResult(problem);
			}
		});
		bus.addListener((PlayerEvent.StartTracking e) -> DaffomillBlockEntity.onItemTrack(e.getEntity(), (ServerPlayer) e.getEntity()));
		bus.addListener((LootTableLoadEvent e) -> LootHandler.lootLoad(e.getName(), b -> e.getTable().addPool(b.build())));
		bus.addListener((ManaNetworkEvent e) -> ManaNetworkHandler.instance.onNetworkEvent(e.getReceiver(), e.getType(), e.getAction()));
		bus.addListener((EntityJoinLevelEvent e) -> {
			if (!e.getLevel().isClientSide) {
				TigerseyeBlockEntity.pacifyAfterLoad(e.getEntity(), (ServerLevel) e.getLevel());
			}
		});

		bus.addListener((ServerAboutToStartEvent e) -> this.serverAboutToStart(e.getServer()));
		bus.addListener((ServerStoppingEvent e) -> this.serverStopping(e.getServer()));
		bus.addListener((PlayerEvent.PlayerLoggedOutEvent e) -> FlugelTiaraItem.playerLoggedOut((ServerPlayer) e.getEntity()));
		bus.addListener((PlayerEvent.Clone e) -> ResoluteIvyItem.onPlayerRespawn(e.getOriginal(), e.getEntity(), !e.isWasDeath()));
		bus.addListener((TickEvent.LevelTickEvent e) -> {
			if (e.phase == TickEvent.Phase.END && e.level instanceof ServerLevel level) {
				CommonTickHandler.onTick(level);
				GrassSeedsItem.onTickEnd(level);
				TerraTruncatorItem.onTickEnd(level);
			}
		});
		bus.addListener((PlayerInteractEvent.RightClickBlock e) -> {
			RedStringInterceptorBlock.onInteract(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());
			RingOfLokiItem.onPlayerInteract(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());
		});
		bus.addListener((PlayerInteractEvent.RightClickItem e) -> {
			InteractionResultHolder<ItemStack> result = EnderAirItem.onPlayerInteract(e.getEntity(), e.getLevel(), e.getHand());
			if (result.getResult().consumesAction()) {
				e.setCanceled(true);
				e.setCancellationResult(result.getResult());
			}
		});

		// Below here are events implemented via Mixins on the Fabric side, ordered by Mixin name
		// FabricMixinAnvilMenu
		bus.addListener((AnvilUpdateEvent e) -> {
			if (SpellbindingClothItem.shouldDenyAnvil(e.getLeft(), e.getRight())) {
				e.setCanceled(true);
			}
		});
		// FabricMixinAxeItem
		bus.addListener((BlockEvent.BlockToolModificationEvent e) -> {
			if (e.getToolAction() == ToolActions.AXE_STRIP) {
				BlockState input = e.getState();
				Block output = ForgeXplatImpl.CUSTOM_STRIPPABLES.get(input.getBlock());
				if (output != null) {
					e.setFinalState(output.withPropertiesOf(input));
				}
			}
		});
		// FabricMixinEnderMan
		bus.addListener((EntityTeleportEvent.EnderEntity e) -> {
			if (e.getEntityLiving() instanceof EnderMan em) {
				var newPos = VinculotusBlockEntity.onEndermanTeleport(em, e.getTargetX(), e.getTargetY(), e.getTargetZ());
				if (newPos != null) {
					e.setTargetX(newPos.x());
					e.setTargetY(newPos.y());
					e.setTargetZ(newPos.z());
				}
			}
		});
		// FabricMixinExplosion
		bus.addListener((ExplosionEvent.Detonate e) -> {
			if (BenevolentGoddessCharmItem.shouldProtectExplosion(e.getLevel(), e.getExplosion().getPosition())) {
				e.getExplosion().clearToBlow();
			}
		});
		// FabricMixinItemEntity
		bus.addListener((EntityItemPickupEvent e) -> {
			if (FlowerPouchItem.onPickupItem(e.getItem(), e.getEntity())) {
				e.setCanceled(true);
			}
		});
		// FabricMixinLivingEntity
		{
			bus.addListener((LivingDropsEvent e) -> {
				var living = e.getEntity();
				ElementiumAxeItem.onEntityDrops(e.isRecentlyHit(), e.getSource(), living, stack -> {
					var ent = new ItemEntity(living.level(), living.getX(), living.getY(), living.getZ(), stack);
					ent.setDefaultPickUpDelay();
					e.getDrops().add(ent);
				});
				LooniumBlockEntity.dropLooniumItems(living, stack -> {
					e.getDrops().clear();
					if (!stack.isEmpty()) {
						var ent = new ItemEntity(living.level(), living.getX(), living.getY(), living.getZ(), stack);
						ent.setDefaultPickUpDelay();
						e.getDrops().add(ent);
					}
				});
			});
			bus.addListener((LivingDeathEvent e) -> {
				if (e.getSource().getEntity() instanceof LivingEntity killer) {
					SoulCrossMobEffect.onEntityKill(e.getEntity(), killer);
				}
			});
			bus.addListener((LivingEvent.LivingJumpEvent e) -> SojournersSashItem.onPlayerJump(e.getEntity()));
		}
		// FabricMixinPlayer
		{
			bus.addListener((LivingAttackEvent e) -> {
				if (e.getEntity() instanceof Player player
						&& RingOfOdinItem.onPlayerAttacked(player, e.getSource())) {
					e.setCanceled(true);
				}
			});
			bus.addListener((ItemTossEvent e) -> RingOfMagnetizationItem.onTossItem(e.getPlayer()));
			bus.addListener((LivingHurtEvent e) -> {
				if (e.getEntity() instanceof Player player) {
					Container worn = EquipmentHandler.getAllWorn(player);
					for (int i = 0; i < worn.getContainerSize(); i++) {
						ItemStack stack = worn.getItem(i);
						if (stack.getItem() instanceof CloakOfVirtueItem cloak) {
							e.setAmount(cloak.onPlayerDamage(player, e.getSource(), e.getAmount()));
						}
					}

					PixieHandler.onDamageTaken(player, e.getSource());
				}
				if (e.getSource().getDirectEntity() instanceof Player player) {
					CharmOfTheDivaItem.onEntityDamaged(player, e.getEntity());
				}
			});
			bus.addListener((LivingEvent.LivingTickEvent e) -> {
				if (e.getEntity() instanceof Player player) {
					FlugelTiaraItem.updatePlayerFlyStatus(player);
					SojournersSashItem.tickBelt(player);
				}
			});
			bus.addListener((LivingFallEvent e) -> {
				if (e.getEntity() instanceof Player player) {
					e.setDistance(SojournersSashItem.onPlayerFall(player, e.getDistance()));
				}
			});
			bus.addListener(EventPriority.LOW, (CriticalHitEvent e) -> {
				Event.Result result = e.getResult();
				if (e.getEntity().level().isClientSide
						|| result == Event.Result.DENY
						|| result == Event.Result.DEFAULT && !e.isVanillaCritical()
						|| !TerrasteelHelmItem.hasTerraArmorSet(e.getEntity())
						|| !(e.getTarget() instanceof LivingEntity target)) {
					return;
				}
				e.setDamageModifier(e.getDamageModifier() * TerrasteelHelmItem.getCritDamageMult(e.getEntity()));
				((PlayerAccess) e.getEntity()).botania$setCritTarget(target);
			});

		}
		// FabricMixinResultSlot
		bus.addListener((PlayerEvent.ItemCraftedEvent e) -> AssemblyHaloItem.onItemCrafted(e.getEntity(), e.getInventory()));
	}

	// Attaching caps requires dispatching off the item, which is a huge pain because it generates long if-else
	// chains on items, and also doesn't match how Fabric is set up.
	// Instead, let's declare ahead of time what items get which caps, similar to how we do it for Fabric.
	// Needs to be lazy since items aren't initialized yet
	private static final Supplier<Map<Item, Function<ItemStack, AvatarWieldable>>> AVATAR_WIELDABLES = Suppliers.memoize(() -> Map.of(
			BotaniaItems.dirtRod, s -> new LandsRodItem.AvatarBehavior(),
			BotaniaItems.diviningRod, s -> new PlentifulMantleRodItem.AvatarBehavior(),
			BotaniaItems.fireRod, s -> new HellsRodItem.AvatarBehavior(),
			BotaniaItems.missileRod, s -> new UnstableReservoirRodItem.AvatarBehavior(),
			BotaniaItems.rainbowRod, s -> new BifrostRodItem.AvatarBehavior(),
			BotaniaItems.tornadoRod, s -> new SkiesRodItem.AvatarBehavior()
	));

	private static final Supplier<Map<Item, Function<ItemStack, BlockProvider>>> BLOCK_PROVIDER = Suppliers.memoize(() -> Map.of(
			BotaniaItems.dirtRod, LandsRodItem.BlockProviderImpl::new,
			BotaniaItems.skyDirtRod, LandsRodItem.BlockProviderImpl::new,
			BotaniaItems.blackHoleTalisman, BlackHoleTalismanItem.BlockProviderImpl::new,
			BotaniaItems.cobbleRod, s -> new DepthsRodItem.BlockProviderImpl(),
			BotaniaItems.enderHand, EnderHandItem.BlockProviderImpl::new,
			BotaniaItems.terraformRod, s -> new TerraFirmaRodItem.BlockProviderImpl()
	));

	private static final Supplier<Map<Item, Function<ItemStack, CoordBoundItem>>> COORD_BOUND_ITEM = Suppliers.memoize(() -> Map.of(
			BotaniaItems.flugelEye, EyeOfTheFlugelItem.CoordBoundItemImpl::new,
			BotaniaItems.manaMirror, ManaMirrorItem.CoordBoundItemImpl::new,
			BotaniaItems.twigWand, WandOfTheForestItem.CoordBoundItemImpl::new,
			BotaniaItems.dreamwoodWand, WandOfTheForestItem.CoordBoundItemImpl::new
	));

	private static final Supplier<Map<Item, Function<ItemStack, ManaItem>>> MANA_ITEM = Suppliers.memoize(() -> Map.of(
			BotaniaItems.manaMirror, ManaMirrorItem.ManaItemImpl::new,
			BotaniaItems.manaRing, BandOfManaItem.ManaItemImpl::new,
			BotaniaItems.manaRingGreater, GreaterBandOfManaItem.GreaterManaItemImpl::new,
			BotaniaItems.manaTablet, ManaTabletItem.ManaItemImpl::new,
			BotaniaItems.terraPick, TerraShattererItem.ManaItemImpl::new
	));

	private static final Supplier<Map<Item, Function<ItemStack, Relic>>> RELIC = Suppliers.memoize(() -> Map.of(
			BotaniaItems.dice, DiceOfFateItem::makeRelic,
			BotaniaItems.flugelEye, EyeOfTheFlugelItem::makeRelic,
			BotaniaItems.infiniteFruit, FruitOfGrisaiaItem::makeRelic,
			BotaniaItems.kingKey, KeyOfTheKingsLawItem::makeRelic,
			BotaniaItems.lokiRing, RingOfLokiItem::makeRelic,
			BotaniaItems.odinRing, RingOfOdinItem::makeRelic,
			BotaniaItems.thorRing, RingOfThorItem::makeRelic
	));

	private void attachItemCaps(AttachCapabilitiesEvent<ItemStack> e) {
		var stack = e.getObject();

		if (stack.getItem() instanceof BaubleItem
				&& EquipmentHandler.instance instanceof CurioIntegration ci) {
			e.addCapability(prefix("curio"), ci.initCapability(stack));
		}

		if (stack.is(BotaniaItems.waterBowl)) {
			e.addCapability(prefix("water_bowl"), new CapabilityUtil.WaterBowlFluidHandler(stack));
		}

		var makeAvatarWieldable = AVATAR_WIELDABLES.get().get(stack.getItem());
		if (makeAvatarWieldable != null) {
			e.addCapability(prefix("avatar_wieldable"),
					CapabilityUtil.makeProvider(BotaniaForgeCapabilities.AVATAR_WIELDABLE, makeAvatarWieldable.apply(stack)));
		}

		var makeBlockProvider = BLOCK_PROVIDER.get().get(stack.getItem());
		if (makeBlockProvider != null) {
			e.addCapability(prefix("block_provider"),
					CapabilityUtil.makeProvider(BotaniaForgeCapabilities.BLOCK_PROVIDER, makeBlockProvider.apply(stack)));
		}

		var makeCoordBoundItem = COORD_BOUND_ITEM.get().get(stack.getItem());
		if (makeCoordBoundItem != null) {
			e.addCapability(prefix("coord_bound_item"),
					CapabilityUtil.makeProvider(BotaniaForgeCapabilities.COORD_BOUND_ITEM, makeCoordBoundItem.apply(stack)));
		}

		var makeManaItem = MANA_ITEM.get().get(stack.getItem());
		if (makeManaItem != null) {
			e.addCapability(prefix("mana_item"),
					CapabilityUtil.makeProvider(BotaniaForgeCapabilities.MANA_ITEM, makeManaItem.apply(stack)));
		}

		var makeRelic = RELIC.get().get(stack.getItem());
		if (makeRelic != null) {
			e.addCapability(prefix("relic"),
					CapabilityUtil.makeProvider(BotaniaForgeCapabilities.RELIC, makeRelic.apply(stack)));
		}
	}

	private void registerBlockLookasides() {
		CapabilityUtil.registerBlockLookaside(BotaniaForgeCapabilities.HORN_HARVEST, (w, p, s) -> (world, pos, stack, hornType, living) -> hornType == HornHarvestable.EnumHornType.CANOPY,
				Blocks.VINE, Blocks.CAVE_VINES, Blocks.CAVE_VINES_PLANT, Blocks.TWISTING_VINES,
				Blocks.TWISTING_VINES_PLANT, Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT);
		CapabilityUtil.registerBlockLookaside(BotaniaForgeCapabilities.HORN_HARVEST, (w, p, s) -> DefaultHornHarvestable.INSTANCE,
				ColorHelper.supportedColors().map(BotaniaBlocks::getMushroom).toArray(Block[]::new));
		CapabilityUtil.registerBlockLookaside(BotaniaForgeCapabilities.HORN_HARVEST, (w, p, s) -> DefaultHornHarvestable.INSTANCE,
				ColorHelper.supportedColors().map(BotaniaBlocks::getShinyFlower).toArray(Block[]::new));
		CapabilityUtil.registerBlockLookaside(BotaniaForgeCapabilities.MANA_GHOST, (w, p, s) -> ((ManaCollisionGhost) s.getBlock()),
				BotaniaBlocks.manaDetector,
				BotaniaBlocks.abstrusePlatform, BotaniaBlocks.infrangiblePlatform, BotaniaBlocks.spectralPlatform,
				BotaniaBlocks.prism, BotaniaBlocks.tinyPlanet);
		CapabilityUtil.registerBlockLookaside(BotaniaForgeCapabilities.MANA_RECEIVER, ManaVoidBlock.ManaReceiverImpl::new, BotaniaBlocks.manaVoid);
		CapabilityUtil.registerBlockLookaside(BotaniaForgeCapabilities.MANA_TRIGGER, DrumBlock.ManaTriggerImpl::new,
				BotaniaBlocks.canopyDrum, BotaniaBlocks.wildDrum, BotaniaBlocks.gatheringDrum);
		CapabilityUtil.registerBlockLookaside(BotaniaForgeCapabilities.MANA_TRIGGER, ManastormChargeBlock.ManaTriggerImpl::new, BotaniaBlocks.manaBomb);
		CapabilityUtil.registerBlockLookaside(BotaniaForgeCapabilities.MANA_TRIGGER, ManaDetectorBlock.ManaTriggerImpl::new, BotaniaBlocks.manaDetector);
		CapabilityUtil.registerBlockLookaside(BotaniaForgeCapabilities.WANDABLE,
				(world, pos, state) -> (player, stack, side) -> ((ForceRelayBlock) state.getBlock()).onUsedByWand(player, stack, world, pos),
				BotaniaBlocks.pistonRelay);
	}

	private void attachBeCaps(AttachCapabilitiesEvent<BlockEntity> e) {
		var be = e.getObject();
		if (be instanceof AbstractFurnaceBlockEntity furnace) {
			e.addCapability(prefix("exoflame_heatable"),
					CapabilityUtil.makeProvider(BotaniaForgeCapabilities.EXOFLAME_HEATABLE,
							new ExoflameFurnaceHandler.FurnaceExoflameHeatable(furnace)));
		}

		if (be instanceof ExposedSimpleInventoryBlockEntity inv) {
			e.addCapability(prefix("inv"), CapabilityUtil.makeProvider(ForgeCapabilities.ITEM_HANDLER, new SidedInvWrapper(inv, null)));
		}

		if (be instanceof PowerGeneratorBlockEntity gen) {
			// we only provide a view of the energy level, no interaction allowed
			var energyStorage = new IEnergyStorage() {
				@Override
				public int getEnergyStored() {
					return gen.getEnergy();
				}

				@Override
				public int getMaxEnergyStored() {
					return PowerGeneratorBlockEntity.MAX_ENERGY;
				}

				@Override
				public boolean canExtract() {
					return false;
				}

				@Override
				public int extractEnergy(int maxExtract, boolean simulate) {
					return 0;
				}

				@Override
				public int receiveEnergy(int maxReceive, boolean simulate) {
					return 0;
				}

				@Override
				public boolean canReceive() {
					return false;
				}
			};
			e.addCapability(prefix("fe"), CapabilityUtil.makeProvider(ForgeCapabilities.ENERGY, energyStorage));
		}

		if (be.getType() == BotaniaBlockEntities.ANIMATED_TORCH) {
			e.addCapability(prefix("hourglass_trigger"), CapabilityUtil.makeProvider(BotaniaForgeCapabilities.HOURGLASS_TRIGGER,
					hourglass -> ((AnimatedTorchBlockEntity) be).toggle()));
		}

		if (BlockEntityConstants.SELF_WANDADBLE_BES.contains(be.getType())) {
			e.addCapability(prefix("wandable"), CapabilityUtil.makeProvider(BotaniaForgeCapabilities.WANDABLE,
					(Wandable) be));
		}

		if (be instanceof RedStringContainerBlockEntity container) {
			e.addCapability(prefix("red_string"), new RedStringContainerCapProvider(container));
		}

		if (BlockEntityConstants.SELF_MANA_TRIGGER_BES.contains(be.getType())) {
			e.addCapability(prefix("mana_trigger"), CapabilityUtil.makeProvider(BotaniaForgeCapabilities.MANA_TRIGGER, (ManaTrigger) be));
		}

		if (BlockEntityConstants.SELF_MANA_RECEIVER_BES.contains(be.getType())) {
			e.addCapability(prefix("mana_receiver"), CapabilityUtil.makeProvider(BotaniaForgeCapabilities.MANA_RECEIVER, (ManaReceiver) be));
		}

		if (BlockEntityConstants.SELF_SPARK_ATTACHABLE_BES.contains(be.getType())) {
			e.addCapability(prefix("spark_attachable"), CapabilityUtil.makeProvider(BotaniaForgeCapabilities.SPARK_ATTACHABLE, (SparkAttachable) be));
		}
	}

	private void serverAboutToStart(MinecraftServer server) {
		if (BotaniaAPI.instance().getClass() != BotaniaAPIImpl.class) {
			String clname = BotaniaAPI.instance().getClass().getName();
			throw new IllegalAccessError("The Botania API has been overriden. "
					+ "This will cause crashes and compatibility issues, and that's why it's marked as"
					+ " \"Do not Override\". Whoever had the brilliant idea of overriding it needs to go"
					+ " back to elementary school and learn to read. (Actual classname: " + clname + ")");
		}

		if (server.isDedicatedServer()) {
			ContributorList.firstStart();
		}
	}

	private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
		if (XplatAbstractions.INSTANCE.gogLoaded()) {
			SkyblockCommand.register(dispatcher);
		}
	}

	private void serverStopping(MinecraftServer server) {
		ManaNetworkHandler.instance.clear();
		CorporeaIndexBlockEntity.clearIndexCache();
	}

}
