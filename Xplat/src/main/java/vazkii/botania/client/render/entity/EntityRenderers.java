package vazkii.botania.client.render.entity;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import vazkii.botania.client.render.block_entity.*;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.GaiaHeadBlockEntity;
import vazkii.botania.common.entity.BotaniaEntities;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public final class EntityRenderers {
	public interface EntityRendererConsumer {
		<E extends Entity> void accept(EntityType<? extends E> entityType,
				EntityRendererProvider<E> entityRendererFactory);
	}

	public static void registerEntityRenderers(EntityRendererConsumer consumer) {
		consumer.accept(BotaniaEntities.MANA_BURST, NoopRenderer::new);
		consumer.accept(BotaniaEntities.PLAYER_MOVER, NoopRenderer::new);
		consumer.accept(BotaniaEntities.FLAME_RING, NoopRenderer::new);
		consumer.accept(BotaniaEntities.MAGIC_LANDMINE, MagicLandmineRenderer::new);
		consumer.accept(BotaniaEntities.MAGIC_MISSILE, NoopRenderer::new);
		consumer.accept(BotaniaEntities.FALLING_STAR, NoopRenderer::new);
		consumer.accept(BotaniaEntities.ENDER_AIR, NoopRenderer::new);
		consumer.accept(BotaniaEntities.THROWN_ITEM, ItemEntityRenderer::new);
		consumer.accept(BotaniaEntities.PIXIE, PixieRenderer::new);
		consumer.accept(BotaniaEntities.DOPPLEGANGER, GaiaGuardianRenderer::new);
		consumer.accept(BotaniaEntities.SPARK, ManaSparkRenderer::new);
		consumer.accept(BotaniaEntities.CORPOREA_SPARK, CorporeaSparkRenderer::new);
		consumer.accept(BotaniaEntities.POOL_MINECART, ManaPoolMinecartRenderer::new);
		consumer.accept(BotaniaEntities.PINK_WITHER, PinkWitherRenderer::new);
		consumer.accept(BotaniaEntities.MANA_STORM, ManaStormRenderer::new);
		consumer.accept(BotaniaEntities.BABYLON_WEAPON, BabylonWeaponRenderer::new);

		consumer.accept(BotaniaEntities.THORN_CHAKRAM, ThrownItemRenderer::new);
		consumer.accept(BotaniaEntities.VINE_BALL, ThrownItemRenderer::new);
		consumer.accept(BotaniaEntities.ENDER_AIR_BOTTLE, ThrownItemRenderer::new);
	}

	public static void addAuxiliaryPlayerRenders(PlayerRenderer renderer,
			Consumer<RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>> consumer) {
		consumer.accept(new ContributorFancinessHandler(renderer));
		consumer.accept(new ManaTabletRenderHandler(renderer));
		consumer.accept(new TerrasteelHelmetLayer(renderer));
	}

	public interface BERConsumer {
		<E extends BlockEntity> void register(BlockEntityType<E> type, BlockEntityRendererProvider<? super E> factory);
	}

	public static void registerBlockEntityRenderers(BERConsumer consumer) {
		consumer.register(BotaniaBlockEntities.ALTAR, PetalApothecaryBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.SPREADER, ManaSpreaderBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.POOL, ManaPoolBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.RUNE_ALTAR, RunicAltarBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.PYLON, PylonBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.ENCHANTER, ManaEnchanterBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.ALF_PORTAL, AlfheimPortalBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.MINI_ISLAND, FloatingFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.TINY_POTATO, TinyPotatoBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.STARFIELD, StarfieldCreatorBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.BREWERY, BotanicalBreweryBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.TERRA_PLATE, TerrestrialAgglomerationPlateBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.RED_STRING_COMPARATOR, RedStringBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.RED_STRING_CONTAINER, RedStringBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.RED_STRING_DISPENSER, RedStringBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.RED_STRING_FERTILIZER, RedStringBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.RED_STRING_INTERCEPTOR, RedStringBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.RED_STRING_RELAY, RedStringBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.PRISM, ManaPrismBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.CORPOREA_INDEX, CorporeaIndexBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.PUMP, ManaPumpBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.CORPOREA_CRYSTAL_CUBE, CorporeaCrystalCubeBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.INCENSE_PLATE, IncensePlateBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.HOURGLASS, HoveringHourglassBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.SPARK_CHANGER, SparkTinkererBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.COCOON, CocoonBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.LIGHT_RELAY, LuminizerBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.BELLOWS, BellowsBlockEntityRenderer::new);
		@SuppressWarnings("unchecked")
		BlockEntityRendererProvider<GaiaHeadBlockEntity> gaia = ctx -> (BlockEntityRenderer<GaiaHeadBlockEntity>) (BlockEntityRenderer<?>) new GaiaHeadBlockEntityRenderer(ctx);
		consumer.register(BotaniaBlockEntities.GAIA_HEAD, gaia);
		consumer.register(BotaniaBlockEntities.TERU_TERU_BOZU, TeruTeruBozuBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.AVATAR, AvatarBlockEntityRenderer::new);
		consumer.register(BotaniaBlockEntities.ANIMATED_TORCH, AnimatedTorchBlockEntityRenderer::new);

		consumer.register(BotaniaFlowerBlocks.PURE_DAISY, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.MANASTAR, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.HYDROANGEAS, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.ENDOFLAME, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.THERMALILY, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.ROSA_ARCANA, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.MUNCHDEW, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.ENTROPINNYUM, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.KEKIMURUS, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.GOURMARYLLIS, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.NARSLIMMUS, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.SPECTROLUS, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.DANDELIFEON, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.RAFFLOWSIA, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.SHULK_ME_NOT, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.BELLETHORNE, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.BELLETHORNE_CHIBI, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.BERGAMUTE, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.DREADTHORN, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.HEISEI_DREAM, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.TIGERSEYE, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.JADED_AMARANTHUS, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.ORECHID, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.FALLEN_KANADE, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.EXOFLAME, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.AGRICARNATION, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.AGRICARNATION_CHIBI, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.HOPPERHOCK, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.HOPPERHOCK_CHIBI, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.TANGLEBERRIE, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.TANGLEBERRIE_CHIBI, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.JIYUULIA, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.JIYUULIA_CHIBI, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.RANNUNCARPUS, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.RANNUNCARPUS_CHIBI, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.HYACIDUS, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.POLLIDISIAC, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.CLAYCONIA, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.CLAYCONIA_CHIBI, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.LOONIUM, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.DAFFOMILL, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.VINCULOTUS, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.SPECTRANTHEMUM, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.MEDUMONE, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.MARIMORPHOSIS, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.MARIMORPHOSIS_CHIBI, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.BUBBELL, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.BUBBELL_CHIBI, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.SOLEGNOLIA, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.SOLEGNOLIA_CHIBI, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.ORECHID_IGNEM, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(BotaniaFlowerBlocks.LABELLIA, SpecialFlowerBlockEntityRenderer::new);
	}

	public static final Map<Block, Function<Block, BlockEntityItemRenderer>> BE_ITEM_RENDERER_FACTORIES = Map.of(
			BotaniaBlocks.manaPylon, PylonBlockEntityRenderer.ItemRenderer::new,
			BotaniaBlocks.naturaPylon, PylonBlockEntityRenderer.ItemRenderer::new,
			BotaniaBlocks.gaiaPylon, PylonBlockEntityRenderer.ItemRenderer::new,
			BotaniaBlocks.teruTeruBozu, BlockEntityItemRenderer::new,
			BotaniaBlocks.avatar, BlockEntityItemRenderer::new,
			BotaniaBlocks.bellows, BlockEntityItemRenderer::new,
			BotaniaBlocks.brewery, BlockEntityItemRenderer::new,
			BotaniaBlocks.corporeaIndex, BlockEntityItemRenderer::new,
			BotaniaBlocks.hourglass, BlockEntityItemRenderer::new
	);

	private EntityRenderers() {}
}
