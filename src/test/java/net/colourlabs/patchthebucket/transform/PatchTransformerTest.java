package net.colourlabs.patchthebucket.transform;

import net.colourlabs.patchthebucket.TestClasses;
import net.colourlabs.patchthebucket.api.patch.ClassPatchBuilder;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PatchTransformerTest {
    private static final String CALC = "net/colourlabs/patchthebucket/fixture/Calculator";

    private static PatchTransformer transformer() {
        return new PatchTransformer(Logger.getLogger("PatchTransformerTest"), name -> {
        });
    }

    @Test
    void returnsNullForNullClassName() {
        assertNull(transformer().transform(null, null, null, null, new byte[0]));
    }

    @Test
    void returnsNullForUnregisteredClass() {
        assertNull(transformer().transform(null, CALC, null, null, new byte[0]));
    }

    @Test
    void appliesRegisteredPatchAndNotifiesLoad() throws Exception {
        List<String> loaded = new ArrayList<>();
        PatchTransformer transformer = new PatchTransformer(Logger.getLogger("PatchTransformerTest"), loaded::add);
        transformer.register(new ClassPatchAdapter(
                ClassPatchBuilder.forClass("net.colourlabs.patchthebucket.fixture.Calculator")
                        .transformMethod("broken", method -> {
                            method.instructions.clear();
                            method.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 7));
                            method.instructions.add(new InsnNode(Opcodes.IRETURN));
                        })
                        .build()));

        byte[] patched = transformer.transform(null, CALC, null, null, TestClasses.bytesOf(CALC));

        assertNotNull(patched);
        assertEquals(1, loaded.size());
        assertEquals("net.colourlabs.patchthebucket.fixture.Calculator", loaded.get(0));

        Object calculator = TestClasses.define(CALC, patched).getConstructor().newInstance();
        assertEquals(7, TestClasses.invoke(calculator, "broken", new Class<?>[0]));
    }

    @Test
    void clearPatchesDisablesTransform() throws Exception {
        PatchTransformer transformer = transformer();
        transformer.register(new ClassPatchAdapter(
                ClassPatchBuilder.forClass("net.colourlabs.patchthebucket.fixture.Calculator")
                        .transformMethod("broken", method -> method.instructions.clear())
                        .build()));

        assertNotNull(transformer.transform(null, CALC, null, null, TestClasses.bytesOf(CALC)));

        transformer.clearPatches("net.colourlabs.patchthebucket.fixture.Calculator");
        assertNull(transformer.transform(null, CALC, null, null, TestClasses.bytesOf(CALC)));
    }
}
