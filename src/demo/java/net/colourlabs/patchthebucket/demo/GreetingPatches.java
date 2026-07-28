package net.colourlabs.patchthebucket.demo;

import net.colourlabs.patchthebucket.api.annotation.Inject;
import net.colourlabs.patchthebucket.api.annotation.At;
import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

@TargetClass("net.colourlabs.testplugin.services.GreetingService")
public class GreetingPatches {

    @Inject(method = "greet", at = @At("HEAD"))
    public static InsnList guardNullName(MethodNode method) {
        InsnList insns = new InsnList();
        LabelNode skip = new LabelNode();
        insns.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insns.add(new JumpInsnNode(Opcodes.IFNONNULL, skip));
        insns.add(new LdcInsnNode("Guest"));
        insns.add(new VarInsnNode(Opcodes.ASTORE, 1));
        insns.add(skip);
        return insns;
    }
}
