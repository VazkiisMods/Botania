/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity;

import com.google.common.base.Predicates;
import com.google.common.base.Suppliers;

import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.block.WandHUD;
import vazkii.botania.api.block.Wandable;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.ManaPool;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.api.mana.spark.ManaSpark;
import vazkii.botania.api.mana.spark.SparkAttachable;
import vazkii.botania.api.mana.spark.SparkHelper;
import vazkii.botania.api.state.BotaniaStateProperties;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.network.EffectType;
import vazkii.botania.network.clientbound.BotaniaEffectPacket;
import vazkii.botania.xplat.XplatAbstractions;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ManaEnchanterBlockEntity extends BotaniaBlockEntity implements ManaReceiver, SparkAttachable, Wandable, Clearable {
	private static final String TAG_STAGE = "stage";
	private static final String TAG_STAGE_TICKS = "stageTicks";
	private static final String TAG_STAGE_3_END_TICKS = "stage3EndTicks";
	private static final String TAG_MANA_REQUIRED = "manaRequired";
	private static final String TAG_MANA = "mana";
	private static final String TAG_ITEM = "item";
	private static final String TAG_ENCHANTS = "enchantsToApply";
	private static final int CRAFT_EFFECT_EVENT = 0;
	private static final int IDLE_CHECK_INTERVAL_TICKS = 10;

	private static final String[][] PATTERN = new String[][] {
			{
					"_P_______P_",
					"___________",
					"___________",
					"P_________P",
					"___________",
					"___________",
					"_P_______P_",
			},
			{
					"_F_______F_",
					"___________",
					"____F_F____",
					"F____L____F",
					"____F_F____",
					"___________",
					"_F_______F_",
			},
			{
					"___________",
					"____BBB____",
					"___B_B_B___",
					"___BB0BB___",
					"___B_B_B___",
					"____BBB____",
					"___________",
			}
	};

	private static final Supplier<IStateMatcher> OBSIDIAN_MATCHER = Suppliers.memoize(() -> PatchouliAPI.get().predicateMatcher(
			Blocks.OBSIDIAN,
			state -> state.is(Blocks.OBSIDIAN) || state.is(Blocks.CRYING_OBSIDIAN)
	));

	public static final Supplier<IMultiblock> MULTIBLOCK = Suppliers.memoize(() -> PatchouliAPI.get().makeMultiblock(
			PATTERN,
			'P', BotaniaBlocks.manaPylon,
			'L', Blocks.LAPIS_BLOCK,
			'B', OBSIDIAN_MATCHER.get(),
			'0', OBSIDIAN_MATCHER.get(),
			'F', PatchouliAPI.get().tagMatcher(BotaniaTags.Blocks.ENCHANTER_FLOWERS)
	));

	private static final Supplier<IMultiblock> FORMED_MULTIBLOCK = Suppliers.memoize(() -> PatchouliAPI.get().makeMultiblock(
			PATTERN,
			'P', BotaniaBlocks.manaPylon,
			'L', BotaniaBlocks.enchanter,
			'B', OBSIDIAN_MATCHER.get(),
			'0', OBSIDIAN_MATCHER.get(),
			'F', PatchouliAPI.get().predicateMatcher(BotaniaBlocks.whiteFlower, state -> state.is(BotaniaTags.Blocks.ENCHANTER_FLOWERS))
	));

	public State stage = State.IDLE;
	public int stageTicks = 0;

	public int stage3EndTicks = 0;

	private int idleTicks = 0;

	private int manaRequired = -1;
	private int mana = 0;

	public ItemStack itemToEnchant = ItemStack.EMPTY;
	private final List<EnchantmentInstance> enchants = new ArrayList<>();

	private static final Map<Direction.Axis, BlockPos[]> PYLON_LOCATIONS = new EnumMap<>(Direction.Axis.class);

	static {
		PYLON_LOCATIONS.put(Direction.Axis.X, new BlockPos[] { new BlockPos(-5, 1, 0), new BlockPos(5, 1, 0), new BlockPos(-4, 1, 3), new BlockPos(4, 1, 3), new BlockPos(-4, 1, -3), new BlockPos(4, 1, -3) });
		PYLON_LOCATIONS.put(Direction.Axis.Z, new BlockPos[] { new BlockPos(0, 1, -5), new BlockPos(0, 1, 5), new BlockPos(3, 1, -4), new BlockPos(3, 1, 4), new BlockPos(-3, 1, -4), new BlockPos(-3, 1, 4) });
	}

	public ManaEnchanterBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.ENCHANTER, pos, state);
	}

	@Override
	public boolean onUsedByWand(@Nullable Player player, ItemStack wand, Direction side) {
		if (stage != State.IDLE || itemToEnchant.isEmpty() || !itemToEnchant.isEnchantable()) {
			return false;
		}

		List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition.getX() - 2, worldPosition.getY(), worldPosition.getZ() - 2, worldPosition.getX() + 3, worldPosition.getY() + 1, worldPosition.getZ() + 3));
		int count = items.size();

		if (count > 0 && !level.isClientSide) {
			for (ItemEntity entity : items) {
				ItemStack item = entity.getItem();
				if (item.is(Items.ENCHANTED_BOOK)) {
					Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(item);
					if (enchants.size() > 0) {
						Enchantment enchant = enchants.keySet().iterator().next();
						if (isEnchantmentValid(enchant)) {
							advanceStage();
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private void gatherEnchants() {
		if (!level.isClientSide && stageTicks % 20 == 0) {
			List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition.getX() - 2, worldPosition.getY(), worldPosition.getZ() - 2, worldPosition.getX() + 3, worldPosition.getY() + 1, worldPosition.getZ() + 3));
			boolean addedEnch = false;

			for (ItemEntity entity : items) {
				ItemStack item = entity.getItem();
				if (item.is(Items.ENCHANTED_BOOK)) {
					Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(item);
					if (enchants.size() > 0) {
						Map.Entry<Enchantment, Integer> e = enchants.entrySet().iterator().next();
						Enchantment ench = e.getKey();
						int enchantLvl = e.getValue();
						if (!hasEnchantAlready(ench) && isEnchantmentValid(ench)) {
							this.enchants.add(new EnchantmentInstance(ench, enchantLvl));
							level.playSound(null, worldPosition, BotaniaSounds.ding, SoundSource.BLOCKS, 1F, 1F);
							addedEnch = true;
							break;
						}
					}
				}
			}

			if (!addedEnch) {
				if (enchants.isEmpty()) {
					stage = State.IDLE;
				} else {
					advanceStage();
				}
			}
		}
	}

	private void gatherMana(Direction.Axis axis) {
		if (manaRequired == -1) {
			manaRequired = 0;
			for (EnchantmentInstance data : enchants) {
				manaRequired += (int) (5000F * ((15 - Math.min(15, data.enchantment.getRarity().getWeight()))
						* 1.05F)
						* ((3F + data.level * data.level)
								* 0.25F)
						* (0.9F + enchants.size() * 0.05F)
						* (data.enchantment.isTreasureOnly() ? 1.25F : 1F));
			}
		} else if (mana >= manaRequired) {
			manaRequired = 0;
			for (BlockPos offset : PYLON_LOCATIONS.get(axis)) {
				BlockEntity te = level.getBlockEntity(worldPosition.offset(offset));
				if (te instanceof PylonBlockEntity pylon) {
					pylon.activated = false;
				}
			}

			advanceStage();
		} else {
			ManaSpark spark = getAttachedSpark();
			if (spark != null) {
				var otherSparks = SparkHelper.getSparksAround(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, spark.getNetwork());
				for (var otherSpark : otherSparks) {
					if (spark != otherSpark && otherSpark.getAttachedManaReceiver() instanceof ManaPool) {
						otherSpark.registerTransfer(spark);
					}
				}
			}
			if (stageTicks % 5 == 0) {
				sync();
			}
		}
	}

	public static void commonTick(Level level, BlockPos worldPosition, BlockState state, ManaEnchanterBlockEntity self) {
		Direction.Axis axis = state.getValue(BotaniaStateProperties.ENCHANTER_DIRECTION);

		for (BlockPos offset : PYLON_LOCATIONS.get(axis)) {
			BlockEntity tile = level.getBlockEntity(worldPosition.offset(offset));
			if (tile instanceof PylonBlockEntity pylon) {
				boolean gatheringMana = self.stage == State.GATHER_MANA;
				pylon.activated = gatheringMana;
				if (gatheringMana) {
					pylon.centerPos = worldPosition;
				}
			}
		}

		if (self.stage != State.IDLE) {
			self.stageTicks++;
		} else {
			self.idleTicks++;
		}

		if (level.isClientSide || self.stage == State.IDLE && self.idleTicks % IDLE_CHECK_INTERVAL_TICKS != 0) {
			return;
		}

		Rotation rot = getAxisRotation(axis);
		if (!FORMED_MULTIBLOCK.get().validate(level, worldPosition.below(), rot)) {
			level.setBlockAndUpdate(worldPosition, Blocks.LAPIS_BLOCK.defaultBlockState());
			XplatAbstractions.INSTANCE.sendToNear(level, worldPosition, new BotaniaEffectPacket(EffectType.ENCHANTER_DESTROY,
					worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5));
			level.playSound(null, worldPosition, BotaniaSounds.enchanterFade, SoundSource.BLOCKS, 1F, 1F);
			return;
		}

		switch (self.stage) {
			case GATHER_ENCHANTS -> self.gatherEnchants();
			case GATHER_MANA -> self.gatherMana(axis);
			case DO_ENCHANT -> { // Enchant
				if (self.stageTicks >= 100) {
					for (EnchantmentInstance data : self.enchants) {
						if (EnchantmentHelper.getItemEnchantmentLevel(data.enchantment, self.itemToEnchant) == 0) {
							self.itemToEnchant.enchant(data.enchantment, data.level);
						}
					}

					self.enchants.clear();
					self.manaRequired = -1;
					self.mana = 0;

					level.blockEvent(worldPosition, BotaniaBlocks.enchanter, CRAFT_EFFECT_EVENT, 0);
					self.advanceStage();
				}
			}
			case RESET -> { // Reset
				if (self.stageTicks >= 20) {
					self.advanceStage();
				}

			}
			default -> {}
		}
	}

	private void advanceStage() {
		switch (stage) {
			case IDLE -> stage = State.GATHER_ENCHANTS;
			case GATHER_ENCHANTS -> stage = State.GATHER_MANA;
			case GATHER_MANA -> stage = State.DO_ENCHANT;
			case DO_ENCHANT -> {
				stage = State.RESET;
				stage3EndTicks = stageTicks;
			}
			case RESET -> {
				stage = State.IDLE;
				stage3EndTicks = 0;
			}
		}

		stageTicks = 0;
		sync();
	}

	@Override
	public boolean triggerEvent(int event, int param) {
		switch (event) {
			case CRAFT_EFFECT_EVENT: {
				if (level.isClientSide) {
					for (int i = 0; i < 25; i++) {
						float red = (float) Math.random();
						float green = (float) Math.random();
						float blue = (float) Math.random();
						SparkleParticleData data = SparkleParticleData.sparkle((float) Math.random(), red, green, blue, 10);
						level.addParticle(data, getBlockPos().getX() + Math.random() * 0.4 - 0.2, getBlockPos().getY(), getBlockPos().getZ() + Math.random() * 0.4 - 0.2, 0, 0, 0);
					}
					level.playLocalSound(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), BotaniaSounds.enchanterEnchant, SoundSource.BLOCKS, 1F, 1F, false);
				}
				return true;
			}
			default:
				return super.triggerEvent(event, param);
		}
	}

	@Override
	public Level getManaReceiverLevel() {
		return getLevel();
	}

	@Override
	public BlockPos getManaReceiverPos() {
		return getBlockPos();
	}

	@Override
	public int getCurrentMana() {
		return mana;
	}

	@Override
	public boolean isFull() {
		return mana >= manaRequired;
	}

	@Override
	public void receiveMana(int mana) {
		this.mana = Math.min(manaRequired, this.mana + mana);
	}

	@Override
	public boolean canReceiveManaFromBursts() {
		return manaRequired > 0;
	}

	public void sync() {
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}

	@Override
	public void writePacketNBT(CompoundTag cmp) {
		cmp.putInt(TAG_MANA, mana);
		cmp.putInt(TAG_MANA_REQUIRED, manaRequired);
		cmp.putInt(TAG_STAGE, stage.ordinal());
		cmp.putInt(TAG_STAGE_TICKS, stageTicks);
		cmp.putInt(TAG_STAGE_3_END_TICKS, stage3EndTicks);

		CompoundTag itemCmp = new CompoundTag();
		if (!itemToEnchant.isEmpty()) {
			cmp.put(TAG_ITEM, itemToEnchant.save(itemCmp));
		}

		String enchStr = enchants.stream()
				.map(e -> BuiltInRegistries.ENCHANTMENT.getKey(e.enchantment) + "=" + e.level)
				.collect(Collectors.joining(","));
		cmp.putString(TAG_ENCHANTS, enchStr);
	}

	@Override
	public void readPacketNBT(CompoundTag cmp) {
		mana = cmp.getInt(TAG_MANA);
		manaRequired = cmp.getInt(TAG_MANA_REQUIRED);
		stage = State.values()[cmp.getInt(TAG_STAGE)];
		stageTicks = cmp.getInt(TAG_STAGE_TICKS);
		stage3EndTicks = cmp.getInt(TAG_STAGE_3_END_TICKS);

		CompoundTag itemCmp = cmp.getCompound(TAG_ITEM);
		itemToEnchant = ItemStack.of(itemCmp);

		enchants.clear();
		String enchStr = cmp.getString(TAG_ENCHANTS);
		if (!enchStr.isEmpty()) {
			String[] enchTokens = enchStr.split(",");
			for (String token : enchTokens) {
				try {
					String[] entryTokens = token.split("=");
					int lvl = Integer.parseInt(entryTokens[1]);
					level.holderLookup(Registries.ENCHANTMENT)
							.get(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse(entryTokens[0])))
							.ifPresent(ench -> enchants.add(new EnchantmentInstance(ench.value(), lvl)));
				} catch (ResourceLocationException ignored) {}
			}
		}
	}

	private boolean hasEnchantAlready(Enchantment enchant) {
		for (EnchantmentInstance data : enchants) {
			if (data.enchantment == enchant) {
				return true;
			}
		}

		return false;
	}

	private boolean isEnchantmentValid(@Nullable Enchantment ench) {
		if (ench == null || !ench.canEnchant(itemToEnchant)) {
			return false;
		}

		for (EnchantmentInstance data : enchants) {
			Enchantment otherEnch = data.enchantment;
			if (!ench.isCompatibleWith(otherEnch)) {
				return false;
			}
		}

		return true;
	}

	@Nullable
	public static Direction.Axis canEnchanterExist(Level world, BlockPos pos) {
		Rotation rot = MULTIBLOCK.get().validate(world, pos.below());
		if (rot == null) {
			return null;
		}

		return switch (rot) {
			case NONE, CLOCKWISE_180 -> Direction.Axis.Z;
			case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> Direction.Axis.X;
		};
	}

	private static Rotation getAxisRotation(Direction.Axis axis) {
		return switch (axis) {
			case Z -> Rotation.NONE;
			case X -> Rotation.CLOCKWISE_90;
			default -> throw new IllegalStateException("Enchanter should only ever be facing in X or Z direction");
		};
	}

	@Override
	public boolean canAttachSpark(ItemStack stack) {
		return true;
	}

	@Override
	public ManaSpark getAttachedSpark() {
		List<Entity> sparks = level.getEntitiesOfClass(Entity.class, new AABB(worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 2, worldPosition.getZ() + 1), Predicates.instanceOf(ManaSpark.class));
		if (sparks.size() == 1) {
			Entity e = sparks.get(0);
			return (ManaSpark) e;
		}

		return null;
	}

	@Override
	public boolean areIncomingTranfersDone() {
		return stage == State.DO_ENCHANT;
	}

	@Override
	public int getAvailableSpaceForMana() {
		return Math.max(0, manaRequired - getCurrentMana());
	}

	@Override
	public void clearContent() {
		this.itemToEnchant = ItemStack.EMPTY;
		this.stage = State.IDLE;
	}

	public static class WandHud implements WandHUD {
		private final ManaEnchanterBlockEntity enchanter;

		public WandHud(ManaEnchanterBlockEntity enchanter) {
			this.enchanter = enchanter;
		}

		@Override
		public void renderHUD(GuiGraphics gui, Minecraft mc) {
			if (enchanter.manaRequired > 0 && !enchanter.itemToEnchant.isEmpty()) {
				int x = mc.getWindow().getGuiScaledWidth() / 2 + 8;
				int y = mc.getWindow().getGuiScaledHeight() / 2 - 12;

				RenderHelper.renderHUDBox(gui, x, y, x + 24, y + 24);
				RenderHelper.renderProgressPie(gui, x + 4, y + 4, (float) enchanter.mana / (float) enchanter.manaRequired,
						enchanter.itemToEnchant);
			}
		}
	}

	public enum State {
		IDLE,
		GATHER_ENCHANTS,
		GATHER_MANA,
		DO_ENCHANT,
		RESET
	}

}
