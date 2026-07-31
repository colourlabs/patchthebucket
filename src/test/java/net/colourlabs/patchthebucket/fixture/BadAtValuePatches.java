package net.colourlabs.patchthebucket.fixture;

import net.colourlabs.patchthebucket.api.annotation.At;
import net.colourlabs.patchthebucket.api.annotation.Inject;
import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

@TargetClass("net.colourlabs.patchthebucket.fixture.Calculator")
public class BadAtValuePatches {
    @Inject(method = "add", at = @At("NOPE"))
    public static InsnList inject(MethodNode method) {
        return null;
    }
}
