package net.colourlabs.patchthebucket.api.patch;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ClassPatchBuilder {
    private final String targetClassName;
    private final Map<MethodSelector, MethodTransform> transforms = new LinkedHashMap<>();
    private boolean computeFrames = true;

    private ClassPatchBuilder(String targetClassName) {
        this.targetClassName = targetClassName;
    }

    public static ClassPatchBuilder forClass(String targetClassName) {
        return new ClassPatchBuilder(targetClassName);
    }

    public ClassPatchBuilder computeFrames(boolean computeFrames) {
        this.computeFrames = computeFrames;
        return this;
    }

    public ClassPatchBuilder transformMethod(MethodSelector selector, MethodTransform transform) {
        transforms.put(selector, transform);
        return this;
    }

    public ClassPatchBuilder transformMethod(String name, MethodTransform transform) {
        return transformMethod(MethodSelector.named(name), transform);
    }

    public ClassPatch build() {
        final Map<MethodSelector, MethodTransform> frozen = new LinkedHashMap<>(transforms);
        final boolean frames = this.computeFrames;
        return new ClassPatch() {
            @Override
            public String targetClassName() {
                return targetClassName;
            }

            @Override
            public boolean computeFrames() {
                return frames;
            }

            @Override
            public void apply(ClassNode classNode) {
                for (MethodNode method : classNode.methods) {
                    for (Map.Entry<MethodSelector, MethodTransform> entry : frozen.entrySet()) {
                        MethodSelector sel = entry.getKey();
                        if (method.name.equals(sel.name())) {
                            if (sel.descriptor() == null || method.desc.equals(sel.descriptor())) {
                                entry.getValue().transform(method);
                            }
                        }
                    }
                }
            }
        };
    }
}
