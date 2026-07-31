package net.colourlabs.patchthebucket.fixture;

import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import net.colourlabs.patchthebucket.api.annotation.TransformMethod;
import org.objectweb.asm.tree.MethodNode;

@TargetClass("net.colourlabs.patchthebucket.fixture.Calculator")
public class NonStaticPatchPatches {
    @TransformMethod("add")
    public void notStatic(MethodNode method) {}
}
