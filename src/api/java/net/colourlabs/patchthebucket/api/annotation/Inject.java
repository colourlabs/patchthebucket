package net.colourlabs.patchthebucket.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Injects code at a specific point in a target method.
 *
 * <pre>{@code
 * @Inject(method = "divide", at = @At("HEAD"))
 * public static void guardDivide(MethodNode method) {
 *     // instructions inserted at the head
 * }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Inject {

    /** Target method name. */
    String method();

    /** Optional descriptor to narrow the match. */
    String descriptor() default "";

    /** Injection point specification. */
    At at();

}
