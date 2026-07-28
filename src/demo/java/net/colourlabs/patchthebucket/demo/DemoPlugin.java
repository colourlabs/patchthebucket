package net.colourlabs.patchthebucket.demo;

import net.colourlabs.patchthebucket.api.PatchTheBucketAPI;
import net.colourlabs.patchthebucket.api.registry.PatchRegistry;

import org.bukkit.plugin.java.JavaPlugin;

public class DemoPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        PatchTheBucketAPI api = getServer().getServicesManager().load(PatchTheBucketAPI.class);

        if (api == null) {
            getLogger().severe("PatchTheBucket not found! Download it to use demo patches");
            getPluginLoader().disablePlugin(this);
            return;
        }

        PatchRegistry registry = api.getRegistry();
        registry.registerAnnotated(CalculatorPatches.class);
        registry.registerAnnotated(GreetingPatches.class);
        registry.registerAnnotated(PluginPatches.class);

        getLogger().info("Demo patches registered!");
    }
}
