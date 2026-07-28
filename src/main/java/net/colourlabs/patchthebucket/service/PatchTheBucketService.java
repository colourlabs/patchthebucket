package net.colourlabs.patchthebucket.service;

import net.colourlabs.patchthebucket.api.PatchTheBucketAPI;
import net.colourlabs.patchthebucket.api.registry.PatchRegistry;

public class PatchTheBucketService implements PatchTheBucketAPI {
    private final PatchRegistryService registry;

    public PatchTheBucketService(PatchRegistryService registry) {
        this.registry = registry;
    }

    @Override
    public PatchRegistry getRegistry() {
        return registry;
    }
}
