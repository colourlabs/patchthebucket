package net.colourlabs.patchthebucket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public final class TestClasses {
    private TestClasses() {}

    public static byte[] bytesOf(String internalName) throws IOException {
        String path = "/" + internalName + ".class";
        try (InputStream in = TestClasses.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IOException("Class bytes not found: " + path);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return out.toByteArray();
        }
    }

    public static Class<?> define(String internalName, byte[] bytes) {
        final String dotted = internalName.replace('/', '.');
        ClassLoader loader = new ClassLoader(TestClasses.class.getClassLoader()) {
            @Override
            protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                Class<?> found = findLoadedClass(name);
                if (found == null && name.equals(dotted)) {
                    found = defineClass(name, bytes, 0, bytes.length);
                }
                if (found == null) {
                    found = super.loadClass(name, resolve);
                } else if (resolve) {
                    resolveClass(found);
                }
                return found;
            }
        };
        try {
            return loader.loadClass(dotted);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Object invoke(Object instance, String method, Class<?>[] types, Object... args)
            throws Exception {
        Method m = instance.getClass().getMethod(method, types);
        return m.invoke(instance, args);
    }
}
