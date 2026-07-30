package net.colourlabs.patchthebucket.service;

import net.colourlabs.patchthebucket.api.patch.ClassPatch;
import net.colourlabs.patchthebucket.api.patch.Patch;
import net.colourlabs.patchthebucket.api.registry.PatchRegistry;
import net.colourlabs.patchthebucket.transform.AnnotationProcessor;
import net.colourlabs.patchthebucket.transform.ClassPatchAdapter;
import net.colourlabs.patchthebucket.transform.PatchTransformer;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PatchRegistryService implements PatchRegistry {
    private final Instrumentation instrumentation;
    private final PatchTransformer transformer;
    private final Logger logger;
    private final ConcurrentHashMap<String, Class<?>> loadedClassCache = new ConcurrentHashMap<>();

    public PatchRegistryService(Instrumentation instrumentation, Logger logger) {
        this.instrumentation = instrumentation;
        this.logger = logger;
        this.transformer = new PatchTransformer(logger);
        instrumentation.addTransformer(transformer, true);
    }

    @Override
    public void register(Patch patch) {
        transformer.register(patch);
    }

    @Override
    public void register(ClassPatch classPatch) {
        register(new ClassPatchAdapter(classPatch));
    }

    @Override
    public void registerAndApply(Patch patch) {
        register(patch);
        retransformTarget(patch.targetClassName());
    }

    @Override
    public void registerAndApply(ClassPatch classPatch) {
        registerAndApply(new ClassPatchAdapter(classPatch));
    }

    @Override
    public void registerAnnotated(Class<?> patchClass) {
        try {
            AnnotationProcessor.process(patchClass, this);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to process annotations on " + patchClass.getName(), e);
        }
    }

    @Override
    public void unregister(String targetClassName) {
        transformer.clearPatches(targetClassName);
    }

    private void retransformTarget(String dottedName) {
        Class<?> target = findLoadedClass(dottedName);
        if (target == null) {
            logger.info(dottedName + " not loaded yet, will apply on class load.");
            return;
        }

        if (!instrumentation.isModifiableClass(target)) {
            logger.warning("Class " + dottedName + " is not modifiable.");
            return;
        }

        try {
            instrumentation.retransformClasses(target);
            logger.info("Retransformed already-loaded class: " + dottedName);
        } catch (UnmodifiableClassException e) {
            logger.warning("Retransform failed for " + dottedName + ": " + e.getMessage());
        } catch (Throwable e) {
            logger.log(Level.WARNING, "Patch rejected by JVM for " + dottedName
                    + ", class left unchanged", e);
        }
    }

    private Class<?> findLoadedClass(String dottedName) {
        Class<?> cached = loadedClassCache.get(dottedName);
        if (cached != null) {
            return cached;
        }
        for (Class<?> c : instrumentation.getAllLoadedClasses()) {
            if (c.getName().equals(dottedName)) {
                loadedClassCache.put(dottedName, c);
                return c;
            }
        }
        return null;
    }
}
