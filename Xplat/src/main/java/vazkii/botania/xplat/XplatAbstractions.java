package vazkii.botania.xplat;

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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
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

import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.ServiceUtil;
import vazkii.botania.api.block.ExoflameHeatable;
import vazkii.botania.api.block.HornHarvestable;
import vazkii.botania.api.block.HourglassTrigger;
import vazkii.botania.api.block.Wandable;
import vazkii.botania.api.block_entity.SpecialFlowerBlockEntity;
import vazkii.botania.api.corporea.CorporeaRequestMatcher;
import vazkii.botania.api.corporea.CorporeaSpark;
import vazkii.botania.api.item.AvatarWieldable;
import vazkii.botania.api.item.BlockProvider;
import vazkii.botania.api.item.CoordBoundItem;
import vazkii.botania.api.item.Relic;
import vazkii.botania.api.mana.*;
import vazkii.botania.api.mana.spark.SparkAttachable;
import vazkii.botania.common.block.block_entity.red_string.RedStringContainerBlockEntity;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.internal_caps.*;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface XplatAbstractions {
	// FML/Fabric Loader
	default boolean gogLoaded() {
		return isModLoaded(BotaniaAPI.GOG_MODID);
	}

	// Yes, this forms a loop by default. Each loader overrides their own to break the loop
	default boolean isFabric() {
		return !isForge();
	}

	default boolean isForge() {
		return !isFabric();
	}

	boolean isModLoaded(String modId);
	boolean isDevEnvironment();
	boolean isDataGen();
	boolean isPhysicalClient();
	String getBotaniaVersion();

	// Capability access (API-facing caps)
	@Nullable
	AvatarWieldable findAvatarWieldable(ItemStack stack);
	@Nullable
	BlockProvider findBlockProvider(ItemStack stack);
	@Nullable
	CoordBoundItem findCoordBoundItem(ItemStack stack);
	@Nullable
	ManaItem findManaItem(ItemStack stack);
	@Nullable
	Relic findRelic(ItemStack stack);
	@Nullable
	ExoflameHeatable findExoflameHeatable(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be);
	@Nullable
	HornHarvestable findHornHarvestable(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be);
	@Nullable
	HourglassTrigger findHourglassTrigger(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be);
	@Nullable
	ManaCollisionGhost findManaGhost(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be);

	@Nullable
	default ManaReceiver findManaReceiver(Level level, BlockPos pos, @Nullable Direction direction) {
		return findManaReceiver(level, pos, level.getBlockState(pos), level.getBlockEntity(pos), direction);
	}

	@Nullable
	ManaReceiver findManaReceiver(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be, @Nullable Direction direction);

	@Nullable
	SparkAttachable findSparkAttachable(Level level, BlockPos pos, BlockState blockState, @Nullable BlockEntity be, Direction direction);

	@Nullable
	ManaTrigger findManaTrigger(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be);
	@Nullable
	Wandable findWandable(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be);
	boolean isFluidContainer(ItemEntity item);
	boolean extractFluidFromItemEntity(ItemEntity item, Fluid fluid);
	boolean extractFluidFromPlayerItem(Player player, InteractionHand hand, Fluid fluid);
	boolean insertFluidIntoPlayerItem(Player player, InteractionHand hand, Fluid fluid);
	boolean hasInventory(Level level, BlockPos pos, Direction sideOfPos);
	ItemStack insertToInventory(Level level, BlockPos pos, Direction sideOfPos, ItemStack toInsert, boolean simulate);

	// Capability access (internal caps)
	EthicalComponent ethicalComponent(PrimedTnt tnt);
	SpectralRailComponent ghostRailComponent(AbstractMinecart cart);
	ItemFlagsComponent itemFlagsComponent(ItemEntity item);
	KeptItemsComponent keptItemsComponent(Player player, boolean reviveCaps);
	@Nullable
	LooniumComponent looniumComponent(LivingEntity entity);
	NarslimmusComponent narslimmusComponent(Slime slime);
	TigerseyeComponent tigersEyeComponent(Creeper creeper);

	// Events
	boolean fireCorporeaRequestEvent(CorporeaRequestMatcher matcher, int itemCount, CorporeaSpark spark, boolean dryRun);
	boolean fireCorporeaIndexRequestEvent(ServerPlayer player, CorporeaRequestMatcher request, int count, CorporeaSpark spark);
	void fireManaItemEvent(Player player, List<ItemStack> toReturn);
	float fireManaDiscountEvent(Player player, float discount, ItemStack tool);
	boolean fireManaProficiencyEvent(Player player, ItemStack tool, boolean proficient);
	void fireElvenPortalUpdateEvent(BlockEntity portal, AABB bounds, boolean open, List<ItemStack> stacksInside);
	void fireManaNetworkEvent(ManaReceiver thing, ManaBlockType type, ManaNetworkAction action);

	// Networking
	Packet<ClientGamePacketListener> toVanillaClientboundPacket(CustomPacketPayload packet);
	void sendToPlayer(Player player, CustomPacketPayload packet);
	void sendToNear(Level level, BlockPos pos, CustomPacketPayload packet);
	void sendToTracking(Entity e, CustomPacketPayload packet);

	// Registrations
	<T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> func, Block... blocks);
	void registerReloadListener(PackType type, ResourceLocation id, PreparableReloadListener listener);
	Item.Properties defaultItemBuilder();

	default Item.Properties defaultItemBuilderWithCustomDamageOnFabric() {
		return defaultItemBuilder();
	}

	/**
	 * Forge allows items to opt out of craft-repairing using the builder.
	 * Fabric we handle it manually in RepairItemRecipeFabricMixin
	 */
	default Item.Properties noRepairOnForge(Item.Properties builder) {
		return builder;
	}

	<T extends AbstractContainerMenu, D> MenuType<T> createMenuType(TriFunction<Integer, Inventory, D, T> constructor, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec);
	@Nullable
	EquipmentHandler tryCreateEquipmentHandler();

	// Misc
	<D> void openMenu(ServerPlayer player, MenuProvider menu, D initialData, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec);

	TagKey<Block> getOreTag();
	boolean isInGlassTag(BlockState state);
	// Forge patches AbstractFurnaceBlockEntity.canBurn to be an instance method, so we gotta abstract it
	boolean canFurnaceBurn(AbstractFurnaceBlockEntity furnace, @Nullable RecipeHolder<?> recipeHolder, NonNullList<ItemStack> items, int maxStackSize);
	// Forge patches BucketItem to use a supplier for the fluid, and exposes it, while Fabric needs an accessor
	Fluid getBucketFluid(BucketItem item);
	int getSmeltingBurnTime(ItemStack stack);
	boolean preventsRemoteMovement(ItemEntity entity);
	void addAxeStripping(Block input, Block output);
	int transferEnergyToNeighbors(Level level, BlockPos pos, int energy);

	// Red string container
	boolean isRedStringContainerTarget(BlockEntity be);
	RedStringContainerBlockEntity newRedStringContainer(BlockPos pos, BlockState state);

	default BlockSetType registerWoodBlockSetType(String name) {
		return registerBlockSetType(name, true, SoundType.WOOD, SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundEvents.WOODEN_BUTTON_CLICK_ON);
	}

	BlockSetType registerBlockSetType(String name, boolean canOpenByHand, SoundType soundType, SoundEvent doorClose, SoundEvent doorOpen, SoundEvent trapdoorClose, SoundEvent trapdoorOpen, SoundEvent pressurePlateClickOff, SoundEvent pressurePlateClickOn, SoundEvent buttonClickOff, SoundEvent buttonClickOn);

	default WoodType registerWoodType(String name, BlockSetType blockSetType) {
		return registerWoodType(name, blockSetType, SoundType.WOOD, SoundType.HANGING_SIGN, SoundEvents.FENCE_GATE_CLOSE, SoundEvents.FENCE_GATE_OPEN);
	}

	WoodType registerWoodType(String name, BlockSetType setType, SoundType soundType, SoundType hangingSignSoundType, SoundEvent fenceGateClose, SoundEvent fenceGateOpen);

	XplatAbstractions INSTANCE = ServiceUtil.findService(XplatAbstractions.class, null);
}
