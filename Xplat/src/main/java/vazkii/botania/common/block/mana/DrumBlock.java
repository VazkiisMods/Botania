/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.mana;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.api.mana.ManaTrigger;
import vazkii.botania.common.block.BotaniaWaterloggedBlock;
import vazkii.botania.common.block.flower.functional.BergamuteBlockEntity;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.EntityHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.HornItem;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.mixin.LivingEntityAccessor;
import vazkii.botania.mixin.MushroomCowAccessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrumBlock extends BotaniaWaterloggedBlock {

	public static final int MAX_NUM_SHEARED = 4;
	public static final int GATHER_RANGE = 10;
	public static final int MINIMUM_REMAINING_EGG_TIME = 600;
	public static final int STARTLED_EGG_TIME = 200;

	public enum Variant {
		WILD,
		GATHERING,
		CANOPY
	}

	private static final VoxelShape SHAPE = Block.box(3, 1, 3, 13, 15, 13);
	private final Variant variant;

	public DrumBlock(Variant v, Properties builder) {
		super(builder);
		this.variant = v;
	}

	@NotNull
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return SHAPE;
	}

	public static void gatherProduce(Level world, BlockPos pos) {
		List<Mob> mobs = world.getEntitiesOfClass(Mob.class, new AABB(pos).inflate(GATHER_RANGE),
				mob -> mob.isAlive() && !BergamuteBlockEntity.isBergamuteNearby(world, mob.getX(), mob.getY(), mob.getZ()));
		List<Shearable> shearables = new ArrayList<>();

		for (Mob mob : mobs) {
			if (mob instanceof Chicken chicken && !chicken.isBaby() && !chicken.isChickenJockey()) {
				speedUpEggLaying(chicken);
			}
			if (mob.getType().is(BotaniaTags.Entities.DRUM_MILKABLE) && !mob.isBaby()) {
				convertNearby(mob, Items.BUCKET, Items.MILK_BUCKET);
			}
			if (mob instanceof MushroomCow mooshroom && !mooshroom.isBaby()) {
				if (mooshroom.getVariant() == MushroomCow.MushroomType.BROWN) {
					fillBowlSuspiciously(mooshroom);
				}
				convertNearby(mob, Items.BOWL, Items.MUSHROOM_STEW);
			}
			if (mob instanceof Shearable shearable && !mob.getType().is(BotaniaTags.Entities.DRUM_NO_SHEARING) && shearable.readyForShearing()) {
				shearables.add(shearable);
			}
		}

		Collections.shuffle(shearables);
		int sheared = 0;

		for (Shearable shearable : shearables) {
			if (sheared > MAX_NUM_SHEARED) {
				break;
			}

			shearable.shear(SoundSource.BLOCKS);
			++sheared;
		}
	}

	private static void speedUpEggLaying(Chicken chicken) {
		if (chicken.eggTime > MINIMUM_REMAINING_EGG_TIME) {
			chicken.eggTime = Math.max(MINIMUM_REMAINING_EGG_TIME, chicken.eggTime / 2);
		} else if (chicken.eggTime < STARTLED_EGG_TIME && chicken.eggTime > 1) {
			chicken.eggTime = 1;
			((LivingEntityAccessor) chicken).botania_playHurtSound(chicken.damageSources().magic());
		}
	}

	private static void convertNearby(Mob mob, Item from, Item to) {
		Level world = mob.level();
		List<ItemEntity> fromEntities = world.getEntitiesOfClass(ItemEntity.class, mob.getBoundingBox(),
				itemEntity -> itemEntity.isAlive() && itemEntity.getItem().is(from));
		for (ItemEntity fromEntity : fromEntities) {
			ItemStack fromStack = fromEntity.getItem();
			for (int i = fromStack.getCount(); i > 0; i--) {
				spawnItem(mob, new ItemStack(to));
			}
			fromEntity.discard();
		}
	}

	private static void spawnItem(Mob mob, ItemStack to) {
		Level world = mob.level();
		ItemEntity ent = mob.spawnAtLocation(to, 1.0F);
		ent.setDeltaMovement(ent.getDeltaMovement().add(
				world.random.nextFloat() * 0.05F,
				(world.random.nextFloat() - world.random.nextFloat()) * 0.1F,
				(world.random.nextFloat() - world.random.nextFloat()) * 0.1F
		));
	}

	private static void fillBowlSuspiciously(MushroomCow mushroomCow) {
		MushroomCowAccessor mushroomCowAccessor = (MushroomCowAccessor) mushroomCow;
		var stewEffects = mushroomCowAccessor.getStewEffects();
		if (stewEffects == null) {
			return;
		}

		Level world = mushroomCow.level();
		List<ItemEntity> bowlItemEntities = world.getEntitiesOfClass(ItemEntity.class, mushroomCow.getBoundingBox(),
				itemEntity -> itemEntity.getItem().is(Items.BOWL) && !itemEntity.getItem().isEmpty());
		for (ItemEntity bowlItemEntity : bowlItemEntities) {
			ItemStack bowlItem = bowlItemEntity.getItem();
			ItemStack stewItem = new ItemStack(Items.SUSPICIOUS_STEW);
			stewItem.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, mushroomCowAccessor.getStewEffects());
			spawnItem(mushroomCow, stewItem);

			EntityHelper.shrinkItem(bowlItemEntity);
			if (bowlItem.getCount() == 0) {
				bowlItemEntity.discard();
			}

			// only one suspicious stew per flower fed
			mushroomCowAccessor.setStewEffects(null);
			break;
		}
	}

	public static class ManaTriggerImpl implements ManaTrigger {
		private final Level world;
		private final BlockPos pos;
		private final Variant variant;

		public ManaTriggerImpl(Level world, BlockPos pos, BlockState state) {
			this.world = world;
			this.pos = pos;
			this.variant = ((DrumBlock) state.getBlock()).variant;
		}

		@Override
		public void onBurstCollision(ManaBurst burst) {
			if (burst.isFake()) {
				return;
			}
			if (world.isClientSide) {
				world.addParticle(ParticleTypes.NOTE, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5D, 1.0 / 24.0, 0, 0);
				return;
			}
			switch (variant) {
				case WILD -> HornItem.breakGrass(world, new ItemStack(BotaniaItems.grassHorn), pos, null);
				case CANOPY -> HornItem.breakGrass(world, new ItemStack(BotaniaItems.leavesHorn), pos, null);
				case GATHERING -> gatherProduce(world, pos);
			}

			for (int i = 0; i < 10; i++) {
				world.playSound(null, pos, BotaniaSounds.drum, SoundSource.BLOCKS, 1F, 1F);
			}
		}
	}
}
