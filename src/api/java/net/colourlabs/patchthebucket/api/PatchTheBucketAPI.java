package net.colourlabs.patchthebucket.api;

import net.colourlabs.patchthebucket.api.registry.PatchRegistry;

/**
 * Main entry point for other plugins to access the patching framework.
 *
 * <p>Retrieve via Bukkit's ServiceManager:
 * <pre>{@code
 * PatchTheBucketAPI api = Bukkit.getServicesManager().load(PatchTheBucketAPI.class);
 * }</pre>
 */
public interface PatchTheBucketAPI {
    PatchRegistry getRegistry();
}
