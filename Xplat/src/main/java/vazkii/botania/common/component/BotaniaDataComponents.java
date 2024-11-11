package vazkii.botania.common.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class BotaniaDataComponents {
	private static final Map<String, DataComponentType<?>> toRegister = new HashMap<>();

	public static final DataComponentType<Integer> MAX_MANA = schedule(
			"max_mana",
			builder -> builder.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	public static final DataComponentType<Integer> MANA = schedule(
			"mana",
			builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	public static final DataComponentType<WandOfTheForestComponent> WAND_OF_THE_FOREST = schedule(
			"wand_of_the_forest",
			builder -> builder.persistent(WandOfTheForestComponent.CODEC).networkSynchronized(WandOfTheForestComponent.STREAM_CODEC)
	);

	private static <T> DataComponentType<T> schedule(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
		DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
		toRegister.put(name, type);
		return type;
	};

	public static void registerComponents(BiConsumer<DataComponentType<?>, ResourceLocation> biConsumer) {
		for (Map.Entry<String, DataComponentType<?>> entry : toRegister.entrySet()) {
			biConsumer.accept(entry.getValue(), botaniaRL(entry.getKey()));
		}
	}
}
