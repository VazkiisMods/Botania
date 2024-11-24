package vazkii.botania.api.configdata;

import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class LooniumMobAttributeModifier {
	public static final Codec<LooniumMobAttributeModifier> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Codec.STRING.fieldOf("name").forGetter(mam -> mam.name),
					BuiltInRegistries.ATTRIBUTE.byNameCodec().fieldOf("attribute").forGetter(mam -> mam.attribute),
					Codec.DOUBLE.fieldOf("amount").forGetter(mam -> mam.amount),
					Codec.STRING.xmap(LooniumMobAttributeModifier::operationFromString,
							LooniumMobAttributeModifier::operationToString)
							.fieldOf("operation").forGetter(mam -> mam.operation)
			).apply(instance, LooniumMobAttributeModifier::new)
	);

	private final String name;
	public final Attribute attribute;
	private final double amount;
	private final AttributeModifier.Operation operation;

	public LooniumMobAttributeModifier(String name, Attribute attribute, double amount,
			AttributeModifier.Operation operation) {
		this.name = name;
		this.attribute = attribute;
		this.amount = amount;
		this.operation = operation;
	}

	public AttributeModifier createAttributeModifier() {
		return new AttributeModifier(name, amount, operation);
	}

	private static String operationToString(AttributeModifier.Operation operation) {
		return switch (operation) {
			case ADDITION -> "addition";
			case MULTIPLY_BASE -> "multiply_base";
			case MULTIPLY_TOTAL -> "multiply_total";
			default -> throw new IllegalArgumentException("Unknown operation " + operation);
		};
	}

	private static AttributeModifier.Operation operationFromString(String operation) {
		return switch (operation) {
			case "addition" -> AttributeModifier.Operation.ADDITION;
			case "multiply_base" -> AttributeModifier.Operation.MULTIPLY_BASE;
			case "multiply_total" -> AttributeModifier.Operation.MULTIPLY_TOTAL;
			default -> throw new JsonSyntaxException("Unknown attribute modifier operation " + operation);
		};
	}

	@Override
	public String toString() {
		return "MobAttributeModifier{" +
				"name='" + name + '\'' +
				", attribute=" + attribute +
				", amount=" + amount +
				", operation=" + operation +
				'}';
	}
}
