package com.example.watcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

public class RegisterDirHandler implements EventHandler {

    private final Map<WatchKey, Path> listeners = new HashMap<>();
    private final WatchService watcher;

    public RegisterDirHandler(WatchService watcher) {
        this.watcher = watcher;
    }

    @Override
    public void handle(WatchKey key, WatchEvent<Path> ev, Path path) throws IOException {
        if ((ev == null || ev.kind() == StandardWatchEventKinds.ENTRY_CREATE) && Files.isDirectory(path)) {
            this.registerListener(this.watcher, path, this.listeners);
        }
    }

    private void registerListener(WatchService watcher, Path newDir, Map<WatchKey, Path> listeners) throws IOException {
        WatchKey key = newDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        listeners.put(key, newDir);
//        System.out.println("Listening " + newDir);
    }

    public Map<WatchKey, Path> getListeners() {
        return this.listeners;
    }
}