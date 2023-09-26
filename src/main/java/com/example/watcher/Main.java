package com.example.watcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            List<EventHandler> handlers = new ArrayList<>();
            Properties config = new Properties();
            try (FileInputStream in = new FileInputStream(new File(args[0]))) {
                config.load(in);
            }
            String[] handlerArgs = config.getProperty("handlers").split(",");
            for (String handler : handlerArgs) {
                Main.HandlerNames hn = Main.HandlerNames.valueOf(handler);
                handlers.add(hn.eventHandler(config));
            }
            Watcher watcher = new Watcher(Paths.get(config.getProperty("rootPath")), handlers);
            watcher.watch();
        } else {
            System.err.println("Requires config.properties as an argument");
        }
    }

    private static enum HandlerNames {
        FILE_REPLACER {
            protected EventHandler eventHandler(Properties config) {
                return new FileReplacerHandler(config);
            }
        };

        protected abstract EventHandler eventHandler(Properties var1);
    }
}