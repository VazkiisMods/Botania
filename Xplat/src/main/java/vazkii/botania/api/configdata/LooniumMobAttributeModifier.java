package vazkii.botania.api.configdata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class LooniumMobAttributeModifier {
	public static final Codec<LooniumMobAttributeModifier> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					ResourceLocation.CODEC.fieldOf("id").forGetter(mam -> mam.id),
					BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(mam -> mam.attribute),
					Codec.DOUBLE.fieldOf("amount").forGetter(mam -> mam.amount),
					AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(mam -> mam.operation)
			).apply(instance, LooniumMobAttributeModifier::new)
	);

	private final ResourceLocation id;
	public final Holder<Attribute> attribute;
	private final double amount;
	private final AttributeModifier.Operation operation;

	public LooniumMobAttributeModifier(ResourceLocation id, Holder<Attribute> attribute, double amount, AttributeModifier.Operation operation) {
		this.id = id;
		this.attribute = attribute;
		this.amount = amount;
		this.operation = operation;
	}

	public AttributeModifier createAttributeModifier() {
		return new AttributeModifier(id, amount, operation);
	}

	@Override
	public String toString() {
		return "MobAttributeModifier{" +
				"id='" + id + '\'' +
				", attribute=" + attribute +
				", amount=" + amount +
				", operation=" + operation +
				'}';
	}
}
