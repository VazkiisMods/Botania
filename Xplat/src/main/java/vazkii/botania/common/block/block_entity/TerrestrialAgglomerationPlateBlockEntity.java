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

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.ManaPool;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.api.mana.spark.ManaSpark;
import vazkii.botania.api.mana.spark.SparkAttachable;
import vazkii.botania.api.mana.spark.SparkHelper;
import vazkii.botania.api.recipe.TerrestrialAgglomerationRecipe;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.network.EffectType;
import vazkii.botania.network.clientbound.BotaniaEffectPacket;
import vazkii.botania.xplat.XplatAbstractions;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TerrestrialAgglomerationPlateBlockEntity extends BotaniaBlockEntity implements SparkAttachable, ManaReceiver {
	public static final Supplier<IMultiblock> MULTIBLOCK = Suppliers.memoize(() -> PatchouliAPI.get().makeMultiblock(
			new String[][] {
					{
							"___",
							"_P_",
							"___"
					},
					{
							"RLR",
							"L0L",
							"RLR"
					}
			},
			'P', BotaniaBlocks.terraPlate,
			'R', PatchouliAPI.get().tagMatcher(BotaniaTags.Blocks.TERRA_PLATE_BASE),
			'0', PatchouliAPI.get().tagMatcher(BotaniaTags.Blocks.TERRA_PLATE_BASE),
			'L', PatchouliAPI.get().tagMatcher(
					XplatAbstractions.INSTANCE.isFabric()
							? TagKey.create(Registries.BLOCK, new ResourceLocation("c", "lapis_blocks"))
							: TagKey.create(Registries.BLOCK, new ResourceLocation("forge", "storage_blocks/lapis")))
	));

	private static final String TAG_MANA = "mana";

	private int mana;

	public TerrestrialAgglomerationPlateBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.TERRA_PLATE, pos, state);
	}

	public static void serverTick(Level level, BlockPos worldPosition, BlockState state, TerrestrialAgglomerationPlateBlockEntity self) {
		boolean removeMana = true;

		if (self.hasValidPlatform()) {
			List<ItemStack> items = self.getItems();
			SimpleContainer inv = self.getInventory();

			TerrestrialAgglomerationRecipe recipe = self.getCurrentRecipe(inv);
			if (recipe != null) {
				removeMana = false;
				ManaSpark spark = self.getAttachedSpark();
				if (spark != null) {
					var otherSparks = SparkHelper.getSparksAround(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, spark.getNetwork());
					for (var otherSpark : otherSparks) {
						if (spark != otherSpark && otherSpark.getAttachedManaReceiver() instanceof ManaPool) {
							otherSpark.registerTransfer(spark);
						}
					}
				}
				if (self.mana > 0) {
					VanillaPacketDispatcher.dispatchTEToNearbyPlayers(self);
					int proportion = Float.floatToIntBits(self.getCompletion());
					XplatAbstractions.INSTANCE.sendToNear(level, worldPosition,
							new BotaniaEffectPacket(EffectType.TERRA_PLATE, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), proportion));
				}

				if (self.mana >= recipe.getMana()) {
					ItemStack result = recipe.assemble(inv, level.registryAccess());
					for (ItemStack item : items) {
						item.setCount(0);
					}
					ItemEntity item = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.2, worldPosition.getZ() + 0.5, result);
					item.setDeltaMovement(Vec3.ZERO);
					level.addFreshEntity(item);
					level.playSound(null, item.getX(), item.getY(), item.getZ(), BotaniaSounds.terrasteelCraft, SoundSource.BLOCKS, 1F, 1F);
					self.mana = 0;
					level.updateNeighbourForOutputSignal(worldPosition, state.getBlock());
					VanillaPacketDispatcher.dispatchTEToNearbyPlayers(self);
				}
			}
		}

		if (removeMana) {
			self.receiveMana(-1000);
		}
	}

	private List<ItemStack> getItems() {
		List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition), EntitySelector.ENTITY_STILL_ALIVE);
		List<ItemStack> stacks = new ArrayList<>();
		for (ItemEntity entity : itemEntities) {
			if (!entity.getItem().isEmpty()) {
				stacks.add(entity.getItem());
			}
		}
		return stacks;
	}

	private SimpleContainer getInventory() {
		List<ItemStack> items = getItems();
		return new SimpleContainer(flattenStacks(items));
	}

	/**
	 * Flattens the list of stacks into an array of stacks with size 1,
	 * for recipe matching purposes only.
	 * If the total count of items exceeds 64, returns no items.
	 */
	private static ItemStack[] flattenStacks(List<ItemStack> items) {
		ItemStack[] stacks;
		int i = 0;
		for (ItemStack item : items) {
			i += item.getCount();
		}
		if (i > 64) {
			return new ItemStack[0];
		}

		stacks = new ItemStack[i];
		int j = 0;
		for (ItemStack item : items) {
			if (item.getCount() > 1) {
				ItemStack temp = item.copyWithCount(1);
				for (int count = 0; count < item.getCount(); count++) {
					stacks[j] = temp.copy();
					j++;
				}
			} else {
				stacks[j] = item;
				j++;
			}
		}
		return stacks;
	}

	@Nullable
	private TerrestrialAgglomerationRecipe getCurrentRecipe(SimpleContainer items) {
		if (items.isEmpty()) {
			return null;
		}
		return level.getRecipeManager().getRecipeFor(BotaniaRecipeTypes.TERRA_PLATE_TYPE, items, level).orElse(null);
	}

	private boolean isActive() {
		return getCurrentRecipe(getInventory()) != null;
	}

	private boolean hasValidPlatform() {
		return MULTIBLOCK.get().validate(level, getBlockPos().below()) != null;
	}

	@Override
	public void writePacketNBT(CompoundTag cmp) {
		cmp.putInt(TAG_MANA, mana);
	}

	@Override
	public void readPacketNBT(CompoundTag cmp) {
		mana = cmp.getInt(TAG_MANA);
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
		TerrestrialAgglomerationRecipe recipe = getCurrentRecipe(getInventory());
		return recipe == null || getCurrentMana() >= recipe.getMana();
	}

	@Override
	public void receiveMana(int mana) {
		this.mana = Math.max(0, this.mana + mana);
		level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
	}

	@Override
	public boolean canReceiveManaFromBursts() {
		return isActive();
	}

	@Override
	public boolean canAttachSpark(ItemStack stack) {
		return true;
	}

	@Override
	public ManaSpark getAttachedSpark() {
		List<Entity> sparks = level.getEntitiesOfClass(Entity.class, new AABB(worldPosition.above()), Predicates.instanceOf(ManaSpark.class));
		if (sparks.size() == 1) {
			Entity e = sparks.get(0);
			return (ManaSpark) e;
		}

		return null;
	}

	@Override
	public boolean areIncomingTranfersDone() {
		return !isActive();
	}

	@Override
	public int getAvailableSpaceForMana() {
		TerrestrialAgglomerationRecipe recipe = getCurrentRecipe(getInventory());
		return recipe == null ? 0 : Math.max(0, recipe.getMana() - getCurrentMana());
	}

	public float getCompletion() {
		TerrestrialAgglomerationRecipe recipe = getCurrentRecipe(getInventory());
		if (recipe == null) {
			return 0;
		}
		return ((float) getCurrentMana()) / recipe.getMana();
	}

	public int getComparatorLevel() {
		int val = (int) (getCompletion() * 15.0);
		if (getCurrentMana() > 0) {
			val = Math.max(val, 1);
		}
		return val;
	}

}
