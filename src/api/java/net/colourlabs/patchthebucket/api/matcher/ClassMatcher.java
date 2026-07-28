package net.colourlabs.patchthebucket.api.matcher;

/**
 * Matches classes by name, superclass, or interface.
 *
 * <pre>{@code
 * ClassMatcher.name("some.plugin.Class")
 * ClassMatcher.subclassOf("some.plugin.BaseClass")
 * ClassMatcher.implementsInterface("some.plugin.ApiInterface")
 * }</pre>
 */
public final class ClassMatcher {
    public static ClassMatcher name(String className) {
        return new ClassMatcher(className, Kind.EXACT, null);
    }

    public static ClassMatcher subclassOf(String superClass) {
        return new ClassMatcher(null, Kind.SUBCLASS, superClass);
    }

    public static ClassMatcher implementsInterface(String interfaceName) {
        return new ClassMatcher(null, Kind.INTERFACE, interfaceName);
    }

    private final String className;
    private final Kind kind;
    private final String target;

    private ClassMatcher(String className, Kind kind, String target) {
        this.className = className;
        this.kind = kind;
        this.target = target;
    }

    public String className() {
        return className;
    }

    public Kind kind() {
        return kind;
    }

    public String target() {
        return target;
    }

    public enum Kind { EXACT, SUBCLASS, INTERFACE }
}
