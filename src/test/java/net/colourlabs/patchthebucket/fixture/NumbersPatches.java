package net.colourlabs.patchthebucket.fixture;

import net.colourlabs.patchthebucket.api.annotation.At;
import net.colourlabs.patchthebucket.api.annotation.Inject;
import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

@TargetClass("net.colourlabs.patchthebucket.fixture.Numbers")
public class NumbersPatches {
    private static InsnList track() {
        InsnList insns = new InsnList();
        insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                "net/colourlabs/patchthebucket/fixture/Track", "record", "()V", false));
        return insns;
    }

    @Inject(method = "sq", at = @At("RETURN"))
    public static InsnList beforeSqReturn(MethodNode method) {
        InsnList insns = track();
        insns.add(new InsnNode(Opcodes.POP));
        insns.add(new InsnNode(Opcodes.ICONST_0));
        return insns;
    }

    @Inject(method = "identity", at = @At("RETURN"))
    public static InsnList beforeIdentityReturn(MethodNode method) {
        InsnList insns = track();
        insns.add(new InsnNode(Opcodes.POP2));
        insns.add(new InsnNode(Opcodes.LCONST_0));
        return insns;
    }

    @Inject(method = "half", at = @At("RETURN"))
    public static InsnList beforeHalfReturn(MethodNode method) {
        InsnList insns = track();
        insns.add(new InsnNode(Opcodes.POP));
        insns.add(new InsnNode(Opcodes.FCONST_0));
        return insns;
    }

    @Inject(method = "magnitude", at = @At("RETURN"))
    public static InsnList beforeMagnitudeReturn(MethodNode method) {
        InsnList insns = track();
        insns.add(new InsnNode(Opcodes.POP2));
        insns.add(new InsnNode(Opcodes.DCONST_0));
        return insns;
    }

    @Inject(method = "echo", at = @At("RETURN"))
    public static InsnList beforeEchoReturn(MethodNode method) {
        InsnList insns = track();
        insns.add(new InsnNode(Opcodes.POP));
        insns.add(new InsnNode(Opcodes.ACONST_NULL));
        return insns;
    }

    @Inject(method = "nothing", at = @At("RETURN"))
    public static InsnList beforeNothingReturn(MethodNode method) {
        return track();
    }
}
