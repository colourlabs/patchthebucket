package net.colourlabs.patchthebucket.agent;

import net.bytebuddy.agent.ByteBuddyAgent;

import java.lang.instrument.Instrumentation;

public class PatchAgent {
    private static volatile Instrumentation instrumentation;

    public static synchronized Instrumentation obtain() {
        if (instrumentation == null) {
            instrumentation = ByteBuddyAgent.install();
        }
        
        return instrumentation;
    }
}