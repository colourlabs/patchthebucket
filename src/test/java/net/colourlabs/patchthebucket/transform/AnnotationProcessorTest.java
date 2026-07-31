package net.colourlabs.patchthebucket.transform;

import net.colourlabs.patchthebucket.TestClasses;
import net.colourlabs.patchthebucket.api.patch.ClassPatch;
import net.colourlabs.patchthebucket.api.patch.Patch;
import net.colourlabs.patchthebucket.api.registry.PatchRegistry;
import net.colourlabs.patchthebucket.fixture.BadAtValuePatches;
import net.colourlabs.patchthebucket.fixture.CalculatorPatches;
import net.colourlabs.patchthebucket.fixture.MissingTargetAnnotationPatches;
import net.colourlabs.patchthebucket.fixture.NonStaticPatchPatches;
import net.colourlabs.patchthebucket.fixture.NumbersPatches;
import net.colourlabs.patchthebucket.fixture.Track;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnnotationProcessorTest {
    private static final String CALC = "net/colourlabs/patchthebucket/fixture/Calculator";
    private static final String NUMBERS = "net/colourlabs/patchthebucket/fixture/Numbers";

    private static final class RecordingRegistry implements PatchRegistry {
        final List<ClassPatch> classPatches = new ArrayList<>();
        final List<Patch> patches = new ArrayList<>();

        @Override
        public void register(Patch patch) {
            patches.add(patch);
        }

        @Override
        public void register(ClassPatch patch) {
            classPatches.add(patch);
        }

        @Override
        public void registerAndApply(Patch patch) {
            patches.add(patch);
        }

        @Override
        public void registerAndApply(ClassPatch patch) {
            classPatches.add(patch);
        }

        @Override
        public void registerAnnotated(Class<?> patchClass) {
        }

        @Override
        public void unregister(String targetClassName) {
        }
    }

    @Test
    void transformMethodReplacesBody() throws Exception {
        RecordingRegistry registry = new RecordingRegistry();
        AnnotationProcessor.process(CalculatorPatches.class, registry);

        assertEquals(1, registry.classPatches.size());
        ClassPatch patch = registry.classPatches.get(0);
        assertEquals("net.colourlabs.patchthebucket.fixture.Calculator", patch.targetClassName());

        byte[] patched = new ClassPatchAdapter(patch).apply(TestClasses.bytesOf(CALC));
        Object calculator = TestClasses.define(CALC, patched).getConstructor().newInstance();

        assertEquals(42, TestClasses.invoke(calculator, "broken", new Class<?>[0]));
        assertEquals(3, TestClasses.invoke(calculator, "add",
                new Class<?>[]{int.class, int.class}, 1, 2));
    }

    @Test
    void returnInjectionHitsEveryReturnType() throws Exception {
        Track.calls = 0;
        RecordingRegistry registry = new RecordingRegistry();
        AnnotationProcessor.process(NumbersPatches.class, registry);

        assertEquals(1, registry.classPatches.size());
        ClassPatch patch = registry.classPatches.get(0);

        byte[] patched = new ClassPatchAdapter(patch).apply(TestClasses.bytesOf(NUMBERS));
        Object numbers = TestClasses.define(NUMBERS, patched).getConstructor().newInstance();

        assertEquals(0, TestClasses.invoke(numbers, "sq", new Class<?>[]{int.class}, 3));
        assertEquals(0L, TestClasses.invoke(numbers, "identity", new Class<?>[]{long.class}, 5L));
        assertEquals(0.0f, TestClasses.invoke(numbers, "half", new Class<?>[]{float.class}, 2.5f));
        assertEquals(0.0, TestClasses.invoke(numbers, "magnitude", new Class<?>[]{double.class}, 5.0));
        assertEquals(0.0, TestClasses.invoke(numbers, "magnitude", new Class<?>[]{double.class}, -5.0));
        assertNull(TestClasses.invoke(numbers, "echo", new Class<?>[]{Object.class}, new Object[]{null}));
        TestClasses.invoke(numbers, "nothing", new Class<?>[0]);

        assertEquals(7, Track.calls);
    }

    @Test
    void unknownAtValueThrows() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> AnnotationProcessor.process(BadAtValuePatches.class, new RecordingRegistry()));
        assertTrue(e.getMessage().contains("NOPE"));
    }

    @Test
    void missingTargetClassThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> AnnotationProcessor.process(MissingTargetAnnotationPatches.class, new RecordingRegistry()));
    }

    @Test
    void nonStaticTransformMethodThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> AnnotationProcessor.process(NonStaticPatchPatches.class, new RecordingRegistry()));
    }
}
