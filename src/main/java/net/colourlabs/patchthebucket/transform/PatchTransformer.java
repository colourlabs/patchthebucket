package net.colourlabs.patchthebucket.transform;

import net.colourlabs.patchthebucket.api.patch.Patch;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PatchTransformer implements ClassFileTransformer {
    private final Logger logger;
    private final Consumer<String> onClassLoaded;
    private final Map<String, List<Patch>> patchesByClass = new ConcurrentHashMap<>();

    public PatchTransformer(Logger logger, Consumer<String> onClassLoaded) {
        this.logger = logger;
        this.onClassLoaded = onClassLoaded;
    }

    public void register(Patch patch) {
        patchesByClass
                .computeIfAbsent(patch.targetClassName(), k -> new CopyOnWriteArrayList<>())
                .add(patch);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className == null)
            return null;

        String dotted = className.replace('/', '.');
        List<Patch> patches = patchesByClass.get(dotted);

        if (patches == null || patches.isEmpty()) {
            return null;
        }

        onClassLoaded.accept(dotted);

        byte[] current = classfileBuffer;
        for (Patch patch : patches) {
            try {
                current = patch.apply(current);
                logger.info("Applied patch to " + dotted + " via " + patch.getClass().getSimpleName());
            } catch (Throwable t) {
                logger.log(Level.SEVERE, "Patch failed for " + dotted + ", skipping", t);
            }
        }

        return current;
    }

    public List<Patch> clearPatches(String className) {
        return patchesByClass.remove(className);
    }
}
