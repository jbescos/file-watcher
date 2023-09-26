package com.example.watcher;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FileReplacerHandler implements EventHandler {

    private static final String BEGGINS = "FILE_REPLACER_";
    private final Map<String, Path> replacements = new HashMap<>();

    public FileReplacerHandler(Properties config) {
        for (String propertyName : config.stringPropertyNames()) {
            if (propertyName.startsWith(BEGGINS)) {
                String value = config.getProperty(propertyName);
                this.replacements.put(propertyName.replaceFirst("FILE_REPLACER_", ""), Paths.get(value, new String[0]));
            }
        }
        System.out.println("FileReplacerHandler: " + String.valueOf(this.replacements));
    }

    @Override
    public void handle(WatchKey key, WatchEvent<Path> ev, Path path) throws IOException {
        if (ev != null && ev.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
            String fileToReplace = path.getFileName().toString();
            Path replacer = this.replacements.get(fileToReplace);
            if (replacer != null) {
                Files.move(replacer, path, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
                System.out.println(
                        "FileReplacerHandler: " + String.valueOf(path) + " replaced by " + String.valueOf(replacer));
            }
        }
    }
}
