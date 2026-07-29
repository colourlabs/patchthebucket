package net.colourlabs.patchthebucket.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a patch container. All {@link TransformMethod} and
 * {@link Inject} methods within will be discovered and applied.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TargetClass {

    /**
     * Fully-qualified class name to patch
     * (e.g. "net.colourlabs.testplugin.services.CalculatorService").
     */
    String value();

    /**
     * Whether to compute stack-map frames. Disable when the target class
     * references plugin-internal types invisible to the agent classloader,
     * which would otherwise throw TypeNotPresentException during frame
     * computation.
     * <p>
     * When true, ASM's COMPUTE_FRAMES is used with a fallback to
     * {@code java/lang/Object} for unresolvable types. When false,
     * COMPUTE_MAXS is used instead (avoids frame computation entirely).
     */
    boolean computeFrames() default true;

}
