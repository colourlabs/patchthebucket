package net.colourlabs.patchthebucket.fixture;

public class Numbers {
    public int sq(int x) {
        return x * x;
    }

    public long identity(long x) {
        return x;
    }

    public float half(float x) {
        return x;
    }

    public double magnitude(double x) {
        if (x < 0) {
            return -x;
        }
        return x;
    }

    public Object echo(Object o) {
        return o;
    }

    public void nothing() {}
}
