package net.colourlabs.patchthebucket.demo;

import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import net.colourlabs.patchthebucket.api.annotation.TransformMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

@TargetClass("net.colourlabs.testplugin.DummyPlugin")
public class PluginPatches {

    @TransformMethod("onEnable")
    public static void fixOnEnable(MethodNode method) {
        method.instructions.clear();

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
                "org/bukkit/plugin/java/JavaPlugin", "onEnable", "()V", false));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new TypeInsnNode(Opcodes.NEW,
                "net/colourlabs/testplugin/services/CalculatorService"));
        method.instructions.add(new InsnNode(Opcodes.DUP));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
                "net/colourlabs/testplugin/services/CalculatorService",
                "<init>", "()V", false));
        method.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD,
                "net/colourlabs/testplugin/DummyPlugin",
                "calculatorService",
                "Lnet/colourlabs/testplugin/services/CalculatorService;"));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new TypeInsnNode(Opcodes.NEW,
                "net/colourlabs/testplugin/services/GreetingService"));
        method.instructions.add(new InsnNode(Opcodes.DUP));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
                "net/colourlabs/testplugin/services/GreetingService",
                "<init>", "()V", false));
        method.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD,
                "net/colourlabs/testplugin/DummyPlugin",
                "greetingService",
                "Lnet/colourlabs/testplugin/services/GreetingService;"));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                "org/bukkit/plugin/java/JavaPlugin", "getLogger",
                "()Ljava/util/logging/Logger;", false));
        method.instructions.add(new LdcInsnNode("TestPlugin enabled!"));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                "java/util/logging/Logger", "info",
                "(Ljava/lang/String;)V", false));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new LdcInsnNode("test"));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                "org/bukkit/plugin/java/JavaPlugin", "getCommand",
                "(Ljava/lang/String;)Lorg/bukkit/command/PluginCommand;", false));
        method.instructions.add(new TypeInsnNode(Opcodes.NEW,
                "net/colourlabs/testplugin/commands/TestCommand"));
        method.instructions.add(new InsnNode(Opcodes.DUP));
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
                "net/colourlabs/testplugin/commands/TestCommand",
                "<init>", "(Lnet/colourlabs/testplugin/DummyPlugin;)V", false));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                "org/bukkit/command/PluginCommand", "setExecutor",
                "(Lorg/bukkit/command/CommandExecutor;)V", false));

        method.instructions.add(new InsnNode(Opcodes.RETURN));
    }
}
