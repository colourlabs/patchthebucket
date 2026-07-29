package net.colourlabs.patchthebucket.api.inject;

import net.colourlabs.patchthebucket.api.patch.ClassPatch;
import net.colourlabs.patchthebucket.api.patch.MethodSelector;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds a ClassPatch that injects code at specific points.
 *
 * <pre>{@code
 * ClassPatch patch = InjectorBuilder.forClass("some.plugin.Class")
 *     .inject(MethodSelector.named("divide"), InjectionPoint.atHead(), method -> {
 *         InsnList insns = new InsnList();
 *         insns.add(new VarInsnNode(ILOAD, 2));
 *         insns.add(new JumpInsnNode(IFNE, ...));
 *         return insns;
 *     })
 *     .build();
 * }</pre>
 */
public final class InjectorBuilder {
    private final String targetClassName;
    private final List<Injection> injections = new ArrayList<>();

    private InjectorBuilder(String targetClassName) {
        this.targetClassName = targetClassName;
    }

    public static InjectorBuilder forClass(String targetClassName) {
        return new InjectorBuilder(targetClassName);
    }

    public InjectorBuilder inject(MethodSelector method, InjectionPoint point, Injector injector) {
        injections.add(new Injection(method, point, injector));
        return this;
    }

    public ClassPatch build() {
        final List<Injection> frozen = new ArrayList<>(injections);
        return new ClassPatch() {
            @Override
            public String targetClassName() {
                return targetClassName;
            }

            @Override
            public boolean computeFrames() {
                return true;
            }

            @Override
            public void apply(ClassNode classNode) {
                for (MethodNode method : classNode.methods) {
                    for (Injection injection : frozen) {
                        MethodSelector sel = injection.method;
                        if (method.name.equals(sel.name())) {
                            if (sel.descriptor() == null || method.desc.equals(sel.descriptor())) {
                                applyInjection(method, injection);
                            }
                        }
                    }
                }
            }

            private void applyInjection(MethodNode method, Injection injection) {
                switch (injection.point.location()) {
                    case HEAD:
                        method.instructions.insert(injection.injector.instructions(method));
                        break;
                    case RETURN:
                        org.objectweb.asm.tree.InsnList toInsert = injection.injector.instructions(method);
                        for (org.objectweb.asm.tree.AbstractInsnNode insn : method.instructions.toArray()) {
                            int opcode = insn.getOpcode();
                            if (opcode == org.objectweb.asm.Opcodes.RETURN
                                    || opcode == org.objectweb.asm.Opcodes.IRETURN
                                    || opcode == org.objectweb.asm.Opcodes.ARETURN) {
                                method.instructions.insertBefore(insn, cloneInsnList(toInsert));
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

            private org.objectweb.asm.tree.InsnList cloneInsnList(org.objectweb.asm.tree.InsnList list) {
                org.objectweb.asm.tree.InsnList clone = new org.objectweb.asm.tree.InsnList();
                Map<LabelNode, LabelNode> labelMap = new HashMap<>();
                for (org.objectweb.asm.tree.AbstractInsnNode insn : list.toArray()) {
                    clone.add(insn.clone(labelMap));
                }
                return clone;
            }
        };
    }

    private static final class Injection {
        final MethodSelector method;
        final InjectionPoint point;
        final Injector injector;

        Injection(MethodSelector method, InjectionPoint point, Injector injector) {
            this.method = method;
            this.point = point;
            this.injector = injector;
        }
    }
}
