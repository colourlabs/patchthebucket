package net.colourlabs.patchthebucket.api.patch;

import org.objectweb.asm.tree.ClassNode;

public interface ClassPatch {
    String targetClassName();

    void apply(ClassNode classNode);
}
