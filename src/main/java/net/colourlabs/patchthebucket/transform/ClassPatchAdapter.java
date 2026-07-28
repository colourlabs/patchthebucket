package net.colourlabs.patchthebucket.transform;

import net.colourlabs.patchthebucket.api.patch.ClassPatch;
import net.colourlabs.patchthebucket.api.patch.Patch;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class ClassPatchAdapter implements Patch {
    private final ClassPatch classPatch;

    public ClassPatchAdapter(ClassPatch classPatch) {
        this.classPatch = classPatch;
    }

    @Override
    public String targetClassName() {
        return classPatch.targetClassName();
    }

    @Override
    public byte[] apply(byte[] originalBytes) {
        ClassReader reader = new ClassReader(originalBytes);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, ClassReader.SKIP_FRAMES);

        classPatch.apply(classNode);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
