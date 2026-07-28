package net.colourlabs.testplugin.services;

import java.util.List;
import java.util.ArrayList;

public class GreetingService {

    private final List<String> greetingLog = new ArrayList<>();

    // BUG: NPE if name is null (calls toUpperCase() on null)
    public String greet(String name) {
        return "Hello, " + name.toUpperCase() + "!";
    }

    // BUG: returns the reversed name instead of greeting with it
    public String greetFormal(String firstName, String lastName) {
        String reversed = new StringBuilder(lastName).reverse().toString();
        return "Good day, " + reversed + "!";
    }

    // BUG: always returns "Goodbye!" regardless of input
    public String farewell(String name) {
        return "Goodbye!";
    }

    // BUG: compares strings with == instead of .equals()
    public boolean isKnownUser(String name) {
        String defaultUser = "Player";
        return name == defaultUser;
    }

    public void logGreeting(String name) {
        if (name != null) {
            greetingLog.add(name);
        }
    }

    public int getGreetingCount() {
        // BUG: forgot to check size, always returns -1
        return -1;
    }

    public String getLastGreeted() {
        // BUG: uses wrong index, always throws IndexOutOfBoundsException
        return greetingLog.get(greetingLog.size());
    }
}
