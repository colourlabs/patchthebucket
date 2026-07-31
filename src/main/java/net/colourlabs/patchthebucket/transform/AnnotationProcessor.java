package net.colourlabs.patchthebucket.transform;

import net.colourlabs.patchthebucket.api.annotation.At;
import net.colourlabs.patchthebucket.api.annotation.Inject;
import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import net.colourlabs.patchthebucket.api.annotation.TransformMethod;
import net.colourlabs.patchthebucket.api.patch.ClassPatchBuilder;
import net.colourlabs.patchthebucket.api.patch.MethodSelector;
import net.colourlabs.patchthebucket.api.patch.MethodTransform;
import net.colourlabs.patchthebucket.api.registry.PatchRegistry;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;

import java.util.HashMap;
import java.util.Map;

public final class AnnotationProcessor {
    private AnnotationProcessor() {}

    public static void process(Class<?> patchClass, PatchRegistry registry) {
        TargetClass targetAnnotation = patchClass.getAnnotation(TargetClass.class);
        if (targetAnnotation == null) {
            throw new IllegalArgumentException(
                    patchClass + " is missing @TargetClass annotation");
        }

        String targetClass = targetAnnotation.value();
        ClassPatchBuilder builder = ClassPatchBuilder.forClass(targetClass)
                .computeFrames(targetAnnotation.computeFrames());

        for (java.lang.reflect.Method method : patchClass.getDeclaredMethods()) {
            TransformMethod transform = method.getAnnotation(TransformMethod.class);
            if (transform != null) {
                validateStatic(method);
                String methodName = transform.value();
                String descriptor = transform.descriptor().isEmpty() ? null : transform.descriptor();

                builder.transformMethod(
                        selector(methodName, descriptor),
                        createMethodTransform(method));
            }

            Inject inject = method.getAnnotation(Inject.class);
            if (inject != null) {
                validateStatic(method);
                String methodName = inject.method();
                String descriptor = inject.descriptor().isEmpty() ? null : inject.descriptor();
                At at = inject.at();

                switch (at.value().toUpperCase()) {
                    case "HEAD":
                        builder.transformMethod(
                                selector(methodName, descriptor),
                                createHeadInject(method));
                        break;
                    case "RETURN":
                        builder.transformMethod(
                                selector(methodName, descriptor),
                                createReturnInject(method));
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Unsupported @At value \"" + at.value()
                                        + "\" on " + method.getName());
                }
            }
        }

        registry.registerAndApply(builder.build());
    }

    private static MethodSelector selector(String name, String descriptor) {
        return descriptor != null
                ? MethodSelector.named(name, descriptor)
                : MethodSelector.named(name);
    }

    private static void validateStatic(java.lang.reflect.Method method) {
        if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException(
                    "@TransformMethod and @Inject methods must be static: " + method);
        }
    }

    private static MethodTransform createMethodTransform(java.lang.reflect.Method transformMethod) {
        return methodNode -> {
            try {
                transformMethod.invoke(null, methodNode);
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke @TransformMethod: "
                        + transformMethod.getName(), e);
            }
        };
    }

    private static MethodTransform createHeadInject(java.lang.reflect.Method injectMethod) {
        return methodNode -> {
            try {
                InsnList insns = (InsnList) injectMethod.invoke(null, methodNode);
                if (insns != null) {
                    methodNode.instructions.insert(insns);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke @Inject method: "
                        + injectMethod.getName(), e);
            }
        };
    }

    private static MethodTransform createReturnInject(java.lang.reflect.Method injectMethod) {
        return methodNode -> {
            try {
                InsnList insns = (InsnList) injectMethod.invoke(null, methodNode);
                if (insns == null) return;

                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    int opcode = insn.getOpcode();
                    if (opcode == Opcodes.RETURN
                            || opcode == Opcodes.IRETURN
                            || opcode == Opcodes.LRETURN
                            || opcode == Opcodes.FRETURN
                            || opcode == Opcodes.DRETURN
                            || opcode == Opcodes.ARETURN) {
                        methodNode.instructions.insertBefore(insn, cloneInsnList(insns));
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke @Inject method: "
                        + injectMethod.getName(), e);
            }
        };
    }

    private static InsnList cloneInsnList(InsnList list) {
        InsnList clone = new InsnList();
        Map<LabelNode, LabelNode> labelMap = new HashMap<>();
        for (AbstractInsnNode insn : list.toArray()) {
            clone.add(insn.clone(labelMap));
        }
        return clone;
    }
}
