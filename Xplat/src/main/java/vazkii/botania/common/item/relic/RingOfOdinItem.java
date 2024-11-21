/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.relic;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.item.Relic;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.lib.BotaniaTags;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class RingOfOdinItem extends RelicBaubleItem {

	public RingOfOdinItem(Properties props) {
		super(props);
	}

	@Override
	public void onValidPlayerWornTick(Player player) {
		if (player.isOnFire()) {
			player.clearFire();
		}
	}

	@Override
	public Multimap<Holder<Attribute>, AttributeModifier> getEquippedAttributeModifiers(ItemStack stack) {
		Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
		attributes.put(Attributes.MAX_HEALTH,
				new AttributeModifier(BotaniaAPI.botaniaRL("odin_ring"), 20, AttributeModifier.Operation.ADD_VALUE));
		return attributes;
	}

	public static boolean onPlayerAttacked(Player player, DamageSource src) {
		return (src.is(BotaniaTags.DamageTypes.RING_OF_ODIN_IMMUNE))
				&& !EquipmentHandler.findOrEmpty(BotaniaItems.odinRing, player).isEmpty();
	}

	public static Relic makeRelic(ItemStack stack) {
		return new RelicImpl(stack, botaniaRL("challenge/odin_ring"));
	}

}
