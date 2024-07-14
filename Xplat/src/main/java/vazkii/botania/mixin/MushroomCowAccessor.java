package vazkii.botania.mixin;

import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.level.block.SuspiciousEffectHolder;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(MushroomCow.class)
public interface MushroomCowAccessor {
	@Accessor
	@Nullable
	List<SuspiciousEffectHolder.EffectEntry> getStewEffects();

	@Accessor
	void setStewEffects(@Nullable List<SuspiciousEffectHolder.EffectEntry> stewEffects);
}
