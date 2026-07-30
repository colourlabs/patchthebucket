package net.colourlabs.patchthebucket.api.inject;

import org.objectweb.asm.tree.MethodNode;

/**
 * An injector inserts instructions at a specific {@link InjectionPoint}
 * within a target method.
 *
 * <p>Created via an {@link InjectorBuilder} to keep construction readable.
 */
@FunctionalInterface
public interface Injector {
    /**
     * Produce the instructions to insert at the matched point.
     *
     * @param methodNode the target method being transformed
     * @return the instruction list to splice in
     */
    org.objectweb.asm.tree.InsnList instructions(MethodNode methodNode);
}
