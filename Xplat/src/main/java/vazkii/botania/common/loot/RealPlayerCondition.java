/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.loot;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.helper.PlayerHelper;

import java.util.Set;

public class RealPlayerCondition implements LootItemCondition {
	public static final RealPlayerCondition INSTANCE = new RealPlayerCondition();
	public static final Codec<RealPlayerCondition> CODEC = Codec.unit(INSTANCE);

	private RealPlayerCondition() {}

	@Override
	public boolean test(LootContext lootContext) {
		Player player = lootContext.getParamOrNull(LootContextParams.LAST_DAMAGE_PLAYER);
		return PlayerHelper.isTruePlayer(player);
	}

	@NotNull
	@Override
	public Set<LootContextParam<?>> getReferencedContextParams() {
		return ImmutableSet.of(LootContextParams.LAST_DAMAGE_PLAYER);
	}

	@NotNull
	@Override
	public LootItemConditionType getType() {
		return BotaniaLootModifiers.KILLED_BY_REAL_PLAYER;
	}
}
