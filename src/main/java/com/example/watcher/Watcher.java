package com.example.watcher;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Watcher {

    private final Map<WatchKey, Path> listeners = new HashMap<>();
    private final List<EventHandler> handlers;
    private final Path root;

    public Watcher(Path root, List<EventHandler> handlers) {
        this.root = root;
        this.handlers = handlers;
    }

    public void watch() throws IOException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        UnregisterHandler unregisterHandler = new UnregisterHandler();
        this.handlers.add(0, unregisterHandler);
        RegisterDirHandler registerHandler = new RegisterDirHandler(watcher);
        this.handlers.add(1, registerHandler);
        Files.walkFileTree(this.root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path newDir, BasicFileAttributes attrs) throws IOException {
                for (EventHandler eventHandler : Watcher.this.handlers) {
                    eventHandler.handle(null, null, newDir);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        this.listeners.putAll(registerHandler.getListeners());
        registerHandler.getListeners().clear();
        while (!this.listeners.isEmpty()) {
            for (Entry<WatchKey, Path> entry : this.listeners.entrySet()) {
                for (WatchEvent<?> event : entry.getKey().pollEvents()) {
                    Kind<?> kind = event.kind();
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = (Path) event.context();
                    filename = ((Path) entry.getValue()).resolve(filename);
                    System.out.println(kind + ": " + filename);
                    for (EventHandler eventHandler : this.handlers) {
                        eventHandler.handle((WatchKey) entry.getKey(), ev, filename);
                    }
                }
            }
            this.listeners.putAll(registerHandler.getListeners());
            registerHandler.getListeners().clear();
            unregisterHandler.getUnregister().stream().forEach(r -> this.listeners.remove(r));
            unregisterHandler.getUnregister().clear();
        }
        System.out.println("Exiting");
    }
}
