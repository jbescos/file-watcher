package com.example.watcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.HashSet;
import java.util.Set;

public class UnregisterHandler implements EventHandler {

    private final Set<WatchKey> unregister = new HashSet<>();

    @Override
    public void handle(WatchKey key, WatchEvent<Path> ev, Path path) throws IOException {
        if (key != null && !key.reset()) {
            this.unregister.add(key);
            System.out.println("Removing listener " + String.valueOf(path));
        }
    }

    public Set<WatchKey> getUnregister() {
        return this.unregister;
    }

}
