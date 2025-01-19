package vazkii.botania.common.internal_caps;

import net.minecraft.nbt.CompoundTag;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.annotations.SoftImplement;

public abstract class SerializableComponent {
	// Fabric CCA interface
	@SoftImplement("Component")
	public abstract void readFromNbt(CompoundTag tag);

	@SoftImplement("Component")
	public abstract void writeToNbt(CompoundTag tag);

	// NeoForge interface
	@NotNull
	@SoftImplement("INBTSerializer<CompoundTag>")
	public final CompoundTag serializeNBT() {
		var ret = new CompoundTag();
		writeToNbt(ret);
		return ret;
	}

	@SoftImplement("INBTSerializer<CompoundTag>")
	public final void deserializeNBT(@NotNull CompoundTag nbt) {
		readFromNbt(nbt);
	}
}
