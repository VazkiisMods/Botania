package vazkii.botania.common.handler;

import net.minecraft.world.level.ItemLike;

import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.common.item.BotaniaItems;

import java.util.function.BiConsumer;

public class CompostingData {
	public static void init(BiConsumer<ItemLike, Float> registrationMethod) {
		// common vanilla composting chances:
		final float chanceLowest = 0.3f;
		final float chanceLow = 0.5f;
		final float chanceMid = 0.65f;
		final float chanceHigh = 0.85f;
		// unused here: final float chanceHighest = 1.0f;

		// see https://github.com/VazkiiMods/Botania/issues/4263#issuecomment-1529130978
		ColorHelper.supportedColors().forEach(dyeColor -> {
			registrationMethod.accept(BotaniaItems.getPetal(dyeColor), chanceLowest);
			registrationMethod.accept(BotaniaBlocks.getPetalBlock(dyeColor), chanceLow);
			registrationMethod.accept(BotaniaBlocks.getFlower(dyeColor), chanceMid);
			registrationMethod.accept(BotaniaBlocks.getDoubleFlower(dyeColor), chanceMid);
			registrationMethod.accept(BotaniaBlocks.getMushroom(dyeColor), chanceMid);
		});

		registrationMethod.accept(BotaniaBlocks.cellBlock, chanceHigh);
	}
}
