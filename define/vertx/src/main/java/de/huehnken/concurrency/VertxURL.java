package de.huehnken.concurrency;

public class VertxURL {
    public final String host;
    public final String path;

    public VertxURL(final String host, final String path) {
        this.host = host;
        this.path = path;
    }
}