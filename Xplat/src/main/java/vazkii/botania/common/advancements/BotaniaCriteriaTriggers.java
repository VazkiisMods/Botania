/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.advancements;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public class BotaniaCriteriaTriggers {
	public static void init(BiConsumer<CriterionTrigger<?>, ResourceLocation> r) {
		r.accept(AlfheimPortalTrigger.INSTANCE, AlfheimPortalTrigger.ID);
		r.accept(CorporeaRequestTrigger.INSTANCE, CorporeaRequestTrigger.ID);
		r.accept(GaiaGuardianNoArmorTrigger.INSTANCE, GaiaGuardianNoArmorTrigger.ID);
		r.accept(RelicBindTrigger.INSTANCE, RelicBindTrigger.ID);
		r.accept(UseItemSuccessTrigger.INSTANCE, UseItemSuccessTrigger.ID);
		r.accept(ManaBlasterTrigger.INSTANCE, ManaBlasterTrigger.ID);
		r.accept(LokiPlaceTrigger.INSTANCE, LokiPlaceTrigger.ID);
		r.accept(AlfheimPortalBreadTrigger.INSTANCE, AlfheimPortalBreadTrigger.ID);
	}
}
