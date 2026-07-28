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

}
