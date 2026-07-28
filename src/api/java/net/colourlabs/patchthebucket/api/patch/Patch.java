package net.colourlabs.patchthebucket.api.patch;

public interface Patch {
    String targetClassName();
    byte[] apply(byte[] originalBytes);
}
