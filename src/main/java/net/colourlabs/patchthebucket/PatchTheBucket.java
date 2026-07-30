package net.colourlabs.patchthebucket;

import net.colourlabs.patchthebucket.agent.PatchAgent;
import net.colourlabs.patchthebucket.api.PatchTheBucketAPI;
import net.colourlabs.patchthebucket.service.PatchRegistryService;
import net.colourlabs.patchthebucket.service.PatchTheBucketService;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class PatchTheBucket extends JavaPlugin {
    private PatchRegistryService registry;

    @Override
    public void onEnable() {
        checkJavaVersion();
        checkMinecraftVersion();

        try {
            PatchAgent.obtain();
            registry = new PatchRegistryService(PatchAgent.obtain(), getLogger());

            PatchTheBucketAPI api = new PatchTheBucketService(registry);
            getServer().getServicesManager().register(
                    PatchTheBucketAPI.class, api, this, ServicePriority.Normal);

            getLogger().info("PatchTheBucket initialized :)");

        } catch (Exception e) {
            getLogger().severe("Failed to init patch framework: " + e.getMessage());
            getPluginLoader().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (registry != null) {
            getServer().getServicesManager().unregister(PatchTheBucketAPI.class, this);
        }
    }

    private void checkMinecraftVersion() {
        String raw = Bukkit.getBukkitVersion();
        
        getLogger().info("Running on " + Bukkit.getName() + " " + Bukkit.getVersion());
        getLogger().info("Bukkit API version: " + raw);
    }

    private void checkJavaVersion() {
        String raw = System.getProperty("java.version");
        int major = 0;

        if (raw.startsWith("1.")) {
            major = Integer.parseInt(raw.substring(2, raw.indexOf('.', 2)));
        } else {
            major = Integer.parseInt(raw.substring(0, raw.indexOf('.')));
        }

        if (major > 8) {
            getLogger().warning("Running on Java " + major + " (target: Java 8). "
                    + "If you see a warning about dynamic agent loading, add "
                    + "-XX:+EnableDynamicAgentLoading to your JVM flags");
        }
    }
}
