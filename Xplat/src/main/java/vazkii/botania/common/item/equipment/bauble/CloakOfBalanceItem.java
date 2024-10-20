/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.bauble;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.apache.commons.lang3.mutable.MutableFloat;

import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.common.handler.BotaniaSounds;

public class CloakOfBalanceItem extends CloakOfVirtueItem {

	private static final ResourceLocation texture = ResourceLocation.parse(ResourcesLib.MODEL_BALANCE_CLOAK);
	private static final ResourceLocation textureGlow = ResourceLocation.parse(ResourcesLib.MODEL_BALANCE_CLOAK_GLOW);

	public CloakOfBalanceItem(Properties props) {
		super(props);
	}

	@Override
	protected boolean effectOnDamage(DamageSource src, MutableFloat amount, Player player, ItemStack stack) {
		if (!src.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
			amount.setValue(amount.getValue() / 2);

			if (src.getEntity() != null) {
				src.getEntity().hurt(player.damageSources().indirectMagic(player, player), amount.getValue());
			}

			if (amount.getValue() > player.getHealth()) {
				amount.setValue(player.getHealth() - 1);
			}

			player.level().playSound(null, player.getX(), player.getY(), player.getZ(), BotaniaSounds.holyCloak, SoundSource.PLAYERS, 1F, 1F);
			for (int i = 0; i < 30; i++) {
				double x = player.getX() + Math.random() * player.getBbWidth() * 2 - player.getBbWidth();
				double y = player.getY() + Math.random() * player.getBbHeight();
				double z = player.getZ() + Math.random() * player.getBbWidth() * 2 - player.getBbWidth();
				boolean green = Math.random() > 0.5;
				float g = green ? 1F : 0.3F;
				float b = green ? 0.3F : 1F;
				SparkleParticleData data = SparkleParticleData.sparkle(0.8F + (float) Math.random() * 0.4F, 0.3F, g, b, 3);
				player.level().addParticle(data, x, y, z, 0, 0, 0);
			}
			return true;
		}

		return false;
	}

	@Override
	ResourceLocation getCloakTexture() {
		return texture;
	}

	@Override
	ResourceLocation getCloakGlowTexture() {
		return textureGlow;
	}

}
