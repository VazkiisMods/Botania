package vazkii.botania.common.component;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.common.lib.LibComponentNames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class BotaniaDataComponents {
	private static final Map<String, DataComponentType<?>> ALL = new HashMap<>();

	// general flags
	/**
	 * General persisted "active" flag for items that can be toggled and need to remember their state later.
	 * (examples: stone of temperance, manufactory halo's auto-crafting, terra shatterer)
	 */
	public static final DataComponentType<Unit> ACTIVE = makeUnit(LibComponentNames.ACTIVE);
	/**
	 * Transient "active" flag for items with an active state that does not need to be persisted.
	 * (examples: slime in a bottle being happy, assembly halo being held, bauble box being open)
	 */
	public static final DataComponentType<Unit> ACTIVE_TRANSIENT = makeTransientUnit(LibComponentNames.ACTIVE_TRANSIENT);

	public static final DataComponentType<Unit> PHANTOM_INKED = makeUnit(LibComponentNames.PHANTOM_INKED);
	public static final DataComponentType<Unit> RESOLUTE_IVY = makeUnit(LibComponentNames.RESOLUTE_IVY);

	// various specific flags
	public static final DataComponentType<Unit> ELEMENTIUM_TIPPED = makeUnit(LibComponentNames.ELEMENTIUM_TIPPED);
	public static final DataComponentType<Unit> ELVEN_UNLOCK = makeUnit(LibComponentNames.ELVEN_UNLOCK);
	/**
	 * Helper component that causes mana pool items to be rendered with mana content.
	 */
	public static final DataComponentType<Unit> RENDER_FULL = makeTransientUnit(LibComponentNames.RENDER_FULL);

	// wand properties
	public static final DataComponentType<DyeColor> WAND_COLOR1 = make(LibComponentNames.WAND_COLOR1,
			builder -> builder.persistent(DyeColor.CODEC).networkSynchronized(DyeColor.STREAM_CODEC));
	public static final DataComponentType<DyeColor> WAND_COLOR2 = make(LibComponentNames.WAND_COLOR2,
			builder -> builder.persistent(DyeColor.CODEC).networkSynchronized(DyeColor.STREAM_CODEC));
	public static final DataComponentType<Unit> WAND_BIND_MODE = makeUnit(LibComponentNames.WAND_BIND_MODE);
	public static final DataComponentType<GlobalPos> BINDING_POS = make(LibComponentNames.BINDING_POS,
			// TODO: does this need to be persisted?
			builder -> builder.persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC));

	// mana item properties
	/**
	 * Current amount of mana in the item.
	 */
	public static final DataComponentType<Integer> MANA = make(LibComponentNames.MANA,
			builder -> builder.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	/**
	 * Maximum amount of mana in the item. Not fixed and may become zero if the item can temporarily not store any mana.
	 */
	public static final DataComponentType<Integer> MAX_MANA = make(LibComponentNames.MAX_MANA,
			builder -> builder.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	/**
	 * Mana backlog to be processed later through some other means.
	 * If this component exists, it receives the same delta amounts as the {@link #MANA} component.
	 */
	public static final DataComponentType<Integer> MANA_BACKLOG = make(LibComponentNames.MANA_BACKLOG,
			builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));
	/**
	 * Defines that the item can receive mana while in the player's inventory,
	 * e.g. from mana producers or nearby dispersive sparks.
	 */
	public static final DataComponentType<Unit> CAN_RECEIVE_MANA_FROM_ITEM = makeUnit(LibComponentNames.CAN_RECEIVE_MANA_FROM_ITEM);
	/**
	 * Defines that the item can be filled in a mana pool.
	 */
	public static final DataComponentType<Unit> CAN_RECEIVE_MANA_FROM_POOL = makeUnit(LibComponentNames.CAN_RECEIVE_MANA_FROM_POOL);
	/**
	 * Defines that the item can provide mana to mana-consuming items in the player's inventory.
	 */
	public static final DataComponentType<Unit> CAN_EXPORT_MANA_TO_ITEM = makeUnit(LibComponentNames.CAN_EXPORT_MANA_TO_ITEM);
	/**
	 * Defines that the item can be drained into a mana pool.
	 */
	public static final DataComponentType<Unit> CAN_EXPORT_MANA_TO_POOL = makeUnit(LibComponentNames.CAN_EXPORT_MANA_TO_POOL);
	/**
	 * Defined that the item can provide infinite mana without draining itself.
	 */
	public static final DataComponentType<Unit> CREATIVE_MANA = makeUnit(LibComponentNames.CREATIVE_MANA);
	/**
	 * Bound pool of a mana mirror. (Not part of the default mana item implementation.)
	 */
	public static final DataComponentType<GlobalPos> MANA_POOL_POS = make(LibComponentNames.MANA_POOL_POS,
			builder -> builder.persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC));

	public static final DataComponentType<Integer> RANGE = make(LibComponentNames.RANGE,
			builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));

	// crafting halo data
	public static final DataComponentType<Float> HALO_ROTATION_BASE = make(LibComponentNames.HALO_ROTATION_BASE,
			builder -> builder.networkSynchronized(ByteBufCodecs.FLOAT));
	public static final DataComponentType<ResourceLocation> LAST_RECIPE_ID = make(LibComponentNames.LAST_RECIPE_ID,
			builder -> builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC)
	);
	public static final DataComponentType<HaloRecipeStorageComponent> STORED_RECIPES = make(LibComponentNames.STORED_RECIPES,
			builder -> builder.persistent(HaloRecipeStorageComponent.CODEC)
					.networkSynchronized(HaloRecipeStorageComponent.STREAM_CODEC).cacheEncoding());

	public static final DataComponentType<Long> LAST_TRIGGER_TIME = make(LibComponentNames.LAST_TRIGGER_TIME,
			builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));

	/**
	 * Defines the dyed lens color value. Should not be used together with {@link #LENS_RAINBOW_TINT}.
	 */
	public static final DataComponentType<DyeColor> LENS_TINT = make(LibComponentNames.LENS_TINT,
			builder -> builder.persistent(DyeColor.CODEC).networkSynchronized(DyeColor.STREAM_CODEC));
	/**
	 * Defines that the lens is rainbow-tinted. Takes precedence over {@link #LENS_TINT}.
	 */
	public static final DataComponentType<Unit> LENS_RAINBOW_TINT = makeUnit(LibComponentNames.LENS_RAINBOW_TINT);
	public static final DataComponentType<ItemStack> ATTACHED_LENS = make(LibComponentNames.ATTACHED_LENS,
			builder -> builder.persistent(ItemStack.CODEC).networkSynchronized(ItemStack.STREAM_CODEC).cacheEncoding());

	// brews and similar consumables
	public static final DataComponentType<ResourceLocation> BREW = make(LibComponentNames.BREW,
			builder -> builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC));
	public static final DataComponentType<Integer> MAX_USES = make(LibComponentNames.MAX_USES,
			builder -> builder.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
	public static final DataComponentType<Integer> REMAINING_USES = make(LibComponentNames.REMAINING_USES,
			builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
	public static final DataComponentType<Long> RANDOM_SEED = make(LibComponentNames.RANDOM_SEED,
			builder -> builder.persistent(Codec.LONG).networkSynchronized(new StreamCodec<>() {
				// There doesn't seem to be a built-in non-var stream codec for Long values.
				@Override
				public Long decode(RegistryFriendlyByteBuf buffer) {
					return buffer.readLong();
				}

				@Override
				public void encode(RegistryFriendlyByteBuf buffer, Long value) {
					buffer.writeLong(value);
				}
			}));

	public static final DataComponentType<List<Integer>> SPECTATOR_HIGHLIGHT_ENTITIES = make(LibComponentNames.SPECTATOR_HIGHLIGHT_ENTITIES,
			builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list())));
	public static final DataComponentType<List<BlockPos>> SPECTATOR_HIGHLIGHT_BLOCKS = make(LibComponentNames.SPECTATOR_HIGHLIGHT_BLOCKS,
			builder -> builder.networkSynchronized(BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list())));

	// black hole talisman
	public static final DataComponentType<ResourceLocation> BLOCK_TYPE = make(LibComponentNames.BLOCK_TYPE,
			builder -> builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC));
	public static final DataComponentType<Integer> BLOCK_COUNT = make(LibComponentNames.BLOCK_COUNT,
			builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

	public static void registerComponents(BiConsumer<DataComponentType<?>, ResourceLocation> biConsumer) {
		for (Map.Entry<String, DataComponentType<?>> entry : ALL.entrySet()) {
			biConsumer.accept(entry.getValue(), botaniaRL(entry.getKey()));
		}
	}

	private static <T> DataComponentType<T> make(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
		if (!name.matches("[a-z]+(?:_[a-z0-9]+)*")) {
			throw new IllegalArgumentException("Typo? Name should be in snake_case: " + name);
		}
		DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
		var old = ALL.put(name, type);
		if (old != null) {
			throw new IllegalArgumentException("Typo? Duplicate name " + name);
		}
		return type;
	}

	private static DataComponentType<Unit> makeUnit(String name) {
		return make(name, builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));
	}

	private static DataComponentType<Unit> makeTransientUnit(String name) {
		return make(name, builder -> builder.networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));
	}

}
