package vazkii.botania.common.component;

import com.mojang.serialization.Codec;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

import io.netty.buffer.ByteBuf;

public enum TooltipVisibility implements StringRepresentable {
	HIDDEN(0, "hidden"),
	IN_NAME(1, "in_name"),
	IN_LIST(2, "in_list");

	private final int id;
	private final String name;
	public static final Codec<TooltipVisibility> CODEC = StringRepresentable.fromValues(TooltipVisibility::values);
	public static final IntFunction<TooltipVisibility> BY_ID = ByIdMap.continuous(visibility -> visibility.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
	public static final StreamCodec<ByteBuf, TooltipVisibility> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, visibility -> visibility.id);

	TooltipVisibility(int id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public @NotNull String getSerializedName() {
		return name;
	}
}
