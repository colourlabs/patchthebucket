package net.colourlabs.patchthebucket.api.matcher;

/**
 * Matches fields by name, type, or annotation.
 *
 * <pre>{@code
 * FieldMatcher.name("counter")
 * FieldMatcher.name("counter", "I")
 * }</pre>
 */
public final class FieldMatcher {
    public static FieldMatcher name(String name) {
        return new FieldMatcher(name, null, null);
    }

    public static FieldMatcher name(String name, String descriptor) {
        return new FieldMatcher(name, descriptor, null);
    }

    private final String fieldName;
    private final String descriptor;
    private final Class<?> annotation;

    private FieldMatcher(String fieldName, String descriptor, Class<?> annotation) {
        this.fieldName = fieldName;
        this.descriptor = descriptor;
        this.annotation = annotation;
    }

    public String fieldName() {
        return fieldName;
    }

    public String descriptor() {
        return descriptor;
    }

    public Class<?> annotation() {
        return annotation;
    }
}
