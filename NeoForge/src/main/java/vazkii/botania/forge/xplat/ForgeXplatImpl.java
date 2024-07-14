package vazkii.botania.forge.xplat;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.block.ExoflameHeatable;
import vazkii.botania.api.block.HornHarvestable;
import vazkii.botania.api.block.HourglassTrigger;
import vazkii.botania.api.block.Wandable;
import vazkii.botania.api.block_entity.SpecialFlowerBlockEntity;
import vazkii.botania.api.corporea.CorporeaIndexRequestEvent;
import vazkii.botania.api.corporea.CorporeaRequestEvent;
import vazkii.botania.api.corporea.CorporeaRequestMatcher;
import vazkii.botania.api.corporea.CorporeaSpark;
import vazkii.botania.api.item.AvatarWieldable;
import vazkii.botania.api.item.BlockProvider;
import vazkii.botania.api.item.CoordBoundItem;
import vazkii.botania.api.item.Relic;
import vazkii.botania.api.mana.*;
import vazkii.botania.api.mana.spark.SparkAttachable;
import vazkii.botania.api.recipe.ElvenPortalUpdateEvent;
import vazkii.botania.common.block.block_entity.red_string.RedStringContainerBlockEntity;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.internal_caps.*;
import vazkii.botania.common.lib.LibMisc;
import vazkii.botania.forge.CapabilityUtil;
import vazkii.botania.forge.block.ForgeSpecialFlowerBlock;
import vazkii.botania.forge.integration.curios.CurioIntegration;
import vazkii.botania.forge.internal_caps.ForgeInternalEntityCapabilities;
import vazkii.botania.forge.mixin.AbstractFurnaceBlockEntityForgeAccessor;
import vazkii.botania.forge.network.ForgePacketHandler;
import vazkii.botania.network.BotaniaPacket;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ForgeXplatImpl implements XplatAbstractions {
	@Override
	public boolean isForge() {
		return true;
	}

	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public boolean isDevEnvironment() {
		return !FMLLoader.isProduction();
	}

	@Override
	public boolean isPhysicalClient() {
		return FMLLoader.getDist() == Dist.CLIENT;
	}

	@Override
	public String getBotaniaVersion() {
		return ModList.get().getModContainerById(LibMisc.MOD_ID).get()
				.getModInfo().getVersion().toString();
	}

	@Nullable
	@Override
	public AvatarWieldable findAvatarWieldable(ItemStack stack) {
		return stack.getCapability(BotaniaForgeCapabilities.AVATAR_WIELDABLE).orElse(null);
	}

	@Nullable
	@Override
	public BlockProvider findBlockProvider(ItemStack stack) {
		return stack.getCapability(BotaniaForgeCapabilities.BLOCK_PROVIDER).orElse(null);
	}

	@Nullable
	@Override
	public CoordBoundItem findCoordBoundItem(ItemStack stack) {
		return stack.getCapability(BotaniaForgeCapabilities.COORD_BOUND_ITEM).orElse(null);
	}

	@Nullable
	@Override
	public ManaItem findManaItem(ItemStack stack) {
		return stack.getCapability(BotaniaForgeCapabilities.MANA_ITEM).orElse(null);
	}

	@Nullable
	@Override
	public Relic findRelic(ItemStack stack) {
		return stack.getCapability(BotaniaForgeCapabilities.RELIC).orElse(null);
	}

	@Nullable
	@Override
	public ExoflameHeatable findExoflameHeatable(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
		return CapabilityUtil.findCapability(BotaniaForgeCapabilities.EXOFLAME_HEATABLE, level, pos, state, be);
	}

	@Nullable
	@Override
	public HornHarvestable findHornHarvestable(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
		return CapabilityUtil.findCapability(BotaniaForgeCapabilities.HORN_HARVEST, level, pos, state, be);
	}

	@Nullable
	@Override
	public HourglassTrigger findHourglassTrigger(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
		return CapabilityUtil.findCapability(BotaniaForgeCapabilities.HOURGLASS_TRIGGER, level, pos, state, be);
	}

	@Nullable
	@Override
	public ManaCollisionGhost findManaGhost(Level level, BlockPos pos, BlockState state, @org.jetbrains.annotations.Nullable BlockEntity be) {
		return CapabilityUtil.findCapability(BotaniaForgeCapabilities.MANA_GHOST, level, pos, state, be);
	}

	@Nullable
	@Override
	public ManaReceiver findManaReceiver(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be, @Nullable Direction direction) {
		return CapabilityUtil.findCapability(BotaniaForgeCapabilities.MANA_RECEIVER, level, pos, state, be, direction);
	}

	@Nullable
	@Override
	public SparkAttachable findSparkAttachable(Level level, BlockPos pos, BlockState blockState, @Nullable BlockEntity be, Direction direction) {
		return CapabilityUtil.findCapability(BotaniaForgeCapabilities.SPARK_ATTACHABLE, level, pos, blockState, be, direction);
	}

	@Nullable
	@Override
	public ManaTrigger findManaTrigger(Level level, BlockPos pos, BlockState state, @org.jetbrains.annotations.Nullable BlockEntity be) {
		return CapabilityUtil.findCapability(BotaniaForgeCapabilities.MANA_TRIGGER, level, pos, state, be);
	}

	@Nullable
	@Override
	public Wandable findWandable(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
		return CapabilityUtil.findCapability(BotaniaForgeCapabilities.WANDABLE, level, pos, state, be);
	}

	@Override
	public boolean isFluidContainer(ItemEntity item) {
		return item.getItem().getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
	}

	@Override
	public boolean extractFluidFromItemEntity(ItemEntity item, Fluid fluid) {
		return item.getItem().getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
				.map(h -> {
					var extracted = h.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
					var success = extracted.getFluid() == fluid && extracted.getAmount() == FluidType.BUCKET_VOLUME;
					if (success) {
						h.drain(extracted, IFluidHandler.FluidAction.EXECUTE);
						item.setItem(h.getContainer());
					}
					return success;
				})
				.orElse(false);
	}

	@Override
	public boolean extractFluidFromPlayerItem(Player player, InteractionHand hand, Fluid fluid) {
		var stack = player.getItemInHand(hand);
		return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
				.map(h -> {
					var extracted = h.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
					var success = extracted.getFluid() == fluid && extracted.getAmount() == FluidType.BUCKET_VOLUME;
					if (success && !player.getAbilities().instabuild) {
						h.drain(extracted, IFluidHandler.FluidAction.EXECUTE);
						player.setItemInHand(hand, h.getContainer());
					}
					return success;
				})
				.orElse(false);
	}

	@Override
	public boolean insertFluidIntoPlayerItem(Player player, InteractionHand hand, Fluid fluid) {
		var stack = player.getItemInHand(hand);
		if (stack.isEmpty()) {
			return false;
		}

		// Have to copy and simulate on a stack of size 1, because buckets don't accept
		// fluid input if they're stacked (why Forge??)
		ItemStack toFill = stack.copy();
		toFill.setCount(1);

		var maybeFluidHandler = toFill.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
		if (maybeFluidHandler.isPresent()) {
			var fluidHandler = maybeFluidHandler.orElseThrow(IllegalStateException::new);
			var fluidToFill = new FluidStack(fluid, FluidType.BUCKET_VOLUME);
			int filled = fluidHandler.fill(fluidToFill, IFluidHandler.FluidAction.SIMULATE);

			if (filled == FluidType.BUCKET_VOLUME) {
				if (!player.getAbilities().instabuild) {
					fluidHandler.fill(fluidToFill, IFluidHandler.FluidAction.EXECUTE);
					stack.shrink(1);
					ItemStack result = fluidHandler.getContainer();
					if (stack.isEmpty()) {
						player.setItemInHand(hand, result);
					} else {
						player.getInventory().placeItemBackInInventory(result);
					}
				}
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasInventory(Level level, BlockPos pos, Direction sideOfPos) {
		var state = level.getBlockState(pos);
		var be = level.getBlockEntity(pos);
		return be != null && be.getCapability(ForgeCapabilities.ITEM_HANDLER, sideOfPos).isPresent()
				|| state.getBlock() instanceof WorldlyContainerHolder wch
						&& wch.getContainer(state, level, pos).getSlotsForFace(sideOfPos).length > 0;
	}

	@Override
	public ItemStack insertToInventory(Level level, BlockPos pos, Direction sideOfPos, ItemStack toInsert, boolean simulate) {
		var be = level.getBlockEntity(pos);
		LazyOptional<IItemHandler> cap = LazyOptional.empty();
		if (be != null) {
			cap = be.getCapability(ForgeCapabilities.ITEM_HANDLER, sideOfPos);
		} else {
			// check vanilla interface for blocks not covered by forge capabilities, e.g. composter
			var state = level.getBlockState(pos);
			if (state.getBlock() instanceof WorldlyContainerHolder wch) {
				cap = LazyOptional.of(() -> new SidedInvWrapper(wch.getContainer(state, level, pos), sideOfPos));
			}
		}

		return cap.map(handler -> ItemHandlerHelper.insertItemStacked(handler, toInsert, simulate))
				.orElse(toInsert);
	}

	@Override
	public EthicalComponent ethicalComponent(PrimedTnt tnt) {
		return tnt.getCapability(ForgeInternalEntityCapabilities.TNT_ETHICAL).orElseThrow(IllegalStateException::new);
	}

	@Override
	public SpectralRailComponent ghostRailComponent(AbstractMinecart cart) {
		return cart.getCapability(ForgeInternalEntityCapabilities.GHOST_RAIL).orElseThrow(IllegalStateException::new);
	}

	@Override
	public ItemFlagsComponent itemFlagsComponent(ItemEntity item) {
		// If missing, just give a fresh instance - works around Create's Ponder fake world
		return item.getCapability(ForgeInternalEntityCapabilities.INTERNAL_ITEM).orElseGet(ItemFlagsComponent::new);
	}

	@Override
	public KeptItemsComponent keptItemsComponent(Player player, boolean reviveCaps) {
		if (reviveCaps) {
			// See the javadoc on reviveCaps for why this is necessary
			player.reviveCaps();
		}
		var ret = player.getCapability(ForgeInternalEntityCapabilities.KEPT_ITEMS).orElseThrow(IllegalStateException::new);
		if (reviveCaps) {
			player.invalidateCaps();
		}
		return ret;
	}

	@Nullable
	@Override
	public LooniumComponent looniumComponent(LivingEntity entity) {
		return entity.getCapability(ForgeInternalEntityCapabilities.LOONIUM_DROP).orElse(null);
	}

	@Override
	public NarslimmusComponent narslimmusComponent(Slime slime) {
		return slime.getCapability(ForgeInternalEntityCapabilities.NARSLIMMUS).orElseThrow(IllegalStateException::new);
	}

	@Override
	public TigerseyeComponent tigersEyeComponent(Creeper creeper) {
		return creeper.getCapability(ForgeInternalEntityCapabilities.TIGERSEYE).orElseThrow(IllegalStateException::new);
	}

	@Override
	public boolean fireCorporeaRequestEvent(CorporeaRequestMatcher matcher, int itemCount, CorporeaSpark spark, boolean dryRun) {
		return MinecraftForge.EVENT_BUS.post(new CorporeaRequestEvent(matcher, itemCount, spark, dryRun));
	}

	@Override
	public boolean fireCorporeaIndexRequestEvent(ServerPlayer player, CorporeaRequestMatcher request, int count, CorporeaSpark spark) {
		return MinecraftForge.EVENT_BUS.post(new CorporeaIndexRequestEvent(player, request, count, spark));
	}

	@Override
	public void fireManaItemEvent(Player player, List<ItemStack> toReturn) {
		MinecraftForge.EVENT_BUS.post(new ManaItemsEvent(player, toReturn));
	}

	@Override
	public float fireManaDiscountEvent(Player player, float discount, ItemStack tool) {
		var evt = new ManaDiscountEvent(player, discount, tool);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.getDiscount();
	}

	@Override
	public boolean fireManaProficiencyEvent(Player player, ItemStack tool, boolean proficient) {
		var evt = new ManaProficiencyEvent(player, tool, proficient);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.isProficient();
	}

	@Override
	public void fireElvenPortalUpdateEvent(BlockEntity portal, AABB bounds, boolean open, List<ItemStack> stacksInside) {
		MinecraftForge.EVENT_BUS.post(new ElvenPortalUpdateEvent(portal, bounds, open, stacksInside));
	}

	@Override
	public void fireManaNetworkEvent(ManaReceiver thing, ManaBlockType type, ManaNetworkAction action) {
		MinecraftForge.EVENT_BUS.post(new ManaNetworkEvent(thing, type, action));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Packet<ClientGamePacketListener> toVanillaClientboundPacket(BotaniaPacket packet) {
		return (Packet<ClientGamePacketListener>) ForgePacketHandler.CHANNEL.toVanillaPacket(packet, NetworkDirection.PLAY_TO_CLIENT);
	}

	@Override
	public void sendToPlayer(Player player, BotaniaPacket packet) {
		if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
			ForgePacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
		}
	}

	private static final PacketDistributor<Pair<Level, BlockPos>> TRACKING_CHUNK_AND_NEAR = new PacketDistributor<>(
			(_d, pairSupplier) -> {
				var pair = pairSupplier.get();
				var level = pair.getFirst();
				var blockpos = pair.getSecond();
				var chunkpos = new ChunkPos(blockpos);
				return packet -> {
					var players = ((ServerChunkCache) level.getChunkSource()).chunkMap
							.getPlayers(chunkpos, false);
					for (var player : players) {
						if (player.distanceToSqr(blockpos.getX(), blockpos.getY(), blockpos.getZ()) < 64 * 64) {
							player.connection.send(packet);
						}
					}
				};
			},
			NetworkDirection.PLAY_TO_CLIENT
	);

	@Override
	public void sendToNear(Level level, BlockPos pos, BotaniaPacket packet) {
		if (!level.isClientSide) {
			ForgePacketHandler.CHANNEL.send(TRACKING_CHUNK_AND_NEAR.with(() -> Pair.of(level, pos)), packet);
		}
	}

	@Override
	public void sendToTracking(Entity e, BotaniaPacket packet) {
		if (!e.level().isClientSide) {
			ForgePacketHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> e), packet);
		}
	}

	@Override
	public boolean isSpecialFlowerBlock(Block b) {
		return b instanceof ForgeSpecialFlowerBlock;
	}

	@Override
	public FlowerBlock createSpecialFlowerBlock(MobEffect effect, int effectDuration,
			BlockBehaviour.Properties props,
			Supplier<BlockEntityType<? extends SpecialFlowerBlockEntity>> beType,
			boolean hasComparatorOutput) {
		return new ForgeSpecialFlowerBlock(effect, effectDuration, props, beType, hasComparatorOutput);
	}

	@Override
	public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> func, Block... blocks) {
		return BlockEntityType.Builder.of(func::apply, blocks).build(null);
	}

	@Override
	public void registerReloadListener(PackType type, ResourceLocation id, PreparableReloadListener listener) {
		switch (type) {
			case CLIENT_RESOURCES -> MinecraftForge.EVENT_BUS.addListener(
					(RegisterClientReloadListenersEvent e) -> e.registerReloadListener(listener));
			case SERVER_DATA -> MinecraftForge.EVENT_BUS.addListener(
					(AddReloadListenerEvent e) -> e.addListener(listener));
		}
	}

	@Override
	public Item.Properties defaultItemBuilder() {
		return new Item.Properties();
	}

	@Override
	public Item.Properties noRepairOnForge(Item.Properties builder) {
		return builder.setNoRepair();
	}

	@Override
	public <T extends AbstractContainerMenu> MenuType<T> createMenuType(TriFunction<Integer, Inventory, FriendlyByteBuf, T> constructor) {
		return IForgeMenuType.create(constructor::apply);
	}

	@Nullable
	@Override
	public EquipmentHandler tryCreateEquipmentHandler() {
		if (XplatAbstractions.INSTANCE.isModLoaded("curios")) {
			CurioIntegration.init();
			return new CurioIntegration();
		}
		return null;
	}

	@Override
	public void openMenu(ServerPlayer player, MenuProvider menu, Consumer<FriendlyByteBuf> writeInitialData) {
		NetworkHooks.openScreen(player, menu, writeInitialData);
	}

	@Override
	public Attribute getReachDistanceAttribute() {
		return ForgeMod.BLOCK_REACH.get();
	}

	@Override
	public Attribute getStepHeightAttribute() {
		return ForgeMod.STEP_HEIGHT_ADDITION.get();
	}

	@Override
	public TagKey<Block> getOreTag() {
		return Tags.Blocks.ORES;
	}

	@Override
	public boolean isInGlassTag(BlockState state) {
		return state.is(Tags.Blocks.GLASS) || state.is(Tags.Blocks.GLASS_PANES);
	}

	@Override
	public boolean canFurnaceBurn(AbstractFurnaceBlockEntity furnace, @Nullable Recipe<?> recipe, NonNullList<ItemStack> items, int maxStackSize) {
		return ((AbstractFurnaceBlockEntityForgeAccessor) furnace)
				.callCanBurn(furnace.getLevel().registryAccess(), recipe, items, maxStackSize);
	}

	@Override
	public Fluid getBucketFluid(BucketItem item) {
		return item.getFluid();
	}

	@Override
	public int getSmeltingBurnTime(ItemStack stack) {
		return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
	}

	@Override
	public boolean preventsRemoteMovement(ItemEntity entity) {
		return entity.getPersistentData().getBoolean("PreventRemoteMovement");
	}

	public static final Map<Block, Block> CUSTOM_STRIPPABLES = new HashMap<>();

	@Override
	public void addAxeStripping(Block input, Block output) {
		CUSTOM_STRIPPABLES.put(input, output);
	}

	@Override
	public int transferEnergyToNeighbors(Level level, BlockPos pos, int energy) {
		for (Direction e : Direction.values()) {
			BlockPos neighbor = pos.relative(e);
			if (!level.hasChunkAt(neighbor)) {
				continue;
			}

			BlockEntity be = level.getBlockEntity(neighbor);
			if (be == null) {
				continue;
			}

			LazyOptional<IEnergyStorage> storage = LazyOptional.empty();

			if (be.getCapability(ForgeCapabilities.ENERGY, e.getOpposite()).isPresent()) {
				storage = be.getCapability(ForgeCapabilities.ENERGY, e.getOpposite());
			} else if (be.getCapability(ForgeCapabilities.ENERGY, null).isPresent()) {
				storage = be.getCapability(ForgeCapabilities.ENERGY, null);
			}

			if (storage.isPresent()) {
				energy -= storage.orElseThrow(NullPointerException::new).receiveEnergy(energy, false);

				if (energy <= 0) {
					return 0;
				}
			}
		}
		return energy;
	}

	@Override
	public boolean isRedStringContainerTarget(BlockEntity be) {
		for (Direction dir : Direction.values()) {
			if (be.getCapability(ForgeCapabilities.ITEM_HANDLER, dir).isPresent()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public RedStringContainerBlockEntity newRedStringContainer(BlockPos pos, BlockState state) {
		return new RedStringContainerBlockEntity(pos, state);
	}

	@Override
	public BlockSetType registerBlockSetType(String name, boolean canOpenByHand, SoundType soundType, SoundEvent doorClose, SoundEvent doorOpen, SoundEvent trapdoorClose, SoundEvent trapdoorOpen, SoundEvent pressurePlateClickOff, SoundEvent pressurePlateClickOn, SoundEvent buttonClickOff, SoundEvent buttonClickOn) {
		return BlockSetType.register(new BlockSetType("botania:" + name, canOpenByHand, soundType, doorClose, doorOpen, trapdoorClose, trapdoorOpen, pressurePlateClickOff, pressurePlateClickOn, buttonClickOff, buttonClickOn));
	}

	@Override
	public WoodType registerWoodType(String name, BlockSetType setType, SoundType soundType, SoundType hangingSignSoundType, SoundEvent fenceGateClose, SoundEvent fenceGateOpen) {
		return WoodType.register(new WoodType("botania:" + name, setType, soundType, hangingSignSoundType, fenceGateClose, fenceGateOpen));
	}
}
