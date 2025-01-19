/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.common.lib.BotaniaTags;

import java.util.ArrayList;
import java.util.List;

public class CocoonBlockEntity extends BotaniaBlockEntity {
	private static final String TAG_TIME_PASSED = "timePassed";
	private static final String TAG_EMERALDS_GIVEN = "emeraldsGiven";
	private static final String TAG_CHORUS_FRUIT_GIVEN = "chorusFruitGiven";
	private static final String TAG_GAIA_SPIRIT_GIVEN = "gaiaSpiritGiven";

	public static final int TOTAL_TIME = 2400;
	public static final int MAX_EMERALDS = 20;
	public static final int MAX_CHORUS_FRUITS = 20;
	private static final float SPECIAL_CHANCE = 0.075F;

	public int timePassed;
	public int emeraldsGiven;
	public int chorusFruitGiven;
	public boolean gaiaSpiritGiven;

	public CocoonBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.COCOON, pos, state);
	}

	public static void commonTick(Level level, BlockPos worldPosition, BlockState state, CocoonBlockEntity self) {
		self.timePassed++;
		if (self.timePassed >= TOTAL_TIME) {
			self.hatch();
		}
	}

	private void hatch() {
		if (!level.isClientSide) {
			timePassed = 0;
			level.destroyBlock(worldPosition, false);

			Mob entity = null;
			BlockPos placePos = worldPosition;
			float rareChance = gaiaSpiritGiven ? 1F : SPECIAL_CHANCE;

			float villagerChance = Math.min(1F, (float) emeraldsGiven / (float) MAX_EMERALDS);
			float shulkerChance = Math.min(1F, (float) chorusFruitGiven / (float) MAX_CHORUS_FRUITS);

			List<BlockPos> validWater = new ArrayList<>();
			for (Direction d : Direction.values()) {
				BlockPos blockPos = (d == Direction.UP) ? worldPosition : worldPosition.relative(d);
				if (level.hasChunkAt(blockPos)
						&& level.getBlockState(blockPos).is(Blocks.WATER)) {
					validWater.add(blockPos);
				}
			}

			if (Math.random() < shulkerChance) {
				entity = EntityType.SHULKER.create(level);
			} else if (Math.random() < villagerChance) {
				Villager villager = EntityType.VILLAGER.create(level);
				if (villager != null) {
					VillagerType type = VillagerType.byBiome(level.getBiome(worldPosition));
					villager.setVillagerData(villager.getVillagerData().setType(type));
				}
				entity = villager;
			} else if (!validWater.isEmpty()) {
				placePos = validWater.get(level.random.nextInt(validWater.size()));
				if (Math.random() < rareChance) {
					entity = random(BotaniaTags.Entities.COCOON_RARE_AQUATIC);
				} else {
					entity = random(BotaniaTags.Entities.COCOON_COMMON_AQUATIC);
				}
			} else {
				if (Math.random() < rareChance) {
					entity = random(BotaniaTags.Entities.COCOON_RARE);
				} else {
					entity = random(BotaniaTags.Entities.COCOON_COMMON);
				}
			}

			if (entity != null) {
				if (level.random.nextFloat() < 0.01) {
					// gonna make modded minecraft items into a gacha game
					// and somehow find a way to add jeanne d'arc to it
					// - Vazkii 2021
					var name = Component.literal("Jeanne d'");
					name.append(entity.getName());
					name.append(Component.literal(" [SSR]"));
					entity.setCustomName(name.withStyle(ChatFormatting.GOLD));
					entity.setCustomNameVisible(true);
				}

				entity.setPos(placePos.getX() + 0.5, placePos.getY() + 0.5, placePos.getZ() + 0.5);
				if (entity instanceof AgeableMob ageable) {
					ageable.setAge(-24000);
				}
				entity.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(getBlockPos()), MobSpawnType.EVENT, null);
				entity.setPersistenceRequired();
				level.addFreshEntity(entity);
				entity.spawnAnim();
			}
		}
	}

	public void forceRare() {
		gaiaSpiritGiven = true;
		timePassed = Math.max(timePassed, TOTAL_TIME / 2);
	}

	@Nullable
	private Mob random(TagKey<EntityType<?>> tag) {
		EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getTag(tag)
				.flatMap(t -> t.getRandomElement(level.random))
				.map(Holder::value)
				.orElse(null);
		if (type == null) {
			return null;
		}

		if (type == EntityType.COW && level.random.nextFloat() < 0.01) {
			type = EntityType.MOOSHROOM;
		}
		Entity entity = type.create(level);
		return entity instanceof Mob mob ? mob : null;
	}

	@Override
	public void writePacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		cmp.putInt(TAG_TIME_PASSED, timePassed);
		cmp.putInt(TAG_EMERALDS_GIVEN, emeraldsGiven);
		cmp.putInt(TAG_CHORUS_FRUIT_GIVEN, chorusFruitGiven);
		cmp.putBoolean(TAG_GAIA_SPIRIT_GIVEN, gaiaSpiritGiven);
	}

	@Override
	public void readPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		timePassed = cmp.getInt(TAG_TIME_PASSED);
		emeraldsGiven = cmp.getInt(TAG_EMERALDS_GIVEN);
		chorusFruitGiven = cmp.getInt(TAG_CHORUS_FRUIT_GIVEN);
		gaiaSpiritGiven = cmp.getBoolean(TAG_GAIA_SPIRIT_GIVEN);
	}
}
