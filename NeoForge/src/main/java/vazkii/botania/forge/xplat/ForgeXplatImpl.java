package vazkii.botania.forge.xplat;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
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
import net.minecraft.world.item.crafting.RecipeHolder;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.NetworkDirection;
import net.neoforged.neoforge.network.PacketDistributor;

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
import vazkii.botania.forge.block.ForgeSpecialFlowerBlock;
import vazkii.botania.forge.integration.curios.CurioIntegration;
import vazkii.botania.forge.internal_caps.ForgeInternalEntityCapabilities;
import vazkii.botania.forge.mixin.AbstractFurnaceBlockEntityForgeAccessor;
import vazkii.botania.forge.network.ForgePacketHandler;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
	public boolean isDataGen() {
		return DatagenModLoader.isRunningDataGen();
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
		return stack.getCapability(BotaniaForgeCapabilities.AVATAR_WIELDABLE);
	}

	@Nullable
	@Override
	public BlockProvider findBlockProvider(ItemStack stack) {
		return stack.getCapability(BotaniaForgeCapabilities.BLOCK_PROVIDER);
	}

	@Nullable
	@Override
	public CoordBoundItem findCoordBoundItem(ItemStack stack) {
		return stack.getCapability(BotaniaForgeCapabilities.COORD_BOUND_ITEM);
	}

	@Nullable
	@Override
	public ManaItem findManaItem(ItemStack stack) {
		return stack.getCapability(BotaniaForgeCapabilities.MANA_ITEM);
	}

	@Nullable
	@Override
	public Relic findRelic(ItemStack stack) {
		return stack.getCapability(BotaniaForgeCapabilities.RELIC);
	}

	@Nullable
	@Override
	public ExoflameHeatable findExoflameHeatable(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
		return level.getCapability(BotaniaForgeCapabilities.EXOFLAME_HEATABLE, pos, state, be, null);
	}

	@Nullable
	@Override
	public HornHarvestable findHornHarvestable(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
		return level.getCapability(BotaniaForgeCapabilities.HORN_HARVEST, pos, state, be, null);
	}

	@Nullable
	@Override
	public HourglassTrigger findHourglassTrigger(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
		return level.getCapability(BotaniaForgeCapabilities.HOURGLASS_TRIGGER, pos, state, be, null);
	}

	@Nullable
	@Override
	public ManaCollisionGhost findManaGhost(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
		return level.getCapability(BotaniaForgeCapabilities.MANA_GHOST, pos, state, be, null);
	}

	@Nullable
	@Override
	public ManaReceiver findManaReceiver(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be, @Nullable Direction direction) {
		return level.getCapability(BotaniaForgeCapabilities.MANA_RECEIVER, pos, state, be, direction);
	}

	@Nullable
	@Override
	public SparkAttachable findSparkAttachable(Level level, BlockPos pos, BlockState blockState, @Nullable BlockEntity be, Direction direction) {
		return level.getCapability(BotaniaForgeCapabilities.SPARK_ATTACHABLE, pos, blockState, be, direction);
	}

	@Nullable
	@Override
	public ManaTrigger findManaTrigger(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
		return level.getCapability(BotaniaForgeCapabilities.MANA_TRIGGER, pos, state, be, null);
	}

	@Nullable
	@Override
	public Wandable findWandable(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
		return level.getCapability(BotaniaForgeCapabilities.WANDABLE, pos, state, be, null);
	}

	@Override
	public boolean isFluidContainer(ItemEntity item) {
		return item.getItem().getCapability(Capabilities.FluidHandler.ITEM) != null;
	}

	@Override
	public boolean extractFluidFromItemEntity(ItemEntity item, Fluid fluid) {
		// TODO: Refactor code. I just want to get this to compile first.
		return Optional.ofNullable(item.getItem().getCapability(Capabilities.FluidHandler.ITEM))
				.map(h -> {
					var extracted = h.drain(new FluidStack(fluid, FluidType.BUCKET_VOLUME),
							IFluidHandler.FluidAction.SIMULATE);
					var success = extracted.getFluid() == fluid && extracted.getAmount() == FluidType.BUCKET_VOLUME;
					if (success) {
						h.drain(new FluidStack(fluid, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
						item.setItem(h.getContainer());
					}
					return success;
				})
				.orElse(false);
	}

	@Override
	public boolean extractFluidFromPlayerItem(Player player, InteractionHand hand, Fluid fluid) {
		var stack = player.getItemInHand(hand);
		// TODO: Refactor code. I just want to get this to compile first.
		return Optional.ofNullable(stack.getCapability(Capabilities.FluidHandler.ITEM))
				.map(h -> {
					var extracted = h.drain(new FluidStack(fluid, FluidType.BUCKET_VOLUME),
							IFluidHandler.FluidAction.SIMULATE);
					var success = extracted.getFluid() == fluid && extracted.getAmount() == FluidType.BUCKET_VOLUME;
					if (success && !player.getAbilities().instabuild) {
						h.drain(new FluidStack(fluid, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
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

		var fluidHandler = toFill.getCapability(Capabilities.FluidHandler.ITEM);
		if (fluidHandler != null) {
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
		return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, sideOfPos) != null;
	}

	@Override
	public ItemStack insertToInventory(Level level, BlockPos pos, Direction sideOfPos, ItemStack toInsert, boolean simulate) {
		IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, sideOfPos);
		return itemHandler != null ? ItemHandlerHelper.insertItemStacked(itemHandler, toInsert, simulate) : toInsert;
	}

	@Override
	public EthicalComponent ethicalComponent(PrimedTnt tnt) {
		return tnt.getData(ForgeInternalEntityCapabilities.TNT_ETHICAL);
	}

	@Override
	public SpectralRailComponent ghostRailComponent(AbstractMinecart cart) {
		return cart.getData(ForgeInternalEntityCapabilities.GHOST_RAIL);
	}

	@Override
	public ItemFlagsComponent itemFlagsComponent(ItemEntity item) {
		return item.getData(ForgeInternalEntityCapabilities.INTERNAL_ITEM);
	}

	@Override
	public KeptItemsComponent keptItemsComponent(Player player, boolean reviveCaps) {
		return player.getData(ForgeInternalEntityCapabilities.KEPT_ITEMS);
	}

	@Nullable
	@Override
	public LooniumComponent looniumComponent(LivingEntity entity) {
		return entity.getData(ForgeInternalEntityCapabilities.LOONIUM_DROP);
	}

	@Override
	public NarslimmusComponent narslimmusComponent(Slime slime) {
		return slime.getData(ForgeInternalEntityCapabilities.NARSLIMMUS);
	}

	@Override
	public TigerseyeComponent tigersEyeComponent(Creeper creeper) {
		return creeper.getData(ForgeInternalEntityCapabilities.TIGERSEYE);
	}

	@Override
	public boolean fireCorporeaRequestEvent(CorporeaRequestMatcher matcher, int itemCount, CorporeaSpark spark, boolean dryRun) {
		return NeoForge.EVENT_BUS.post(new CorporeaRequestEvent(matcher, itemCount, spark, dryRun));
	}

	@Override
	public boolean fireCorporeaIndexRequestEvent(ServerPlayer player, CorporeaRequestMatcher request, int count, CorporeaSpark spark) {
		return NeoForge.EVENT_BUS.post(new CorporeaIndexRequestEvent(player, request, count, spark));
	}

	@Override
	public void fireManaItemEvent(Player player, List<ItemStack> toReturn) {
		NeoForge.EVENT_BUS.post(new ManaItemsEvent(player, toReturn));
	}

	@Override
	public float fireManaDiscountEvent(Player player, float discount, ItemStack tool) {
		var evt = new ManaDiscountEvent(player, discount, tool);
		NeoForge.EVENT_BUS.post(evt);
		return evt.getDiscount();
	}

	@Override
	public boolean fireManaProficiencyEvent(Player player, ItemStack tool, boolean proficient) {
		var evt = new ManaProficiencyEvent(player, tool, proficient);
		NeoForge.EVENT_BUS.post(evt);
		return evt.isProficient();
	}

	@Override
	public void fireElvenPortalUpdateEvent(BlockEntity portal, AABB bounds, boolean open, List<ItemStack> stacksInside) {
		NeoForge.EVENT_BUS.post(new ElvenPortalUpdateEvent(portal, bounds, open, stacksInside));
	}

	@Override
	public void fireManaNetworkEvent(ManaReceiver thing, ManaBlockType type, ManaNetworkAction action) {
		NeoForge.EVENT_BUS.post(new ManaNetworkEvent(thing, type, action));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Packet<ClientGamePacketListener> toVanillaClientboundPacket(CustomPacketPayload packet) {
		return (Packet<ClientGamePacketListener>) ForgePacketHandler.CHANNEL.toVanillaPacket(packet, NetworkDirection.PLAY_TO_CLIENT);
	}

	@Override
	public void sendToPlayer(Player player, CustomPacketPayload packet) {
		if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
			PacketDistributor.sendToPlayer(serverPlayer, packet);
		}
	}

	@Override
	public void sendToNear(Level level, BlockPos pos, CustomPacketPayload packet) {
		if (!level.isClientSide) {
			PacketDistributor.sendToPlayersNear((ServerLevel) level, null, pos.getX(), pos.getY(), pos.getZ(), 64, packet);
		}
	}

	@Override
	public void sendToTracking(Entity e, CustomPacketPayload packet) {
		if (!e.level().isClientSide) {
			PacketDistributor.sendToPlayersTrackingEntityAndSelf(e, packet);
		}
	}

	@Override
	public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> func, Block... blocks) {
		return BlockEntityType.Builder.of(func::apply, blocks).build(null);
	}

	@Override
	public void registerReloadListener(PackType type, ResourceLocation id, PreparableReloadListener listener) {
		switch (type) {
			case CLIENT_RESOURCES -> NeoForge.EVENT_BUS.addListener(
					(RegisterClientReloadListenersEvent e) -> e.registerReloadListener(listener));
			case SERVER_DATA -> NeoForge.EVENT_BUS.addListener(
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
	public <T extends AbstractContainerMenu, D> MenuType<T> createMenuType(TriFunction<Integer, Inventory, D, T> constructor, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec) {
		return IMenuTypeExtension.create((windowId, inv, data) -> constructor.apply(windowId, inv, streamCodec.decode(data)));
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
	public <D> void openMenu(ServerPlayer player, MenuProvider menu, D initialData, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec) {
		player.openMenu(menu, buf -> streamCodec.encode(buf, initialData));
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
	public boolean canFurnaceBurn(AbstractFurnaceBlockEntity furnace, @Nullable RecipeHolder<?> recipeHolder, NonNullList<ItemStack> items, int maxStackSize) {
		return ((AbstractFurnaceBlockEntityForgeAccessor) furnace)
				.callCanBurn(furnace.getLevel().registryAccess(), recipeHolder, items, maxStackSize);
	}

	@Override
	public Fluid getBucketFluid(BucketItem item) {
		return item.getFluid();
	}

	@Override
	public int getSmeltingBurnTime(ItemStack stack) {
		return CommonHooks.getBurnTime(stack, RecipeType.SMELTING);
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

			IEnergyStorage storage = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, e.getOpposite());

			if (storage == null) {
				storage = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, null);
			}

			if (storage != null) {
				energy -= storage.receiveEnergy(energy, false);

				if (energy <= 0) {
					return 0;
				}
			}
		}
		return energy;
	}

	// TODO: Maybe switch to level and block position?
	@Override
	public boolean isRedStringContainerTarget(BlockEntity be) {
		for (Direction dir : Direction.values()) {
			if (be.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, be.getBlockPos(), be.getBlockState(), be, dir) != null) {
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
		// TODO: There are a bunch of new parameters that need to be incorporated into the API.
		return BlockSetType.register(new BlockSetType("botania:" + name, canOpenByHand, canOpenByHand, canOpenByHand, BlockSetType.PressurePlateSensitivity.EVERYTHING, soundType, doorClose, doorOpen, trapdoorClose, trapdoorOpen, pressurePlateClickOff, pressurePlateClickOn, buttonClickOff, buttonClickOn));
	}

	@Override
	public WoodType registerWoodType(String name, BlockSetType setType, SoundType soundType, SoundType hangingSignSoundType, SoundEvent fenceGateClose, SoundEvent fenceGateOpen) {
		return WoodType.register(new WoodType("botania:" + name, setType, soundType, hangingSignSoundType, fenceGateClose, fenceGateOpen));
	}
}
