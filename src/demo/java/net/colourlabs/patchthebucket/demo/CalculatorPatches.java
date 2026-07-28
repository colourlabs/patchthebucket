package net.colourlabs.patchthebucket.demo;

import net.colourlabs.patchthebucket.api.annotation.At;
import net.colourlabs.patchthebucket.api.annotation.Inject;
import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import net.colourlabs.patchthebucket.api.annotation.TransformMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

@TargetClass("net.colourlabs.testplugin.services.CalculatorService")
public class CalculatorPatches {

    @TransformMethod("add")
    public static void fixAdd(MethodNode method) {
        method.instructions.clear();
        method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
        method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
        method.instructions.add(new InsnNode(Opcodes.IADD));
        method.instructions.add(new InsnNode(Opcodes.IRETURN));
    }

    @Inject(method = "divide", at = @At("HEAD"))
    public static InsnList guardDivide(MethodNode method) {
        InsnList insns = new InsnList();
        LabelNode skip = new LabelNode();
        insns.add(new VarInsnNode(Opcodes.ILOAD, 2));
        insns.add(new JumpInsnNode(Opcodes.IFNE, skip));
        insns.add(new InsnNode(Opcodes.ICONST_0));
        insns.add(new InsnNode(Opcodes.IRETURN));
        insns.add(skip);
        return insns;
    }

    @TransformMethod("isEven")
    public static void fixIsEven(MethodNode method) {
        method.instructions.clear();
        method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
        method.instructions.add(new InsnNode(Opcodes.ICONST_2));
        method.instructions.add(new InsnNode(Opcodes.IREM));
        LabelNode zero = new LabelNode();
        method.instructions.add(new JumpInsnNode(Opcodes.IFNE, zero));
        method.instructions.add(new InsnNode(Opcodes.ICONST_1));
        method.instructions.add(new InsnNode(Opcodes.IRETURN));
        method.instructions.add(zero);
        method.instructions.add(new InsnNode(Opcodes.ICONST_0));
        method.instructions.add(new InsnNode(Opcodes.IRETURN));
    }
}
