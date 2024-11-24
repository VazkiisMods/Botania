package vazkii.botania.api.configdata;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.entity.EntityType;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class LooniumMobSpawnData extends WeightedEntry.IntrusiveBase {
	public static final Codec<LooniumMobSpawnData> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(msd -> msd.type),
					Weight.CODEC.fieldOf("weight").forGetter(IntrusiveBase::getWeight),
					Codec.BOOL.optionalFieldOf("spawnAsBaby").forGetter(msd -> Optional.ofNullable(msd.spawnAsBaby)),
					CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(msd -> Optional.ofNullable(msd.nbt)),
					ResourceLocation.CODEC.optionalFieldOf("equipmentTable")
							.forGetter(msd -> Optional.ofNullable(msd.equipmentTable)),
					Codec.list(LooniumMobEffectToApply.CODEC)
							.optionalFieldOf("effectsToApply")
							.forGetter(msd -> Optional.ofNullable(msd.effectsToApply)),
					Codec.list(LooniumMobAttributeModifier.CODEC)
							.optionalFieldOf("attributeModifiers")
							.forGetter(msd -> Optional.ofNullable(msd.attributeModifiers))
			).apply(instance, LooniumMobSpawnData::create)
	);

	public final EntityType<?> type;
	public final Boolean spawnAsBaby;
	public final CompoundTag nbt;
	public final ResourceLocation equipmentTable;
	public final List<LooniumMobEffectToApply> effectsToApply;
	public final List<LooniumMobAttributeModifier> attributeModifiers;

	private LooniumMobSpawnData(EntityType<?> type, Weight weight, Boolean spawnAsBaby, @Nullable CompoundTag nbt,
			@Nullable ResourceLocation equipmentTable,
			@Nullable List<LooniumMobEffectToApply> effectsToApply,
			@Nullable List<LooniumMobAttributeModifier> attributeModifiers) {
		super(weight);
		this.type = type;
		this.spawnAsBaby = spawnAsBaby;
		this.nbt = nbt != null ? nbt.copy() : null;
		this.equipmentTable = equipmentTable;
		this.effectsToApply = effectsToApply != null ? ImmutableList.copyOf(effectsToApply) : null;
		this.attributeModifiers = attributeModifiers != null ? ImmutableList.copyOf(attributeModifiers) : null;
	}

	public static Builder entityWeight(EntityType<?> type, int weight) {
		return new Builder(type, weight);
	}

	@Override
	public String toString() {
		return "MobSpawnData{" +
				"type=" + type +
				", spawnAsBaby=" + spawnAsBaby +
				", nbt=" + nbt +
				", equipmentTable=" + equipmentTable +
				", effectsToApply=" + effectsToApply +
				", attributeModifiers=" + attributeModifiers +
				'}';
	}

	// Codecs don't support setting null as intentional default value for optional fields, so we do this.
	// (blame com.mojang.datafixers.util.Either::getLeft using Optional::of instead Optional.ofNullable)
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private static LooniumMobSpawnData create(EntityType<?> type, Weight weight,
			Optional<Boolean> spawnAsBaby,
			Optional<CompoundTag> nbt,
			Optional<ResourceLocation> equipmentTable,
			Optional<List<LooniumMobEffectToApply>> effectsToApply,
			Optional<List<LooniumMobAttributeModifier>> attributeModifiers) {
		return new LooniumMobSpawnData(type, weight,
				spawnAsBaby.orElse(null),
				nbt.orElse(null),
				equipmentTable.orElse(null),
				effectsToApply.orElse(null),
				attributeModifiers.orElse(null));
	}

	public static class Builder {
		private final EntityType<?> type;
		private final int weight;
		private @Nullable Boolean spawnAsBaby;
		private @Nullable CompoundTag nbt;
		private @Nullable ResourceLocation equipmentTable;
		private @Nullable List<LooniumMobEffectToApply> effectsToApply;
		private @Nullable List<LooniumMobAttributeModifier> attributeModifiers;

		private Builder(EntityType<?> type, int weight) {
			this.type = type;
			this.weight = weight;
		}

		/**
		 * Make the mob spawn as a baby. (This will not prevent AgeableMobs from growing up.)
		 */
		public Builder spawnAsBaby() {
			this.spawnAsBaby = true;
			return this;
		}

		/**
		 * Force conversion of a baby mob to be reverted. This may have unintended side effects,
		 * like an adult zombie sitting on a chicken or an adult piglin not having a weapon.
		 * The latter case can usually be taken care of via an equipment table.
		 */
		public Builder spawnAsAdult() {
			this.spawnAsBaby = false;
			return this;
		}

		/**
		 * Custom NBT data to apply to the mob before finalizing its spawning.
		 */
		public Builder nbt(CompoundTag nbt) {
			this.nbt = nbt;
			return this;
		}

		/**
		 * A loot table to define equipment to apply to the mob after it spawned.
		 */
		public Builder equipmentTable(ResourceLocation equipmentTable) {
			this.equipmentTable = equipmentTable;
			return this;
		}

		/**
		 * A list of potion effects to apply to the mob.
		 * (These are applied instead of any mob effects from the structure configuration.)
		 */
		public Builder effectsToApply(LooniumMobEffectToApply... effectsToApply) {
			this.effectsToApply = List.of(effectsToApply);
			return this;
		}

		/**
		 * A list of attribute modifiers to apply to the mob.
		 * (These are applied instead of any attribute modifiers from the structure configuration.)
		 */
		public Builder attributeModifiers(LooniumMobAttributeModifier... attributeModifiers) {
			this.attributeModifiers = List.of(attributeModifiers);
			return this;
		}

		public LooniumMobSpawnData build() {
			return new LooniumMobSpawnData(type, Weight.of(weight), spawnAsBaby, nbt, equipmentTable,
					effectsToApply, attributeModifiers);
		}
	}
}
