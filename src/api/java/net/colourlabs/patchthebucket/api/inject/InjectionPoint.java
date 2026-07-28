package net.colourlabs.patchthebucket.api.inject;

/**
 * Defines where code should be injected into a method.
 */
public final class InjectionPoint {
    public static InjectionPoint atHead() {
        return new InjectionPoint(Location.HEAD, null, null);
    }

    public static InjectionPoint atReturn() {
        return new InjectionPoint(Location.RETURN, null, null);
    }

    public static InjectionPoint beforeInvoke(String owner, String name, String descriptor) {
        return new InjectionPoint(Location.BEFORE_INVOKE, new MethodRef(owner, name, descriptor), null);
    }

    public static InjectionPoint afterInvoke(String owner, String name, String descriptor) {
        return new InjectionPoint(Location.AFTER_INVOKE, new MethodRef(owner, name, descriptor), null);
    }

    public static InjectionPoint onFieldAccess(String owner, String name, String descriptor) {
        return new InjectionPoint(Location.FIELD_ACCESS, new MethodRef(owner, name, descriptor), null);
    }

    private final Location location;
    private final MethodRef target;
    private final MethodRef replacement;

    private InjectionPoint(Location location, MethodRef target, MethodRef replacement) {
        this.location = location;
        this.target = target;
        this.replacement = replacement;
    }

    public Location location() {
        return location;
    }

    public MethodRef target() {
        return target;
    }

    public MethodRef replacement() {
        return replacement;
    }

    public enum Location {
        HEAD,
        RETURN,
        BEFORE_INVOKE,
        AFTER_INVOKE,
        FIELD_ACCESS
    }

    public static final class MethodRef {
        private final String owner;
        private final String name;
        private final String descriptor;

        public MethodRef(String owner, String name, String descriptor) {
            this.owner = owner;
            this.name = name;
            this.descriptor = descriptor;
        }

        public String owner() { return owner; }
        public String name() { return name; }
        public String descriptor() { return descriptor; }
    }
}
