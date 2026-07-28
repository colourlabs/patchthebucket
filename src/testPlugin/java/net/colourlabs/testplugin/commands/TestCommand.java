package net.colourlabs.testplugin.commands;

import net.colourlabs.testplugin.DummyPlugin;
import net.colourlabs.testplugin.services.CalculatorService;
import net.colourlabs.testplugin.services.GreetingService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestCommand implements CommandExecutor {

    private final DummyPlugin plugin;

    public TestCommand(DummyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        CalculatorService calc = plugin.getCalculatorService();
        GreetingService greet = plugin.getGreetingService();

        if (args.length == 0) {
            // BUG: will NPE since services are null
            sender.sendMessage(greet.greet("Player"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add":
                // BUG: will NPE since calc is null
                sender.sendMessage("5 + 3 = " + calc.add(5, 3));
                break;

            case "divide":
                // BUG: will crash with ArithmeticException and NPE
                sender.sendMessage("10 / 0 = " + calc.divide(10, 0));
                break;

            case "greet":
                // BUG: NPE if no second arg (null passed to greet)
                String name = args.length > 1 ? args[1] : null;
                sender.sendMessage(greet.greet(name));
                break;

            case "count":
                // BUG: returns -1 instead of actual count
                sender.sendMessage("Greeting count: " + greet.getGreetingCount());
                break;

            default:
                sender.sendMessage("Unknown subcommand.");
        }

        return true;
    }
}
