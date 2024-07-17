/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.common.annotations.SoftImplement;
import vazkii.botania.common.item.equipment.tool.manasteel.ManasteelPickaxeItem;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.Map;

public class VitreousPickaxeItem extends ManasteelPickaxeItem {
	private static final String TAG_SILK_HACK = "botania:silk_hack";
	private static final int MANA_PER_DAMAGE = 160;
	private static final Tier MATERIAL = new Tier() {
		@Override
		public int getUses() {
			return 125;
		}

		@Override
		public float getSpeed() {
			return 4.8F;
		}

		@Override
		public float getAttackDamageBonus() {
			return 0;
		}

		@Override
		public int getLevel() {
			return 0;
		}

		@Override
		public int getEnchantmentValue() {
			return 10;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.of(Blocks.GLASS);
		}
	};

	public VitreousPickaxeItem(Properties props) {
		super(MATERIAL, props, -1);
	}

	/*
	* No way to modify the loot context so we're gonna go braindead hack workaround here:
	* - When block starting to break, if the tool doesn't have silktouch already, add it and add a "temp silk touch" flag
	* - Every tick, if the "temp silk touch" flag is present, remove it and remove any silk touch enchants from the stack
	*/

	@SoftImplement("IItemExtension")
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
		BlockState state = player.level().getBlockState(pos);
		boolean hasSilk = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) > 0;
		if (hasSilk || !isGlass(state)) {
			return false;
		}

		itemstack.enchant(Enchantments.SILK_TOUCH, 1);
		itemstack.getTag().putBoolean(TAG_SILK_HACK, true);

		return false;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity player, int slot, boolean selected) {
		super.inventoryTick(stack, world, player, slot, selected);
		if (stack.getOrCreateTag().getBoolean(TAG_SILK_HACK)) {
			stack.getTag().remove(TAG_SILK_HACK);
			Map<Enchantment, Integer> ench = EnchantmentHelper.deserializeEnchantments(stack.getEnchantmentTags());
			ench.remove(Enchantments.SILK_TOUCH);
			EnchantmentHelper.setEnchantments(ench, stack);
		}
	}

	private boolean isGlass(BlockState state) {
		return XplatAbstractions.INSTANCE.isInGlassTag(state);
	}

	@Override
	public int getManaPerDamage() {
		return MANA_PER_DAMAGE;
	}

	@Override
	public int getSortingPriority(ItemStack stack, BlockState state) {
		return isGlass(state) ? Integer.MAX_VALUE : 0;
	}

}
