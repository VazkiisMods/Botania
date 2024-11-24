package vazkii.botania.forge.internal_caps;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import vazkii.botania.common.internal_caps.*;
import vazkii.botania.common.lib.LibMisc;
import vazkii.botania.forge.CapabilityUtil;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public final class ForgeInternalEntityCapabilities {
	public static final Capability<EthicalComponent> TNT_ETHICAL = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<SpectralRailComponent> GHOST_RAIL = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<ItemFlagsComponent> INTERNAL_ITEM = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<KeptItemsComponent> KEPT_ITEMS = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<LooniumComponent> LOONIUM_DROP = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<NarslimmusComponent> NARSLIMMUS = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<TigerseyeComponent> TIGERSEYE = CapabilityManager.get(new CapabilityToken<>() {});

	@Mod.EventBusSubscriber(modid = LibMisc.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ModBusEvents {
		@SubscribeEvent
		public static void registerCaps(RegisterCapabilitiesEvent evt) {
			evt.register(EthicalComponent.class);
			evt.register(SpectralRailComponent.class);
			evt.register(ItemFlagsComponent.class);
			evt.register(KeptItemsComponent.class);
			evt.register(LooniumComponent.class);
			evt.register(NarslimmusComponent.class);
			evt.register(TigerseyeComponent.class);
		}
	}

	@Mod.EventBusSubscriber(modid = LibMisc.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class ForgeBusEvents {
		@SubscribeEvent
		public static void attachCapabilities(AttachCapabilitiesEvent<Entity> evt) {
			var entity = evt.getObject();

			if (entity instanceof PrimedTnt tnt) {
				evt.addCapability(prefix("tnt_ethical"), CapabilityUtil.makeSavedProvider(TNT_ETHICAL, new EthicalComponent(tnt)));
			}
			if (entity instanceof AbstractMinecart) {
				evt.addCapability(prefix("ghost_rail"), CapabilityUtil.makeSavedProvider(GHOST_RAIL, new SpectralRailComponent()));
			}
			if (entity instanceof ItemEntity) {
				evt.addCapability(prefix("iitem"), CapabilityUtil.makeSavedProvider(INTERNAL_ITEM, new ItemFlagsComponent()));
			}
			if (entity instanceof Player) {
				evt.addCapability(prefix("kept_items"), CapabilityUtil.makeSavedProvider(KEPT_ITEMS, new KeptItemsComponent()));
			}
			if (entity instanceof Mob) {
				evt.addCapability(prefix("loonium_drop"), CapabilityUtil.makeSavedProvider(LOONIUM_DROP, new LooniumComponent()));
			}
			if (entity instanceof Slime) {
				evt.addCapability(prefix("narslimmus"), CapabilityUtil.makeSavedProvider(NARSLIMMUS, new NarslimmusComponent()));
			}
			if (entity instanceof Creeper) {
				evt.addCapability(prefix("tigerseye_pacified"), CapabilityUtil.makeSavedProvider(TIGERSEYE, new TigerseyeComponent()));
			}
		}
	}

	private ForgeInternalEntityCapabilities() {}
}
