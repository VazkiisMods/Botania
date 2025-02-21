package vazkii.botania.neoforge.internal_caps;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.PrimedTnt;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.internal_caps.*;

import java.util.function.Supplier;

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

	public static void init(IEventBus eventBus) {
		ATTACHMENT_TYPES.register(eventBus);
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
