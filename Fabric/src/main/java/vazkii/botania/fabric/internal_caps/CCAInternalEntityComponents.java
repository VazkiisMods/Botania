/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.internal_caps;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

import vazkii.botania.common.internal_caps.*;

public class CCAInternalEntityComponents implements EntityComponentInitializer {
	public static final ComponentKey<CCAEthicalComponent> TNT_ETHICAL = ComponentRegistryV3.INSTANCE.getOrCreate(EthicalComponent.ID, CCAEthicalComponent.class);
	public static final ComponentKey<CCASpectralRailComponent> GHOST_RAIL = ComponentRegistryV3.INSTANCE.getOrCreate(SpectralRailComponent.ID, CCASpectralRailComponent.class);
	public static final ComponentKey<CCAItemFlagsComponent> INTERNAL_ITEM = ComponentRegistryV3.INSTANCE.getOrCreate(ItemFlagsComponent.ID, CCAItemFlagsComponent.class);
	public static final ComponentKey<CCAKeptItemsComponent> KEPT_ITEMS = ComponentRegistryV3.INSTANCE.getOrCreate(KeptItemsComponent.ID, CCAKeptItemsComponent.class);
	public static final ComponentKey<CCALooniumComponent> LOONIUM_DROP = ComponentRegistryV3.INSTANCE.getOrCreate(LooniumComponent.ID, CCALooniumComponent.class);
	public static final ComponentKey<CCANarslimmusComponent> NARSLIMMUS = ComponentRegistryV3.INSTANCE.getOrCreate(NarslimmusComponent.ID, CCANarslimmusComponent.class);
	public static final ComponentKey<CCATigerseyeComponent> TIGERSEYE = ComponentRegistryV3.INSTANCE.getOrCreate(TigerseyeComponent.ID, CCATigerseyeComponent.class);

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

	//TODO the CCA Component needs the NBT methods now. Decide what to do here
	public static class CCAEthicalComponent extends EthicalComponent implements Component {
		public CCAEthicalComponent(PrimedTnt entity) {
			super(entity);
		}

		@Override
		public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {

		}

		@Override
		public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {

		}
	}

	public static class CCASpectralRailComponent extends SpectralRailComponent implements Component {
		@Override
		public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {

		}

		@Override
		public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {

		}
	}

	public static class CCAItemFlagsComponent extends ItemFlagsComponent implements Component {
		@Override
		public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {

		}

		@Override
		public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {

		}
	}

	public static class CCAKeptItemsComponent extends KeptItemsComponent implements Component {
		@Override
		public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {

		}

		@Override
		public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {

		}
	}

	public static class CCALooniumComponent extends LooniumComponent implements Component {
		@Override
		public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {

		}

		@Override
		public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {

		}
	}

	public static class CCANarslimmusComponent extends NarslimmusComponent implements Component {
		@Override
		public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {

		}

		@Override
		public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {

		}
	}

	public static class CCATigerseyeComponent extends TigerseyeComponent implements Component {
		@Override
		public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {

		}

		@Override
		public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {

		}
	}
}
