package net.colourlabs.patchthebucket.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies where to inject code within a method.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface At {
    /**
     * Injection location:
     * <ul>
     *   <li>{@code "HEAD"} - at the start of the method</li>
     *   <li>{@code "RETURN"} - before every RETURN / ARETURN / IRETURN</li>
     *   <li>{@code "INVOKE"} - before a call to a specific method</li>
     * </ul>
     */
    String value();

    /**
     * Target method/field for INVOKE locations (e.g. "some/Class.methodName").
     */
    String target() default "";

    /**
     * Method descriptor for INVOKE locations.
     */
    String descriptor() default "";
}
