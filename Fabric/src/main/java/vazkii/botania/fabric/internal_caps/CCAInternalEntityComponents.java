/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.internal_caps;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

import vazkii.botania.common.internal_caps.*;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class CCAInternalEntityComponents implements EntityComponentInitializer {
	public static final ComponentKey<CCAEthicalComponent> TNT_ETHICAL = ComponentRegistryV3.INSTANCE.getOrCreate(prefix("tnt_ethical"), CCAEthicalComponent.class);
	public static final ComponentKey<CCASpectralRailComponent> GHOST_RAIL = ComponentRegistryV3.INSTANCE.getOrCreate(prefix("ghost_rail"), CCASpectralRailComponent.class);
	public static final ComponentKey<CCAItemFlagsComponent> INTERNAL_ITEM = ComponentRegistryV3.INSTANCE.getOrCreate(prefix("iitem"), CCAItemFlagsComponent.class);
	public static final ComponentKey<CCAKeptItemsComponent> KEPT_ITEMS = ComponentRegistryV3.INSTANCE.getOrCreate(prefix("kept_items"), CCAKeptItemsComponent.class);
	public static final ComponentKey<CCALooniumComponent> LOONIUM_DROP = ComponentRegistryV3.INSTANCE.getOrCreate(prefix("loonium_drop"), CCALooniumComponent.class);
	public static final ComponentKey<CCANarslimmusComponent> NARSLIMMUS = ComponentRegistryV3.INSTANCE.getOrCreate(prefix("narslimmus"), CCANarslimmusComponent.class);
	public static final ComponentKey<CCATigerseyeComponent> TIGERSEYE = ComponentRegistryV3.INSTANCE.getOrCreate(prefix("tigerseye_pacified"), CCATigerseyeComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerFor(Mob.class, LOONIUM_DROP, e -> new CCALooniumComponent());
		registry.registerFor(PrimedTnt.class, TNT_ETHICAL, CCAEthicalComponent::new);
		registry.registerFor(Slime.class, NARSLIMMUS, e -> new CCANarslimmusComponent());
		registry.registerFor(ItemEntity.class, INTERNAL_ITEM, e -> new CCAItemFlagsComponent());
		registry.registerFor(AbstractMinecart.class, GHOST_RAIL, e -> new CCASpectralRailComponent());
		registry.registerFor(Creeper.class, TIGERSEYE, creeper -> new CCATigerseyeComponent());
		// Never copy as we handle it ourselves in ResoluteIvyItem.onPlayerRespawn
		registry.registerForPlayers(KEPT_ITEMS, e -> new CCAKeptItemsComponent(), RespawnCopyStrategy.NEVER_COPY);
	}

	// NB: These all have to be public because CCA generates direct references to them via ASM (but why???)

	public static class CCAEthicalComponent extends EthicalComponent implements Component {
		public CCAEthicalComponent(PrimedTnt entity) {
			super(entity);
		}
	}

	public static class CCASpectralRailComponent extends SpectralRailComponent implements Component {
	}

	public static class CCAItemFlagsComponent extends ItemFlagsComponent implements Component {
	}

	public static class CCAKeptItemsComponent extends KeptItemsComponent implements Component {
	}

	public static class CCALooniumComponent extends LooniumComponent implements Component {
	}

	public static class CCANarslimmusComponent extends NarslimmusComponent implements Component {
	}

	public static class CCATigerseyeComponent extends TigerseyeComponent implements Component {
	}
}
