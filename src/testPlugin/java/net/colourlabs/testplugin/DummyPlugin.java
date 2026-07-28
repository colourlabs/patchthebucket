package net.colourlabs.testplugin;

import net.colourlabs.testplugin.commands.TestCommand;
import net.colourlabs.testplugin.services.CalculatorService;
import net.colourlabs.testplugin.services.GreetingService;
import org.bukkit.plugin.java.JavaPlugin;

// This plugin is meant to be broken, used as a test case for actual patching.

public class DummyPlugin extends JavaPlugin {
    private CalculatorService calculatorService = null;
    private GreetingService greetingService = null;

    @Override
    public void onEnable() {
        getLogger().info("PatchTheBucket TestPlugin enabled!");

        // BUG: doesn't actually assign the new instances, fields stay null
        new CalculatorService();
        new GreetingService();

        getCommand("test").setExecutor(new TestCommand(this));
    }

    public CalculatorService getCalculatorService() {
        // BUG: could return null since fields are never assigned
        return calculatorService;
    }

    public GreetingService getGreetingService() {
        // BUG: could return null since fields are never assigned
        return greetingService;
    }

    @Override
    public void onDisable() {
        getLogger().info("TestPlugin disabled.");
    }
}
