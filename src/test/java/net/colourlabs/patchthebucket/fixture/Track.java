package net.colourlabs.patchthebucket.fixture;

public final class Track {
    public static int calls;

    private Track() {}

    public static void record() {
        calls++;
    }
}
