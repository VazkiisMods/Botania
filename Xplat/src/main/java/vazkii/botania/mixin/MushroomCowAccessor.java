package vazkii.botania.mixin;

import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.item.component.SuspiciousStewEffects;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MushroomCow.class)
public interface MushroomCowAccessor {
	@Accessor
	@Nullable
	SuspiciousStewEffects getStewEffects();

	@Accessor
	void setStewEffects(@Nullable SuspiciousStewEffects stewEffects);
}
