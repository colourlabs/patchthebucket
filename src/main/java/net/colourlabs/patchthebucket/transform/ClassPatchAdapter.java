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
        int readerFlags = classPatch.computeFrames() ? ClassReader.SKIP_FRAMES : 0;
        ClassReader reader = new ClassReader(originalBytes);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, readerFlags);

        classPatch.apply(classNode);

        int writerFlags = classPatch.computeFrames()
                ? ClassWriter.COMPUTE_FRAMES
                : ClassWriter.COMPUTE_MAXS;

        try {
            ClassWriter writer = new ClassWriter(writerFlags) {
                @Override
                protected String getCommonSuperClass(String type1, String type2) {
                    try {
                        return super.getCommonSuperClass(type1, type2);
                    } catch (TypeNotPresentException e) {
                        return "java/lang/Object";
                    }
                }
            };
            classNode.accept(writer);
            return writer.toByteArray();
        } catch (TypeNotPresentException e) {
            return originalBytes;
        }
    }
}
