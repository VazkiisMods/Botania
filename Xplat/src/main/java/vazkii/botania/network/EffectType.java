package vazkii.botania.network;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import io.netty.buffer.ByteBuf;

public enum EffectType {
	PAINT_LENS(1), // Arg: EnumDyeColor
	ARENA_INDICATOR(0),
	ITEM_SMOKE(2), // Arg: Entity ID, number of particles
	SPARK_NET_INDICATOR(2), // Arg: Entity ID from, Entity ID towards
	SPARK_MANA_FLOW(3), // Arg: Entity ID from, Entity ID towards, color
	ENCHANTER_DESTROY(0),
	BLACK_LOTUS_DISSOLVE(0),
	TERRA_PLATE(1), // Arg: Completion proportion (transmuted from float)
	FLUGEL_EFFECT(1), // Arg: Entity ID
	PARTICLE_BEAM(3), // Args: dest xyz
	DIVA_EFFECT(1), // Arg: Entity ID
	HALO_CRAFT(1), // Arg: Entity ID
	AVATAR_TORNADO_JUMP(1), // Arg: Entity ID
	AVATAR_TORNADO_BOOST(1), // Arg: Entity ID
	THUNDERCALLER_EFFECT(-1), // Arg: All entity IDs hit by the bolt
	GRASS_SEED_PARTICLES(1) // Arg: Color of the particles
	;

	// If -1, then variable length and the number of arguments is also sent over the network
	public final int argCount;

	public static final StreamCodec<ByteBuf, EffectType> STREAM_CODEC = ByteBufCodecs.VAR_INT
			.map(i -> EffectType.values()[i], EffectType::ordinal);

	EffectType(int argCount) {
		this.argCount = argCount;
	}
}
