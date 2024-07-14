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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.EntityHelper;
import vazkii.botania.mixin.ExperienceOrbAccessor;

import java.util.List;
import java.util.Map;

public class RosaArcanaBlockEntity extends GeneratingFlowerBlockEntity {
	private static final int MANA_PER_XP = 50;
	private static final int RANGE = 1;

	public RosaArcanaBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaFlowerBlocks.ROSA_ARCANA, pos, state);
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		if (level.isClientSide || getMana() >= getMaxMana()) {
			return;
		}

		AABB effectBounds = new AABB(getEffectivePos()).inflate(RANGE);

		/* TODO: Now that player and orb yields are identical, it might look better/funnier
			to instead make xp orbs leak out of the player's head instead directly consuming.
		*/
		List<Player> players = getLevel().getEntitiesOfClass(Player.class, effectBounds);
		for (Player player : players) {
			// You would think checking totalExperience is right, but it seems to be
			// possibly equal to zero even when the level is > 0.
			// Instead, check the level and intra-level progress separately.
			if ((player.experienceLevel > 0 || player.experienceProgress > 0)
					&& player.onGround()) {
				player.giveExperiencePoints(-1);
				addMana(MANA_PER_XP);
				sync();
				return;
			}
		}

		List<ExperienceOrb> orbs = getLevel().getEntitiesOfClass(ExperienceOrb.class, effectBounds);
		for (ExperienceOrb orb : orbs) {
			int count = ((ExperienceOrbAccessor) orb).botania_getCount();
			if (orb.isAlive() && count > 0) {
				addMana(orb.getValue() * MANA_PER_XP);
				((ExperienceOrbAccessor) orb).botania_setCount(count - 1);
				if (count == 1) {
					orb.discard();
				}
				float pitch = (level.random.nextFloat() - level.random.nextFloat()) * 0.35F + 0.9F;
				//Usage of vanilla sound event: Subtitle is "Experience gained", and this is about gaining experience anyways.
				level.playSound(null, getEffectivePos(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.07F, pitch);
				sync();
				return;
			}
		}

		List<ItemEntity> items = getLevel().getEntitiesOfClass(ItemEntity.class, effectBounds, e -> e.isAlive() && !e.getItem().isEmpty());
		for (ItemEntity entity : items) {
			ItemStack stack = entity.getItem();
			if (stack.is(Items.ENCHANTED_BOOK) || stack.isEnchanted()) {
				int xp = getEnchantmentXpValue(stack);
				if (xp > 0) {
					ItemStack newStack = removeNonCurses(stack);
					newStack.setCount(1);
					EntityHelper.shrinkItem(entity);

					ItemEntity newEntity = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), newStack);
					newEntity.setDeltaMovement(entity.getDeltaMovement());
					level.addFreshEntity(newEntity);

					level.playSound(null, getEffectivePos(), BotaniaSounds.arcaneRoseDisenchant, SoundSource.BLOCKS, 1F, this.level.random.nextFloat() * 0.1F + 0.9F);
					while (xp > 0) {
						int i = ExperienceOrb.getExperienceValue(xp);
						xp -= i;
						level.addFreshEntity(new ExperienceOrb(level, getEffectivePos().getX() + 0.5D, getEffectivePos().getY() + 0.5D, getEffectivePos().getZ() + 0.5D, i));
					}
					return;
				}
			}
		}
	}

	// [VanillaCopy] GrindstoneMenu
	private static int getEnchantmentXpValue(ItemStack stack) {
		int ret = 0;
		Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);

		for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
			Enchantment enchantment = entry.getKey();
			Integer integer = entry.getValue();
			if (!enchantment.isCurse()) {
				ret += enchantment.getMinCost(integer);
			}
		}

		return ret;
	}

	// [VanillaCopy] GrindstoneMenu, no damage and count setting
	private static ItemStack removeNonCurses(ItemStack stack) {
		ItemStack itemstack = stack.copy();
		itemstack.removeTagKey("Enchantments");
		itemstack.removeTagKey("StoredEnchantments");

		Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
		map.keySet().removeIf(e -> !e.isCurse());
		EnchantmentHelper.setEnchantments(map, itemstack);
		itemstack.setRepairCost(0);
		if (itemstack.is(Items.ENCHANTED_BOOK) && map.size() == 0) {
			itemstack = new ItemStack(Items.BOOK);
			if (stack.hasCustomHoverName()) {
				itemstack.setHoverName(stack.getHoverName());
			}
		}

		for (int i = 0; i < map.size(); ++i) {
			itemstack.setRepairCost(AnvilMenu.calculateIncreasedRepairCost(itemstack.getBaseRepairCost()));
		}

		return itemstack;
	}

	@Override
	public RadiusDescriptor getRadius() {
		return RadiusDescriptor.Rectangle.square(getEffectivePos(), RANGE);
	}

	@Override
	public int getColor() {
		return 0xFF8EF8;
	}

	@Override
	public int getMaxMana() {
		return 6000;
	}

}
