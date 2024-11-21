package vazkii.botania.fabric.xplat;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;

import dev.emi.stepheightentityattribute.StepHeightEntityAttributeMain;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;

import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.BotaniaFabricCapabilities;
import vazkii.botania.api.block.*;
import vazkii.botania.api.corporea.CorporeaIndexRequestCallback;
import vazkii.botania.api.corporea.CorporeaRequestCallback;
import vazkii.botania.api.corporea.CorporeaRequestMatcher;
import vazkii.botania.api.corporea.CorporeaSpark;
import vazkii.botania.api.item.AvatarWieldable;
import vazkii.botania.api.item.BlockProvider;
import vazkii.botania.api.item.CoordBoundItem;
import vazkii.botania.api.item.Relic;
import vazkii.botania.api.mana.*;
import vazkii.botania.api.mana.spark.SparkAttachable;
import vazkii.botania.api.recipe.ElvenPortalUpdateCallback;
import vazkii.botania.common.block.block_entity.red_string.RedStringContainerBlockEntity;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.internal_caps.*;
import vazkii.botania.common.item.equipment.CustomDamageItem;
import vazkii.botania.common.lib.LibMisc;
import vazkii.botania.fabric.block_entity.FabricRedStringContainerBlockEntity;
import vazkii.botania.fabric.integration.tr_energy.FluxfieldTRStorage;
import vazkii.botania.fabric.integration.trinkets.TrinketsIntegration;
import vazkii.botania.fabric.internal_caps.CCAInternalEntityComponents;
import vazkii.botania.fabric.mixin.AbstractFurnaceBlockEntityFabricAccessor;
import vazkii.botania.fabric.mixin.BucketItemFabricAccessor;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class FabricXplatImpl implements XplatAbstractions {
	@Override
	public boolean isFabric() {
		return true;
	}

	@Override
	public boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public boolean isDevEnvironment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	@Override
	public boolean isDataGen() {
		return FabricDataGenHelper.ENABLED;
	}

	@Override
	public boolean isPhysicalClient() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	}

	@Override
	public String getBotaniaVersion() {
		return FabricLoader.getInstance().getModContainer(LibMisc.MOD_ID).get()
				.getMetadata().getVersion().getFriendlyString();
	}

	@Nullable
	@Override
	public AvatarWieldable findAvatarWieldable(ItemStack stack) {
		return BotaniaFabricCapabilities.AVATAR_WIELDABLE.find(stack, Unit.INSTANCE);
	}

	@Nullable
	@Override
	public BlockProvider findBlockProvider(ItemStack stack) {
		return BotaniaFabricCapabilities.BLOCK_PROVIDER.find(stack, Unit.INSTANCE);
	}

	@Nullable
	@Override
	public CoordBoundItem findCoordBoundItem(ItemStack stack) {
		return BotaniaFabricCapabilities.COORD_BOUND_ITEM.find(stack, Unit.INSTANCE);
	}

	@Nullable
	@Override
	public ManaItem findManaItem(ItemStack stack) {
		return BotaniaFabricCapabilities.MANA_ITEM.find(stack, Unit.INSTANCE);
	}

	@Nullable
	@Override
	public Relic findRelic(ItemStack stack) {
		return BotaniaFabricCapabilities.RELIC.find(stack, Unit.INSTANCE);
	}

	@Nullable
	@Override
	public ExoflameHeatable findExoflameHeatable(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
		return BotaniaFabricCapabilities.EXOFLAME_HEATABLE.find(level, pos, state, be, Unit.INSTANCE);
	}

	@Nullable
	@Override
	public HornHarvestable findHornHarvestable(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
		return BotaniaFabricCapabilities.HORN_HARVEST.find(level, pos, state, be, Unit.INSTANCE);
	}

	@Nullable
	@Override
	public HourglassTrigger findHourglassTrigger(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
		return BotaniaFabricCapabilities.HOURGLASS_TRIGGER.find(level, pos, state, be, Unit.INSTANCE);
	}

	@Nullable
	@Override
	public ManaCollisionGhost findManaGhost(Level level, BlockPos pos, BlockState state, @org.jetbrains.annotations.Nullable BlockEntity be) {
		return BotaniaFabricCapabilities.MANA_GHOST.find(level, pos, state, be, Unit.INSTANCE);
	}

	@Nullable
	@Override
	public ManaReceiver findManaReceiver(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be, Direction direction) {
		return BotaniaFabricCapabilities.MANA_RECEIVER.find(level, pos, state, be, direction);
	}

	@Nullable
	@Override
	public SparkAttachable findSparkAttachable(Level level, BlockPos pos, BlockState blockState, @Nullable BlockEntity be, Direction direction) {
		return BotaniaFabricCapabilities.SPARK_ATTACHABLE.find(level, pos, blockState, be, direction);
	}

	@Nullable
	@Override
	public ManaTrigger findManaTrigger(Level level, BlockPos pos, BlockState state, @org.jetbrains.annotations.Nullable BlockEntity be) {
		return BotaniaFabricCapabilities.MANA_TRIGGER.find(level, pos, state, be, Unit.INSTANCE);
	}

	@Nullable
	@Override
	public Wandable findWandable(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
		return BotaniaFabricCapabilities.WANDABLE.find(level, pos, state, be, Unit.INSTANCE);
	}

	private static class SingleStackEntityStorage extends SingleStackStorage {
		private final ItemEntity entity;

		private SingleStackEntityStorage(ItemEntity entity) {
			this.entity = entity;
		}

		@Override
		protected ItemStack getStack() {
			return entity.getItem();
		}

		@Override
		protected void setStack(ItemStack stack) {
			entity.setItem(stack);
		}
	}

	@Override
	public boolean isFluidContainer(ItemEntity item) {
		return ContainerItemContext.ofSingleSlot(new SingleStackEntityStorage(item)).find(FluidStorage.ITEM) != null;
	}

	@Override
	public boolean extractFluidFromItemEntity(ItemEntity item, Fluid fluid) {
		var fluidStorage = ContainerItemContext.ofSingleSlot(new SingleStackEntityStorage(item)).find(FluidStorage.ITEM);
		if (fluidStorage == null) {
			return false;
		}
		try (Transaction txn = Transaction.openOuter()) {
			long extracted = fluidStorage.extract(FluidVariant.of(fluid), FluidConstants.BLOCK, txn);
			if (extracted == FluidConstants.BLOCK) {
				txn.commit();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean extractFluidFromPlayerItem(Player player, InteractionHand hand, Fluid fluid) {
		var fluidStorage = ContainerItemContext.ofPlayerHand(player, hand).find(FluidStorage.ITEM);
		if (fluidStorage == null) {
			return false;
		}
		try (Transaction txn = Transaction.openOuter()) {
			long extracted = fluidStorage.extract(FluidVariant.of(fluid), FluidConstants.BUCKET, txn);
			if (extracted == FluidConstants.BUCKET) {
				if (!player.getAbilities().instabuild) {
					// Only perform inventory side effects in survival
					txn.commit();
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean insertFluidIntoPlayerItem(Player player, InteractionHand hand, Fluid fluid) {
		var fluidStorage = ContainerItemContext.ofPlayerHand(player, hand).find(FluidStorage.ITEM);

		if (fluidStorage == null) {
			return false;
		}

		try (Transaction txn = Transaction.openOuter()) {
			long inserted = fluidStorage.insert(FluidVariant.of(fluid), FluidConstants.BUCKET, txn);
			if (inserted == FluidConstants.BUCKET) {
				if (!player.getAbilities().instabuild) {
					// Only perform inventory side effects in survival
					txn.commit();
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
		return ItemStorage.SIDED.find(level, pos, state, be, sideOfPos) != null;
	}

	@Override
	public ItemStack insertToInventory(Level level, BlockPos pos, Direction sideOfPos, ItemStack toInsert, boolean simulate) {
		if (toInsert.isEmpty()) {
			//It is valid for Storage#insert implementations to crash when provided empty variants
			return toInsert;
		}

		var state = level.getBlockState(pos);
		var be = level.getBlockEntity(pos);
		var storage = ItemStorage.SIDED.find(level, pos, state, be, sideOfPos);
		if (storage == null) {
			return toInsert;
		}

		var itemVariant = ItemVariant.of(toInsert);
		try (Transaction txn = Transaction.openOuter()) {
			// Truncation to int ok since the value passed in was an int
			// and that value should only decrease or stay same
			int inserted = (int) storage.insert(itemVariant, toInsert.getCount(), txn);
			if (!simulate) {
				txn.commit();
			}

			if (inserted == toInsert.getCount()) {
				return ItemStack.EMPTY;
			} else {
				var ret = toInsert.copy();
				ret.setCount(toInsert.getCount() - inserted);
				return ret;
			}
		}
	}

	@Override
	public EthicalComponent ethicalComponent(PrimedTnt tnt) {
		return CCAInternalEntityComponents.TNT_ETHICAL.get(tnt);
	}

	@Override
	public SpectralRailComponent ghostRailComponent(AbstractMinecart cart) {
		return CCAInternalEntityComponents.GHOST_RAIL.get(cart);
	}

	@Override
	public ItemFlagsComponent itemFlagsComponent(ItemEntity item) {
		return CCAInternalEntityComponents.INTERNAL_ITEM.get(item);
	}

	@Override
	public KeptItemsComponent keptItemsComponent(Player player, boolean reviveCaps) {
		return CCAInternalEntityComponents.KEPT_ITEMS.get(player);
	}

	@Nullable
	@Override
	public LooniumComponent looniumComponent(LivingEntity entity) {
		return CCAInternalEntityComponents.LOONIUM_DROP.getNullable(entity);
	}

	@Override
	public NarslimmusComponent narslimmusComponent(Slime slime) {
		return CCAInternalEntityComponents.NARSLIMMUS.get(slime);
	}

	@Override
	public TigerseyeComponent tigersEyeComponent(Creeper creeper) {
		return CCAInternalEntityComponents.TIGERSEYE.get(creeper);
	}

	@Override
	public boolean fireCorporeaRequestEvent(CorporeaRequestMatcher matcher, int itemCount, CorporeaSpark spark, boolean dryRun) {
		return CorporeaRequestCallback.EVENT.invoker().onRequest(matcher, itemCount, spark, dryRun);
	}

	@Override
	public boolean fireCorporeaIndexRequestEvent(ServerPlayer player, CorporeaRequestMatcher request, int count, CorporeaSpark spark) {
		return CorporeaIndexRequestCallback.EVENT.invoker().onIndexRequest(player, request, count, spark);
	}

	@Override
	public void fireManaItemEvent(Player player, List<ItemStack> toReturn) {
		ManaItemsCallback.EVENT.invoker().getManaItems(player, toReturn);
	}

	@Override
	public float fireManaDiscountEvent(Player player, float discount, ItemStack tool) {
		return ManaDiscountCallback.EVENT.invoker().getManaDiscount(player, discount, tool);
	}

	@Override
	public boolean fireManaProficiencyEvent(Player player, ItemStack tool, boolean proficient) {
		return ManaProficiencyCallback.EVENT.invoker().getProficient(player, tool, proficient);
	}

	@Override
	public void fireElvenPortalUpdateEvent(BlockEntity portal, AABB bounds, boolean open, List<ItemStack> stacksInside) {
		ElvenPortalUpdateCallback.EVENT.invoker().onElvenPortalTick(portal, bounds, open, stacksInside);
	}

	@Override
	public void fireManaNetworkEvent(ManaReceiver thing, ManaBlockType type, ManaNetworkAction action) {
		ManaNetworkCallback.EVENT.invoker().onNetworkChange(thing, type, action);
	}

	@Override
	public Packet<ClientGamePacketListener> toVanillaClientboundPacket(CustomPacketPayload packet) {
		return ServerPlayNetworking.createS2CPacket(packet.getFabricId(), packet.toBuf());
	}

	@Override
	public void sendToPlayer(Player player, CustomPacketPayload packet) {
		if (player instanceof ServerPlayer serverPlayer) {
			ServerPlayNetworking.send(serverPlayer, packet);
		}
	}

	@Override
	public void sendToNear(Level level, BlockPos pos, CustomPacketPayload packet) {
		var pkt = ServerPlayNetworking.createS2CPacket(packet);
		for (var player : PlayerLookup.tracking((ServerLevel) level, pos)) {
			if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64 * 64) {
				player.connection.send(pkt);
			}
		}
	}

	@Override
	public void sendToTracking(Entity e, CustomPacketPayload packet) {
		var pkt = ServerPlayNetworking.createS2CPacket(packet);
		PlayerLookup.tracking(e).forEach(p -> p.connection.send(pkt));
		if (e instanceof ServerPlayer) {
			((ServerPlayer) e).connection.send(pkt);
		}
	}

	@Override
	public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> func, Block... blocks) {
		return FabricBlockEntityTypeBuilder.create(func::apply, blocks).build();
	}

	@Override
	public void registerReloadListener(PackType type, ResourceLocation id, PreparableReloadListener listener) {
		ResourceManagerHelper.get(type).registerReloadListener(new IdentifiableResourceReloadListener() {
			@Override
			public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, ProfilerFiller prepProfiler,
					ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
				return listener.reload(barrier, manager, prepProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
			}

			@Override
			public ResourceLocation getFabricId() {
				return id;
			}
		});
	}

	@Override
	public FabricItemSettings defaultItemBuilder() {
		return new FabricItemSettings();
	}

	@Override
	public Item.Properties defaultItemBuilderWithCustomDamageOnFabric() {
		return defaultItemBuilder().customDamage((stack, amount, entity, breakCallback) -> {
			var item = stack.getItem();
			if (item instanceof CustomDamageItem cd) {
				return cd.damageItem(stack, amount, entity, breakCallback);
			}
			return amount;
		});
	}

	@Override
	public <T extends AbstractContainerMenu, D> MenuType<T> createMenuType(TriFunction<Integer, Inventory, D, T> constructor, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec) {
		return new ExtendedScreenHandlerType<>(constructor::apply, streamCodec);
	}

	@Nullable
	@Override
	public EquipmentHandler tryCreateEquipmentHandler() {
		if (isModLoaded("trinkets")) {
			TrinketsIntegration.init();
			return new TrinketsIntegration();
		}
		return null;
	}

	@Override
	public <D> void openMenu(ServerPlayer player, MenuProvider menu, D initialData, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec) {
		var menuProvider = new ExtendedScreenHandlerFactory<D>() {
			@Nullable
			@Override
			public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
				return menu.createMenu(id, inventory, player);
			}

			@Override
			public Component getDisplayName() {
				return menu.getDisplayName();
			}

			@Override
			public D getScreenOpeningData(ServerPlayer player) {
				return initialData;
			}
		};
		player.openMenu(menuProvider);
	}

	private final TagKey<Block> oreTag = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "ores"));

	@Override
	public TagKey<Block> getOreTag() {
		return oreTag;
	}

	// No standard so we have to check both :wacko:
	private final TagKey<Block> cGlass = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "glass"));
	private final TagKey<Block> cGlassBlocks = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "glass_blocks"));
	private final TagKey<Block> cGlassPanes = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "glass_panes"));

	@Override
	public boolean isInGlassTag(BlockState state) {
		return state.is(cGlass) || state.is(cGlassBlocks) || state.is(cGlassPanes);
	}

	@Override
	public boolean canFurnaceBurn(AbstractFurnaceBlockEntity furnace, @Nullable RecipeHolder<?> recipeHolder, NonNullList<ItemStack> items, int maxStackSize) {
		return AbstractFurnaceBlockEntityFabricAccessor.callCanBurn(furnace.getLevel().registryAccess(), recipeHolder, items, maxStackSize);
	}

	@Override
	public Fluid getBucketFluid(BucketItem item) {
		return ((BucketItemFabricAccessor) item).getContent();
	}

	@Override
	public int getSmeltingBurnTime(ItemStack stack) {
		Integer v = FuelRegistry.INSTANCE.get(stack.getItem());
		return v == null ? 0 : v;
	}

	@Override
	public boolean preventsRemoteMovement(ItemEntity entity) {
		return false;
	}

	public static final Map<Block, Block> CUSTOM_STRIPPING = new LinkedHashMap<>();

	@Override
	public void addAxeStripping(Block input, Block output) {
		if (input.getStateDefinition().getProperties().contains(BlockStateProperties.AXIS)
				&& output.getStateDefinition().getProperties().contains(BlockStateProperties.AXIS)) {
			StrippableBlockRegistry.register(input, output);
		} else {
			CUSTOM_STRIPPING.put(input, output);
		}
	}

	@Override
	public int transferEnergyToNeighbors(Level level, BlockPos pos, int energy) {
		if (isModLoaded("team_reborn_energy")) {
			return FluxfieldTRStorage.transferEnergyToNeighbors(level, pos, energy);
		}
		return energy;
	}

	@Override
	public boolean isRedStringContainerTarget(BlockEntity be) {
		if (be.getLevel().isClientSide) {
			return false;
		}
		for (Direction value : Direction.values()) {
			if (ItemStorage.SIDED.find(be.getLevel(), be.getBlockPos(), be.getBlockState(), be, value) != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public RedStringContainerBlockEntity newRedStringContainer(BlockPos pos, BlockState state) {
		return new FabricRedStringContainerBlockEntity(pos, state);
	}

	@Override
	public BlockSetType registerBlockSetType(String name, boolean canOpenByHand, SoundType soundType, SoundEvent doorClose, SoundEvent doorOpen, SoundEvent trapdoorClose, SoundEvent trapdoorOpen, SoundEvent pressurePlateClickOff, SoundEvent pressurePlateClickOn, SoundEvent buttonClickOff, SoundEvent buttonClickOn) {
		return BlockSetTypeBuilder.copyOf(BlockSetType.OAK)
				.openableByHand(canOpenByHand)
				.openableByWindCharge(canOpenByHand)
				.soundGroup(soundType)
				.doorCloseSound(doorClose)
				.doorOpenSound(doorOpen)
				.trapdoorCloseSound(trapdoorClose)
				.trapdoorOpenSound(trapdoorOpen)
				.pressurePlateClickOffSound(pressurePlateClickOff)
				.pressurePlateClickOnSound(pressurePlateClickOn)
				.buttonClickOffSound(buttonClickOff)
				.buttonClickOnSound(buttonClickOn)
				.register(botaniaRL(name));
	}

	@Override
	public WoodType registerWoodType(String name, BlockSetType setType, SoundType soundType, SoundType hangingSignSoundType, SoundEvent fenceGateClose, SoundEvent fenceGateOpen) {
		return WoodTypeBuilder.copyOf(WoodType.OAK)
				.soundGroup(soundType)
				.hangingSignSoundGroup(hangingSignSoundType)
				.fenceGateCloseSound(fenceGateClose)
				.fenceGateOpenSound(fenceGateOpen)
				.register(botaniaRL(name), setType);
	}
}
