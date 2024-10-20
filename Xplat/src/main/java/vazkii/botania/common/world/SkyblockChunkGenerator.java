/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class SkyblockChunkGenerator extends NoiseBasedChunkGenerator {
	// [VanillaCopy] NoiseBasedChunkGenerator's codec, but calls our constructor
	public static final Codec<SkyblockChunkGenerator> CODEC = RecordCodecBuilder.create(
			(instance) -> instance.group(
					BiomeSource.CODEC.fieldOf("biome_source").forGetter((gen) -> gen.biomeSource),
					NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::generatorSettings))
					.apply(instance, instance.stable(SkyblockChunkGenerator::new)));

	public static void submitRegistration(BiConsumer<Codec<? extends ChunkGenerator>, ResourceLocation> consumer) {
		consumer.accept(SkyblockChunkGenerator.CODEC, botaniaRL("skyblock"));
	}

	public static boolean isWorldSkyblock(Level world) {
		return world.getChunkSource() instanceof ServerChunkCache chunkCache && chunkCache.getGenerator() instanceof SkyblockChunkGenerator;
	}

	private SkyblockChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
		super(biomeSource, settings);
	}

	@NotNull
	@Override
	protected Codec<? extends ChunkGenerator> codec() {
		return CODEC;
	}

	@Override
	public void buildSurface(@NotNull ChunkAccess chunkAccess, @NotNull WorldGenerationContext context,
			@NotNull RandomState randomState, @NotNull StructureManager structureManager,
			@NotNull BiomeManager biomeManager, @NotNull Registry<Biome> biomes, @NotNull Blender blender) {}

	@Override
	public void applyCarvers(@NotNull WorldGenRegion worldGenRegion, long seed, @NotNull RandomState randomState,
			@NotNull BiomeManager biomeManager, @NotNull StructureManager structureManager,
			@NotNull ChunkAccess chunkAccess, GenerationStep.@NotNull Carving carving) {}

	@Override
	public @NotNull CompletableFuture<ChunkAccess> fillFromNoise(@NotNull Executor executor, @NotNull Blender blender,
			@NotNull RandomState randomState, @NotNull StructureManager structureManager, @NotNull ChunkAccess chunk) {
		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public void spawnOriginalMobs(@NotNull WorldGenRegion region) {}

	@Override
	public void applyBiomeDecoration(@NotNull WorldGenLevel level, @NotNull ChunkAccess chunkAccess,
			@NotNull StructureManager structureManager) {}

	/**
	 * Calculates the height at a particular location as it would be in a world with the same seed that isn't empty.
	 * (Based on code from CarpetSkyAdditions.)
	 */
	public int getBaseHeightInEquivalentNoiseWorld(int x, int z, Heightmap.Types heightmap, WorldGenLevel level) {
		RandomState randomState = RandomState.create(
				generatorSettings().value(),
				level.registryAccess().registryOrThrow(Registries.NOISE).asLookup(),
				level.getSeed());
		return super.getBaseHeight(x, z, heightmap, level, randomState);
	}
}
