package net.colourlabs.patchthebucket.transform;

import net.colourlabs.patchthebucket.TestClasses;
import net.colourlabs.patchthebucket.api.patch.ClassPatch;
import net.colourlabs.patchthebucket.api.patch.ClassPatchBuilder;
import net.colourlabs.patchthebucket.api.patch.MethodSelector;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassPatchAdapterTest {
    private static final String CALC = "net/colourlabs/patchthebucket/fixture/Calculator";

    @Test
    void replacesMethodBody() throws Exception {
        ClassPatch patch = ClassPatchBuilder.forClass("net.colourlabs.patchthebucket.fixture.Calculator")
                .transformMethod("broken", method -> {
                    method.instructions.clear();
                    method.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 42));
                    method.instructions.add(new InsnNode(Opcodes.IRETURN));
                })
                .build();

        byte[] patched = new ClassPatchAdapter(patch).apply(TestClasses.bytesOf(CALC));
        Class<?> loaded = TestClasses.define(CALC, patched);
        Object calculator = loaded.getConstructor().newInstance();

        assertEquals(42, TestClasses.invoke(calculator, "broken", new Class<?>[0]));
    }

    @Test
    void descriptorSelectorNarrowsMatch() throws Exception {
        ClassPatch patch = ClassPatchBuilder.forClass("net.colourlabs.patchthebucket.fixture.Calculator")
                .transformMethod(MethodSelector.named("add", "(II)I"), method -> {
                    method.instructions.clear();
                    method.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 99));
                    method.instructions.add(new InsnNode(Opcodes.IRETURN));
                })
                .build();

        byte[] patched = new ClassPatchAdapter(patch).apply(TestClasses.bytesOf(CALC));
        Class<?> loaded = TestClasses.define(CALC, patched);
        Object calculator = loaded.getConstructor().newInstance();

        assertEquals(99, TestClasses.invoke(calculator, "add",
                new Class<?>[]{int.class, int.class}, 1, 2));
        assertEquals(0, TestClasses.invoke(calculator, "broken", new Class<?>[0]));
    }
}
