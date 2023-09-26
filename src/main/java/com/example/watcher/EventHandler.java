package com.example.watcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

public interface EventHandler {

    void handle(WatchKey paramWatchKey, WatchEvent<Path> paramWatchEvent, Path paramPath) throws IOException;

}
