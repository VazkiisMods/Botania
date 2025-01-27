/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.flower.generating;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.helper.DelayHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class GourmaryllisBlockEntity extends GeneratingFlowerBlockEntity {
	private static final String TAG_COOLDOWN = "cooldown";
	private static final String TAG_DIGESTING_MANA = "digestingMana";
	public static final String TAG_LAST_FOODS = "lastFoods";
	public static final String TAG_LAST_FOOD_COUNT = "lastFoodCount";
	public static final String TAG_STREAK_LENGTH = "streakLength";
	private static final int RANGE = 1;
	private static final double[] STREAK_MULTIPLIERS = { 0, 1, 1.3, 1.5, 1.6, 1.7, 1.75, 1.8 };
	private static final int MAX_FOOD_VALUE = 12;
	private static final int FOOD_COOLDOWN_FACTOR = 10;
	private static final int FOOD_MANA_FACTOR = 70;
	private static final int MAX_MANA = getDigestingMana(MAX_FOOD_VALUE, STREAK_MULTIPLIERS[STREAK_MULTIPLIERS.length - 1]);

	private int cooldown = 0;
	private int digestingMana = 0;
	private final List<ItemStack> lastFoods = new LinkedList<>();
	private int streakLength = -1;
	private int lastFoodCount = 0;

	public GourmaryllisBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaFlowerBlocks.GOURMARYLLIS, pos, state);
	}

	private int getMaxStreak() {
		return STREAK_MULTIPLIERS.length - 1;
	}

	private double getMultiplierForStreak(int index) {
		// special-case repeated first foods
		if (index == 0) {
			return 1.0 / ++lastFoodCount;
		} else {
			lastFoodCount = 1;
			return STREAK_MULTIPLIERS[index];
		}
	}

	/**
	 * Processes a food, placing it in the appropriate place in the history.
	 * 
	 * @return the last time the food showed up in history.
	 */
	private int processFood(ItemStack food) {
		for (ListIterator<ItemStack> it = lastFoods.listIterator(); it.hasNext();) {
			int index = it.nextIndex();
			ItemStack streakFood = it.next();
			if (ItemStack.isSameItemSameTags(streakFood, food)) {
				it.remove();
				lastFoods.add(0, streakFood);
				return index;
			}
		}
		ItemStack newestFood = food.copyWithCount(1);
		lastFoods.add(0, newestFood);
		if (lastFoods.size() >= getMaxStreak()) {
			lastFoods.remove(lastFoods.size() - 1);
		}
		return getMaxStreak();
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		if (getLevel().isClientSide) {
			return;
		}

		if (cooldown > -1) {
			cooldown--;
		}
		if (digestingMana != 0) {
			int munchInterval = 2 + (2 * lastFoodCount);

			if (cooldown == 0) {
				addMana(digestingMana);
				digestingMana = 0;

				float burpPitch = (float) Math.pow(2.0, (streakLength == 0 ? -lastFoodCount : streakLength) / 12.0);
				//Usage of vanilla sound event: Subtitle is just "Burp", at least in English, and not specific to players.
				getLevel().playSound(null, getEffectivePos(), SoundEvents.PLAYER_BURP, SoundSource.BLOCKS, 1F, burpPitch);
				getLevel().gameEvent(null, GameEvent.BLOCK_DEACTIVATE, getEffectivePos());
				sync();
			} else if (cooldown % munchInterval == 0) {
				//Usage of vanilla sound event: Subtitle is "Eating", generic sounds are meant to be reused.
				getLevel().playSound(null, getEffectivePos(), SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 0.5F, 1F);

				Vec3 offset = getLevel().getBlockState(getEffectivePos()).getOffset(getLevel(), getEffectivePos()).add(0.4, 0.6, 0.4);

				((ServerLevel) getLevel()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, lastFoods.get(0)), getEffectivePos().getX() + offset.x, getEffectivePos().getY() + offset.y, getEffectivePos().getZ() + offset.z, 10, 0.1D, 0.1D, 0.1D, 0.03D);
			}
		}

		List<ItemEntity> items = getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(getEffectivePos().offset(-RANGE, -RANGE, -RANGE), getEffectivePos().offset(RANGE + 1, RANGE + 1, RANGE + 1)));

		for (ItemEntity item : items) {
			ItemStack stack = item.getItem();

			if (DelayHelper.canInteractWithImmediate(this, item) && stack.getItem().isEdible()) {
				if (cooldown <= 0) {
					streakLength = Math.min(streakLength + 1, processFood(stack));

					int val = getFoodValue(stack);
					digestingMana = getDigestingMana(val, getMultiplierForStreak(streakLength));
					cooldown = getCooldown(val);
					//Usage of vanilla sound event: Subtitle is "Eating", generic sounds are meant to be reused.
					item.playSound(SoundEvents.GENERIC_EAT, 0.2F, 0.6F);
					getLevel().gameEvent(null, GameEvent.EAT, item.position());
					getLevel().gameEvent(null, GameEvent.BLOCK_ACTIVATE, getEffectivePos());
					sync();
					((ServerLevel) getLevel()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), item.getX(), item.getY(), item.getZ(), 20, 0.1D, 0.1D, 0.1D, 0.05D);
				}

				item.discard();
			}
		}
	}

	private static int getCooldown(int foodValue) {
		return Math.max(1, foodValue * FOOD_COOLDOWN_FACTOR);
	}

	private static int getDigestingMana(int foodValue, double streakFactor) {
		return Math.max(1, (int) (foodValue * foodValue * FOOD_MANA_FACTOR * streakFactor));
	}

	private static int getFoodValue(ItemStack stack) {
		FoodProperties foodProperties = stack.getItem().getFoodProperties();
		// greetings to GregTech's Purple Drink, which claims to be edible but does not have food properties
		int nutrition = foodProperties != null ? foodProperties.getNutrition() : 0;
		return Math.min(MAX_FOOD_VALUE, nutrition);
	}

	@Override
	public void writeToPacketNBT(CompoundTag cmp) {
		super.writeToPacketNBT(cmp);
		cmp.putInt(TAG_COOLDOWN, cooldown);
		cmp.putInt(TAG_DIGESTING_MANA, digestingMana);
		ListTag foodList = new ListTag();
		for (ItemStack food : lastFoods) {
			foodList.add(food.save(new CompoundTag()));
		}
		cmp.put(TAG_LAST_FOODS, foodList);
		cmp.putInt(TAG_LAST_FOOD_COUNT, lastFoodCount);
		cmp.putInt(TAG_STREAK_LENGTH, streakLength);
	}

	@Override
	public void readFromPacketNBT(CompoundTag cmp) {
		super.readFromPacketNBT(cmp);
		cooldown = cmp.getInt(TAG_COOLDOWN);
		digestingMana = cmp.getInt(TAG_DIGESTING_MANA);
		lastFoods.clear();
		ListTag foodList = cmp.getList(TAG_LAST_FOODS, Tag.TAG_COMPOUND);
		for (int i = 0; i < foodList.size(); i++) {
			lastFoods.add(ItemStack.of(foodList.getCompound(i)));
		}
		lastFoodCount = cmp.getInt(TAG_LAST_FOOD_COUNT);
		streakLength = cmp.getInt(TAG_STREAK_LENGTH);
	}

	@Override
	public RadiusDescriptor getRadius() {
		return RadiusDescriptor.Rectangle.square(getEffectivePos(), RANGE);
	}

	@Override
	public int getMaxMana() {
		return MAX_MANA;
	}

	@Override
	public int getColor() {
		return 0xD3D604;
	}

}
