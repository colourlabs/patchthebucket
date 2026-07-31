package net.colourlabs.patchthebucket.fixture;

import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import net.colourlabs.patchthebucket.api.annotation.TransformMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

@TargetClass("net.colourlabs.patchthebucket.fixture.Calculator")
public class CalculatorPatches {
    @TransformMethod("broken")
    public static void fixBroken(MethodNode method) {
        method.instructions.clear();
        method.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 42));
        method.instructions.add(new InsnNode(Opcodes.IRETURN));
    }
}
