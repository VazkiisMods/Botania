package vazkii.botania.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A purely-documentative annotation.
 * This annotation is used by developers in xplat code. The annotated method is intended
 * to "soft implement" a certain method in a loader specific interface that cannot be
 * named in xplat code and thus cannot be checked with @Override.
 * In this context, "soft implement" means to implement the method by matching the signature
 * with the intended interface method.
 * Examples of interfaces that we would use this for is IItemExtension or FabricItem.
 *
 * The intent is that we audit such sites every major Minecraft version or so, to ensure
 * that they still properly override the intended target.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface SoftImplement {
	/**
	 * What interface we're soft implementing
	 */
	String value();
}
