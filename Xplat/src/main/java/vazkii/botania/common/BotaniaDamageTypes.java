package vazkii.botania.common;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.Nullable;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class BotaniaDamageTypes {
	public static final ResourceKey<DamageType> PLAYER_ATTACK_ARMOR_PIERCING =
			ResourceKey.create(Registries.DAMAGE_TYPE, botaniaRL("player_attack_armor_piercing"));
	public static final ResourceKey<DamageType> RELIC_DAMAGE =
			ResourceKey.create(Registries.DAMAGE_TYPE, botaniaRL("relic_damage"));
	public static final ResourceKey<DamageType> KEY_EXPLOSION =
			ResourceKey.create(Registries.DAMAGE_TYPE, botaniaRL("key_explosion"));

	public static final DamageType PLAYER_AP = new DamageType("player", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F);
	public static final DamageType RELIC = new DamageType("botania.relic", DamageScaling.NEVER, 1F, DamageEffects.FREEZING);
	public static final DamageType KEY = new DamageType("botania.key_explosion", DamageScaling.ALWAYS, 0.1F);

	public static class Sources {

		private static Holder.Reference<DamageType> getHolder(RegistryAccess ra, ResourceKey<DamageType> key) {
			return ra.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
		}

		private static DamageSource source(RegistryAccess ra, ResourceKey<DamageType> resourceKey) {
			return new DamageSource(getHolder(ra, resourceKey));
		}

		private static DamageSource source(RegistryAccess ra, ResourceKey<DamageType> resourceKey, @Nullable Entity entity) {
			return new DamageSource(getHolder(ra, resourceKey), entity);
		}

		private static DamageSource source(RegistryAccess ra, ResourceKey<DamageType> resourceKey, @Nullable Entity entity, @Nullable Entity entity2) {
			return new DamageSource(getHolder(ra, resourceKey), entity, entity2);
		}

		public static DamageSource playerAttackArmorPiercing(RegistryAccess ra, Player player) {
			return source(ra, PLAYER_ATTACK_ARMOR_PIERCING, player);
		}

		public static DamageSource relicDamage(RegistryAccess ra) {
			return source(ra, RELIC_DAMAGE);
		}
	}
}
