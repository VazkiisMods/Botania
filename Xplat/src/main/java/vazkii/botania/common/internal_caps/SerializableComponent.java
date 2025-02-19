package vazkii.botania.common.internal_caps;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.UnknownNullability;
import vazkii.botania.common.annotations.SoftImplement;

public abstract class SerializableComponent {
	// Fabric CCA interface
	@SoftImplement("org.ladysnake.cca.api.v3.component.Component")
	public abstract void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup);

	@SoftImplement("org.ladysnake.cca.api.v3.component.Component")
	public abstract void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup);

	// NeoForge interface
	@UnknownNullability
	@SoftImplement("net.neoforged.neoforge.common.util.INBTSerializable<CompoundTag>")
	public final CompoundTag serializeNBT(HolderLookup.Provider provider) {
		var ret = new CompoundTag();
		writeToNbt(ret, provider);
		return ret;
	}

	@SoftImplement("net.neoforged.neoforge.common.util.INBTSerializable<CompoundTag>")
	public final void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
		readFromNbt(nbt, provider);
	}
}
