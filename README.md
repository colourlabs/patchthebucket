# patchthebucket

PatchTheBucket is a Bukkit (Spigot + Paper) plugin patching framework for Minecraft Java 1.12.2.

It transforms other plugins' bytecode at load time using a Java agent + ASM, swapping broken classes with fixed versions before they're fully loaded.

# why

1.12.2 can run on basically anything, even the worst of hardware. Some plugins have slight bugs (1.12.2 came out in 2017), their source code isn't updated for modern tooling, and rebuilding from source is a pain. PatchTheBucket lets you fix them at the bytecode level without touching the original jar.

# how it works

1. A Java agent (ByteBuddy Agent) attaches at runtime, no `-javaagent` flag needed
2. PatchTheBucket registers a `ClassFileTransformer` and exposes a `PatchTheBucketAPI` via Bukkit's `ServicesManager`
3. Consumer plugins discover the API, declare their patches (via annotations or programmatic builders), and register them
4. Patches apply at class load time; already-loaded classes are retransformed

Patches use ASM tree API (`MethodNode`, `InsnList`, etc) directly, No abstraction layer.

# setup

## server

Drop `patchthebucket-1.0.0.jar` in `plugins/`. No config required

If you get a warning about dynamic agent loading on Java 21+, add to your JVM flags:

```
-XX:+EnableDynamicAgentLoading
```

## for patch authors

Add JitPack to your `build.gradle.kts`:

```kotlin
repositories {
    maven("https://jitpack.io")
    mavenLocal() // spigot api from BuildTools
}

dependencies {
    compileOnly("com.github.colourlabs.patchthebucket:patchthebucket-api:VERSION")
    compileOnly("org.ow2.asm:asm-tree:9.7")
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
}
```

ASM and the API are `compileOnly` - at runtime they come from PatchTheBucket's classloader. The Spigot API is `compileOnly` too, provided by the server.

Replace `VERSION` with a tag (e.g. `1.0.0`) or `main-SNAPSHOT` for latest commit.

# usage

## annotation-based

```java
@TargetClass("net.example.BrokenPlugin")
public class MyPatches {

    @TransformMethod("brokenMethod")
    public static void fixIt(MethodNode method) {
        method.instructions.clear();
        method.instructions.add(new VarInsnNode(Opcodes.ICONST_0));
        method.instructions.add(new InsnNode(Opcodes.IRETURN));
    }

    @Inject(method = "riskyMethod", at = @At("HEAD"))
    public static InsnList guardNull(MethodNode method) {
        InsnList insns = new InsnList();
        LabelNode skip = new LabelNode();
        insns.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insns.add(new JumpInsnNode(Opcodes.IFNONNULL, skip));
        insns.add(new InsnNode(Opcodes.ICONST_0));
        insns.add(new InsnNode(Opcodes.IRETURN));
        insns.add(skip);
        return insns;
    }
}
```

## registering from your plugin

```java
public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        PatchTheBucketAPI api = getServer()
            .getServicesManager()
            .load(PatchTheBucketAPI.class);
        api.registerAnnotated(MyPatches.class);
    }
}
```

Add `depend: [PatchTheBucket]` to your `plugin.yml`.

## programmatic API

```java
ClassPatch patch = ClassPatchBuilder.forClass("net.example.BrokenPlugin")
    .transformMethod(MethodSelector.named("brokenMethod"), methodNode -> {
        methodNode.instructions.clear();
        // ...
    })
    .build();
api.register(patch);
```

# building

```sh
./gradlew build
```

Outputs in `build/libs/`:

| jar                             | purpose                                          |
| ------------------------------- | ------------------------------------------------ |
| `patchthebucket-1.0.0.jar`      | framework (shaded, deploy to server)             |
| `patchthebucket-api-1.0.0.jar`  | API for consumer compile-time dep                |
| `test-plugin-1.0.0.jar`         | broken dummy plugin for testing                  |
| `patchthebucket-demo-1.0.0.jar` | example consumer plugin that patches test-plugin |

## setting up buildtools

You will need a Java 8 installation to setup the libraries for Spigot, tested on Linux 7.1.3. PaperMC Maven don't host 1.12.2 anymore sadly

```sh
git clone https://hub.spigotmc.org/stash/scm/spigot/buildtools.git
cd buildtools
java -jar BuildTools.jar --rev 1.12.2 # USE JAVA 8 FOR THIS, it will error due to version checks.
```

This should add the required libraries for 1.12.2 Bukkit development into MavenLocal meaning you can build the project / make plugins

# requirements

- Java 8+
- Spigot / Paper 1.12.2

# license 

MIT 
