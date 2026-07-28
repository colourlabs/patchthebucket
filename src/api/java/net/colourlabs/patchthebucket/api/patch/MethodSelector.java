package net.colourlabs.patchthebucket.api.patch;

public final class MethodSelector {
    private final String name;
    private final String descriptor;

    public MethodSelector(String name) {
        this(name, null);
    }

    public MethodSelector(String name, String descriptor) {
        this.name = name;
        this.descriptor = descriptor;
    }

    public String name() {
        return name;
    }

    public String descriptor() {
        return descriptor;
    }

    public static MethodSelector named(String name) {
        return new MethodSelector(name);
    }

    public static MethodSelector named(String name, String descriptor) {
        return new MethodSelector(name, descriptor);
    }
}
