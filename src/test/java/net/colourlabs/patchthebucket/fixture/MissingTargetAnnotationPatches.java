package net.colourlabs.patchthebucket.fixture;

import net.colourlabs.patchthebucket.api.annotation.TransformMethod;
import org.objectweb.asm.tree.MethodNode;

public class MissingTargetAnnotationPatches {
    @TransformMethod("add")
    public static void transform(MethodNode method) {}
}
