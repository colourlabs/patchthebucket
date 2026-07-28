package net.colourlabs.patchthebucket.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a static method that replaces the body of a target method.
 * The annotated method takes a {@code MethodNode} parameter and modifies it.
 *
 * <pre>{@code
 * @TargetClass("some.plugin.Class")
 * public class MyPatch {
 *     @TransformMethod("methodName")
 *     public static void fixMethod(MethodNode method) {
 *         method.instructions.clear();
 *         // ... rebuild
 *     }
 * }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TransformMethod {

    /** Method name to transform. */
    String value();

    /** Optional method descriptor to narrow the match. */
    String descriptor() default "";

}
