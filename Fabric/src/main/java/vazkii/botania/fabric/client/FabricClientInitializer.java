package vazkii.botania.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

import vazkii.botania.api.BotaniaFabricClientCapabilities;
import vazkii.botania.client.BotaniaItemProperties;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.handler.CorporeaInputHandler;
import vazkii.botania.client.core.handler.KonamiHandler;
import vazkii.botania.client.core.handler.MiscellaneousModels;
import vazkii.botania.client.core.helper.CoreShaders;
import vazkii.botania.client.core.proxy.ClientProxy;
import vazkii.botania.client.fx.BotaniaParticles;
import vazkii.botania.client.gui.HUDHandler;
import vazkii.botania.client.gui.ManaBarTooltipComponent;
import vazkii.botania.client.gui.TooltipHandler;
import vazkii.botania.client.gui.bag.FlowerPouchGui;
import vazkii.botania.client.gui.box.BaubleBoxGui;
import vazkii.botania.client.integration.ears.EarsIntegration;
import vazkii.botania.client.model.BotaniaLayerDefinitions;
import vazkii.botania.client.model.armor.ArmorModels;
import vazkii.botania.client.render.BlockRenderLayers;
import vazkii.botania.client.render.ColorHandler;
import vazkii.botania.client.render.entity.*;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.entity.BotaniaEntities;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.armor.manasteel.ManasteelArmorItem;
import vazkii.botania.common.lib.LibMisc;
import vazkii.botania.fabric.network.FabricPacketHandler;
import vazkii.botania.xplat.XplatAbstractions;
import vazkii.patchouli.api.BookDrawScreenCallback;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

public class FabricClientInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CoreShaderRegistrationCallback.EVENT.register(ctx -> CoreShaders.init((id, vertexFormat, onLoaded) -> {
			try {
				ctx.register(id, vertexFormat, onLoaded);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		));
		FabricPacketHandler.initClient();

		// Guis
		MenuScreens.register(BotaniaItems.FLOWER_BAG_CONTAINER, FlowerPouchGui::new);
		MenuScreens.register(BotaniaItems.BAUBLE_BOX_CONTAINER, BaubleBoxGui::new);

		// Blocks and Items
		ModelLoadingPlugin.register(pluginContext -> {
			MiscellaneousModels.INSTANCE.onModelRegister(Minecraft.getInstance().getResourceManager(), pluginContext::addModels);
			pluginContext.modifyModelAfterBake().register((bakedModel, context) -> MiscellaneousModels.INSTANCE.modifyModelAfterbake(bakedModel, context.id()));
		});
		BlockRenderLayers.init(BlockRenderLayerMap.INSTANCE::putBlock);
		BotaniaItemProperties.init((i, id, propGetter) -> ItemProperties.register(i.asItem(), id, propGetter));

		// BE/Entity Renderer
		BotaniaLayerDefinitions.init((loc, supplier) -> EntityModelLayerRegistry.registerModelLayer(loc, supplier::get));
		EntityRenderers.registerBlockEntityRenderers(BlockEntityRenderers::register);
		for (var pair : EntityRenderers.BE_ITEM_RENDERER_FACTORIES.entrySet()) {
			var block = pair.getKey();
			var renderer = pair.getValue().apply(block);
			BuiltinItemRendererRegistry.INSTANCE.register(block, renderer::render);
		}
		EntityRenderers.registerEntityRenderers(EntityRendererRegistry::register);
		LivingEntityFeatureRendererRegistrationCallback.EVENT.register(this::initAuxiliaryRender);

		BotaniaParticles.FactoryHandler.registerFactories(new BotaniaParticles.FactoryHandler.Consumer() {
			@Override
			public <T extends ParticleOptions> void register(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> constructor) {
				ParticleFactoryRegistry.getInstance().register(type, constructor::apply);
			}
		});

		// Events
		BookDrawScreenCallback.EVENT.register(KonamiHandler::renderBook);
		ClientLifecycleEvents.CLIENT_STARTED.register(this::loadComplete);
		ClientTickEvents.END_CLIENT_TICK.register(ClientTickHandler::clientTickEnd);
		ClientTickEvents.END_CLIENT_TICK.register(KonamiHandler::clientTick);
		HudRenderCallback.EVENT.register(HUDHandler::onDrawScreenPost);
		ItemTooltipCallback.EVENT.register(TooltipHandler::onTooltipEvent);
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> ScreenKeyboardEvents.beforeKeyPress(screen)
				.register((screen2, key, scancode, modifiers) -> CorporeaInputHandler.buttonPressed(key, scancode)));
		TooltipComponentCallback.EVENT.register(ManaBarTooltipComponent::tryConvert);

		// Etc
		ClientProxy.initSeasonal();
		ClientProxy.initKeybindings(KeyBindingHelper::registerKeyBinding);

		registerArmors();
		registerCapabilities();

		if (XplatAbstractions.INSTANCE.isModLoaded("ears")) {
			EarsIntegration.register();
		}
	}

	private static void registerCapabilities() {
		BotaniaEntities.registerWandHudCaps((factory, types) -> BotaniaFabricClientCapabilities.ENTITY_WAND_HUD.registerForTypes((e, c) -> factory.apply(e), types));
		BotaniaBlockEntities.registerWandHudCaps((factory, types) -> BotaniaFabricClientCapabilities.WAND_HUD.registerForBlockEntities((be, c) -> factory.apply(be), types));
		BotaniaFlowerBlocks.registerWandHudCaps((factory, types) -> BotaniaFabricClientCapabilities.WAND_HUD.registerForBlockEntities((be, c) -> factory.apply(be), types));
	}

	private static void registerArmors() {
		Item[] armors = BuiltInRegistries.ITEM.stream()
				.filter(i -> i instanceof ManasteelArmorItem
						&& BuiltInRegistries.ITEM.getKey(i).getNamespace().equals(LibMisc.MOD_ID))
				.toArray(Item[]::new);

		ArmorRenderer renderer = (matrices, vertexConsumers, stack, entity, slot, light, contextModel) -> {
			ManasteelArmorItem armor = (ManasteelArmorItem) stack.getItem();
			var model = ArmorModels.get(stack);
			var texture = armor.getArmorTexture(stack, entity, slot, "");
			if (model != null) {
				contextModel.copyPropertiesTo(model);
				ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, model, ResourceLocation.parse(texture));
			}
		};
		ArmorRenderer.register(renderer, armors);
	}

	private void loadComplete(Minecraft mc) {
		ColorHandler.submitBlocks(ColorProviderRegistry.BLOCK::register);
		ColorHandler.submitItems(ColorProviderRegistry.ITEM::register);
	}

	private void initAuxiliaryRender(EntityType<? extends LivingEntity> type, LivingEntityRenderer<?, ?> renderer,
			LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper helper, EntityRendererProvider.Context ctx) {
		if (type == EntityType.PLAYER && renderer instanceof PlayerRenderer playerRenderer) {
			EntityRenderers.addAuxiliaryPlayerRenders(playerRenderer, helper::register);
		}
	}
}
