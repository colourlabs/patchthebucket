package net.colourlabs.patchthebucket.api.registry;

import net.colourlabs.patchthebucket.api.patch.Patch;
import net.colourlabs.patchthebucket.api.patch.ClassPatch;

/**
 * Registry for class-transforming patches.
 */
public interface PatchRegistry {
    void register(Patch patch);
    void register(ClassPatch patch);

    void registerAndApply(Patch patch);
    void registerAndApply(ClassPatch patch);

    /**
     * Scan a class for {@code @TargetClass} / {@code @TransformMethod} /
     * {@code @Inject} annotations and register the resulting patches.
     */
    void registerAnnotated(Class<?> patchClass);

    void unregister(String targetClassName);
}
