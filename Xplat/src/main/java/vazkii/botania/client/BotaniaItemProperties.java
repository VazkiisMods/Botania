package vazkii.botania.client;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import vazkii.botania.client.core.proxy.ClientProxy;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.mana.ManaPoolBlock;
import vazkii.botania.common.component.BotaniaDataComponents;
import vazkii.botania.common.item.*;
import vazkii.botania.common.item.brew.BaseBrewItem;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraTruncatorItem;
import vazkii.botania.common.item.relic.FruitOfGrisaiaItem;
import vazkii.botania.common.item.rod.SkiesRodItem;
import vazkii.botania.network.TriConsumer;

import java.util.Locale;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public final class BotaniaItemProperties {
	public static void init(TriConsumer<ItemLike, ResourceLocation, ClampedItemPropertyFunction> consumer) {
		consumer.accept(BotaniaItems.baubleBox, botaniaRL("open"),
				(stack, world, entity, seed) -> stack.has(BotaniaDataComponents.ACTIVE_TRANSIENT) ? 1 : 0);
		consumer.accept(BotaniaItems.blackHoleTalisman, botaniaRL("active"),
				(stack, world, entity, seed) -> stack.has(BotaniaDataComponents.ACTIVE) ? 1 : 0);
		consumer.accept(BotaniaItems.manaBottle, botaniaRL("swigs_taken"),
				(stack, world, entity, seed) -> BottledManaItem.SWIGS - BottledManaItem.getSwigsLeft(stack));

		ResourceLocation vuvuzelaId = botaniaRL("vuvuzela");
		ClampedItemPropertyFunction isVuvuzela = (stack, world, entity, seed) -> stack.getHoverName().getString().toLowerCase(Locale.ROOT).contains("vuvuzela") ? 1 : 0;
		consumer.accept(BotaniaItems.grassHorn, vuvuzelaId, isVuvuzela);
		consumer.accept(BotaniaItems.leavesHorn, vuvuzelaId, isVuvuzela);
		consumer.accept(BotaniaItems.snowHorn, vuvuzelaId, isVuvuzela);

		consumer.accept(BotaniaItems.lexicon, botaniaRL("elven"), (stack, world, living, seed) -> LexicaBotaniaItem.isElven(stack) ? 1 : 0);
		consumer.accept(BotaniaItems.manaCookie, botaniaRL("totalbiscuit"),
				(stack, world, entity, seed) -> stack.getHoverName().getString().toLowerCase(Locale.ROOT).contains("totalbiscuit") ? 1 : 0);
		consumer.accept(BotaniaItems.slimeBottle, botaniaRL("active"),
				(stack, world, entity, seed) -> stack.has(BotaniaDataComponents.ACTIVE_TRANSIENT) ? 1 : 0);
		consumer.accept(BotaniaItems.spawnerMover, botaniaRL("full"),
				(stack, world, entity, seed) -> LifeAggregatorItem.hasData(stack) ? 1 : 0);
		consumer.accept(BotaniaItems.temperanceStone, botaniaRL("active"),
				(stack, world, entity, seed) -> stack.has(BotaniaDataComponents.ACTIVE) ? 1 : 0);
		ClampedItemPropertyFunction wandBindModeProperty =
				(stack, world, entity, seed) -> WandOfTheForestItem.getBindMode(stack) ? 1 : 0;
		consumer.accept(BotaniaItems.twigWand, botaniaRL("bindmode"), wandBindModeProperty);
		consumer.accept(BotaniaItems.dreamwoodWand, botaniaRL("bindmode"), wandBindModeProperty);
		consumer.accept(BotaniaItems.autocraftingHalo, botaniaRL("active"),
				(stack, world, entity, seed) -> stack.has(BotaniaDataComponents.ACTIVE) ? 1 : 0);

		ResourceLocation poolFullId = botaniaRL("full");
		ClampedItemPropertyFunction poolFull = (stack, world, entity, seed) -> {
			Block block = ((BlockItem) stack.getItem()).getBlock();
			boolean renderFull = ((ManaPoolBlock) block).variant == ManaPoolBlock.Variant.CREATIVE || stack.has(BotaniaDataComponents.RENDER_FULL);
			return renderFull ? 1 : 0;
		};
		consumer.accept(BotaniaBlocks.manaPool, poolFullId, poolFull);
		consumer.accept(BotaniaBlocks.dilutedPool, poolFullId, poolFull);
		consumer.accept(BotaniaBlocks.creativePool, poolFullId, poolFull);
		consumer.accept(BotaniaBlocks.fabulousPool, poolFullId, poolFull);

		ClampedItemPropertyFunction brewGetter = (stack, world, entity, seed) -> {
			BaseBrewItem item = ((BaseBrewItem) stack.getItem());
			return item.getSwigs(stack) - item.getSwigsLeft(stack);
		};
		consumer.accept(BotaniaItems.brewVial, botaniaRL("swigs_taken"), brewGetter);
		consumer.accept(BotaniaItems.brewFlask, botaniaRL("swigs_taken"), brewGetter);

		ResourceLocation holidayId = botaniaRL("holiday");
		ClampedItemPropertyFunction holidayGetter = (stack, worldIn, entityIn, seed) -> ClientProxy.jingleTheBells ? 1 : 0;
		consumer.accept(BotaniaItems.manaweaveHelm, holidayId, holidayGetter);
		consumer.accept(BotaniaItems.manaweaveChest, holidayId, holidayGetter);
		consumer.accept(BotaniaItems.manaweaveBoots, holidayId, holidayGetter);
		consumer.accept(BotaniaItems.manaweaveLegs, holidayId, holidayGetter);

		ClampedItemPropertyFunction ringOnGetter = (stack, worldIn, entityIn, seed) -> {
			if (entityIn instanceof Player player) {
				if (player.getCooldowns().isOnCooldown(stack.getItem())) {
					return 0;
				} else {
					return 1;
				}
			} else {
				return 0;
			}
		};
		consumer.accept(BotaniaItems.magnetRing, botaniaRL("active"), ringOnGetter);
		consumer.accept(BotaniaItems.magnetRingGreater, botaniaRL("active"), ringOnGetter);

		consumer.accept(BotaniaItems.elementiumShears, botaniaRL("reddit"),
				(stack, world, entity, seed) -> stack.getHoverName().getString().equalsIgnoreCase("dammit reddit") ? 1F : 0F);
		consumer.accept(BotaniaItems.manasteelSword, botaniaRL("elucidator"),
				(stack, world, entity, seed) -> "the elucidator".equals(stack.getHoverName().getString().toLowerCase(Locale.ROOT).trim()) ? 1 : 0);
		consumer.accept(BotaniaItems.terraAxe, botaniaRL("active"),
				(stack, world, entity, seed) -> entity instanceof Player player && !TerraTruncatorItem.shouldBreak(player) ? 0 : 1);
		consumer.accept(BotaniaItems.terraPick, botaniaRL("tipped"),
				(stack, world, entity, seed) -> TerraShattererItem.isTipped(stack) ? 1 : 0);
		consumer.accept(BotaniaItems.terraPick, botaniaRL("active"),
				(stack, world, entity, seed) -> TerraShattererItem.isEnabled(stack) ? 1 : 0);
		consumer.accept(BotaniaItems.infiniteFruit, botaniaRL("boot"),
				(stack, worldIn, entity, seed) -> FruitOfGrisaiaItem.isBoot(stack) ? 1F : 0F);
		consumer.accept(BotaniaItems.tornadoRod, botaniaRL("active"),
				(stack, world, living, seed) -> SkiesRodItem.isFlying(stack) ? 1 : 0);

		// [VanillaCopy] ItemProperties.BOW's minecraft:pulling and minecraft:pull properties
		ClampedItemPropertyFunction pulling = (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
		ClampedItemPropertyFunction pull = (stack, level, entity, seed) -> {
			if (entity == null) {
				return 0.0F;
			} else {
				return entity.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
			}
		};
		consumer.accept(BotaniaItems.livingwoodBow, ResourceLocation.withDefaultNamespace("pulling"), pulling);
		consumer.accept(BotaniaItems.livingwoodBow, ResourceLocation.withDefaultNamespace("pull"), pull);
		consumer.accept(BotaniaItems.crystalBow, ResourceLocation.withDefaultNamespace("pulling"), pulling);
		consumer.accept(BotaniaItems.crystalBow, ResourceLocation.withDefaultNamespace("pull"), pull);
	}

	private BotaniaItemProperties() {}
}
