package vazkii.botania.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public record WandOfTheForestComponent(WandMode wandMode, TooltipVisibility tooltipVisibility) implements AdvancedTooltipProvider {
	public static Codec<WandOfTheForestComponent> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					WandMode.CODEC.optionalFieldOf("mode", WandMode.BIND).forGetter(WandOfTheForestComponent::wandMode),
					TooltipVisibility.CODEC.optionalFieldOf("tooltip_visibility", TooltipVisibility.IN_NAME).forGetter(WandOfTheForestComponent::tooltipVisibility)
			).apply(instance, WandOfTheForestComponent::new)
	);
	public static StreamCodec<ByteBuf, WandOfTheForestComponent> STREAM_CODEC = StreamCodec.composite(
		WandMode.STREAM_CODEC, WandOfTheForestComponent::wandMode,
		TooltipVisibility.STREAM_CODEC, WandOfTheForestComponent::tooltipVisibility,
		WandOfTheForestComponent::new
	);

	@Override
	public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag, List<Component> fullTooltip) {
		if (tooltipVisibility == TooltipVisibility.IN_NAME) {
			MutableComponent name = fullTooltip.get(0).copy();
			Style style = name.getStyle();
			name.append(" (");
			name.append(wandMode.getDisplay());
			// TODO: 1.21 this probably has the wrong style
			name.append(")");
			fullTooltip.set(0, name);
		} else if (tooltipVisibility == TooltipVisibility.IN_LIST) {
			tooltipAdder.accept(wandMode.getDisplay());
		}
	}

	public enum WandMode implements StringRepresentable {
		BIND(0, "bind", "botaniamisc.wandMode.bind"),
		FUNCTION(1, "function", "botaniamisc.wandMode.function");

		public static final Codec<WandMode> CODEC = StringRepresentable.fromValues(WandMode::values);
		public static final IntFunction<WandMode> BY_ID = ByIdMap.continuous(mode -> mode.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
		public static final StreamCodec<ByteBuf, WandMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, mode -> mode.id);
		private final int id;
		private final String name;
		private final Component display;

		WandMode(int id, String name, String translationKey) {
			this.id = id;
			this.name = name;
			this.display = Component.translatable(translationKey).withStyle(ChatFormatting.DARK_GREEN);
		}

		@Override
		public @NotNull String getSerializedName() {
			return name;
		}

		public Component getDisplay() {
			return display;
		}
	}
}
