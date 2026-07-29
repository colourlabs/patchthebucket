package net.colourlabs.patchthebucket.api.patch;

import org.objectweb.asm.tree.ClassNode;

public interface ClassPatch {
    String targetClassName();

    boolean computeFrames();

    void apply(ClassNode classNode);
}
