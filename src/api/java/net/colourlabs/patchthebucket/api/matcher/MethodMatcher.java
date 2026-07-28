package net.colourlabs.patchthebucket.api.matcher;

/**
 * Matches methods by name, descriptor, or annotation presence.
 *
 * <pre>{@code
 * MethodMatcher.name("onCommand")
 * MethodMatcher.name("add", "(II)I")
 * MethodMatcher.annotatedWith(Deprecated.class)
 * }</pre>
 */
public final class MethodMatcher {
    public static MethodMatcher name(String name) {
        return new MethodMatcher(name, null, null);
    }

    public static MethodMatcher name(String name, String descriptor) {
        return new MethodMatcher(name, descriptor, null);
    }

    public static MethodMatcher annotatedWith(Class<?> annotation) {
        return new MethodMatcher(null, null, annotation);
    }

    private final String methodName;
    private final String descriptor;
    private final Class<?> annotation;

    private MethodMatcher(String methodName, String descriptor, Class<?> annotation) {
        this.methodName = methodName;
        this.descriptor = descriptor;
        this.annotation = annotation;
    }

    public String methodName() {
        return methodName;
    }

    public String descriptor() {
        return descriptor;
    }

    public Class<?> annotation() {
        return annotation;
    }
}
