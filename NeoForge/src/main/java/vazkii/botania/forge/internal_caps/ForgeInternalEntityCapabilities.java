package vazkii.botania.forge.internal_caps;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.AttachCapabilitiesEvent;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.internal_caps.*;
import vazkii.botania.common.lib.LibMisc;
import vazkii.botania.forge.CapabilityUtil;

import java.util.function.Supplier;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public final class ForgeInternalEntityCapabilities {
	private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, BotaniaAPI.MODID);
	public static final Supplier<AttachmentType<ForgeEthicalComponent>> TNT_ETHICAL = ATTACHMENT_TYPES.register(
			ForgeEthicalComponent.ID.getPath(),
			AttachmentType.serializable(ForgeEthicalComponent::new)::build);
	public static final Supplier<AttachmentType<ForgeSpectralRailComponent>> GHOST_RAIL =
			registerComponentAttachmentType(ForgeSpectralRailComponent.ID, ForgeSpectralRailComponent::new);
	public static final Supplier<AttachmentType<ForgeItemFlagsComponent>> INTERNAL_ITEM =
			registerComponentAttachmentType(ForgeItemFlagsComponent.ID, ForgeItemFlagsComponent::new);
	public static final Supplier<AttachmentType<ForgeKeptItemsComponent>> KEPT_ITEMS = ATTACHMENT_TYPES.register(
			ForgeKeptItemsComponent.ID.getPath(),
			AttachmentType.serializable(ForgeKeptItemsComponent::new)::build);
	public static final Supplier<AttachmentType<ForgeLooniumComponent>> LOONIUM_DROP =
			registerComponentAttachmentType(ForgeLooniumComponent.ID, ForgeLooniumComponent::new);
	public static final Supplier<AttachmentType<ForgeNarslimmusComponent>> NARSLIMMUS =
			registerComponentAttachmentType(ForgeNarslimmusComponent.ID, ForgeNarslimmusComponent::new);
	public static final Supplier<AttachmentType<ForgeTigerseyeComponent>> TIGERSEYE =
			registerComponentAttachmentType(ForgeTigerseyeComponent.ID, ForgeTigerseyeComponent::new);

	private static <T extends SerializableComponent & INBTSerializable<CompoundTag>> DeferredHolder<AttachmentType<?>, AttachmentType<T>> registerComponentAttachmentType(ResourceLocation componentId, Supplier<T> componentSupplier) {
		return ATTACHMENT_TYPES.register(componentId.getPath(), AttachmentType.serializable(componentSupplier)::build);
	}

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

	public static class ForgeEthicalComponent extends EthicalComponent implements INBTSerializable<CompoundTag> {
		public ForgeEthicalComponent(IAttachmentHolder entity) {
			super((PrimedTnt) entity);
		}
	}

	public static class ForgeSpectralRailComponent extends SpectralRailComponent implements INBTSerializable<CompoundTag> {
	}

	public static class ForgeItemFlagsComponent extends ItemFlagsComponent implements INBTSerializable<CompoundTag> {
	}

	public static class ForgeKeptItemsComponent extends KeptItemsComponent implements INBTSerializable<CompoundTag> {
	}

	public static class ForgeLooniumComponent extends LooniumComponent implements INBTSerializable<CompoundTag> {
	}

	public static class ForgeNarslimmusComponent extends NarslimmusComponent implements INBTSerializable<CompoundTag> {
	}

	public static class ForgeTigerseyeComponent extends TigerseyeComponent implements INBTSerializable<CompoundTag> {
	}
}
