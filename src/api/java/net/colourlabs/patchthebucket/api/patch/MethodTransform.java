package net.colourlabs.patchthebucket.api.patch;

import org.objectweb.asm.tree.MethodNode;

@FunctionalInterface
public interface MethodTransform {
    void transform(MethodNode methodNode);
}
