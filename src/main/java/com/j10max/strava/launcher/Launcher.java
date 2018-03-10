package com.j10max.strava.launcher;

import com.j10max.strava.StravaLauncher;
import com.j10max.strava.util.Ansi;
import com.j10max.strava.util.Console;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class Launcher {

    private File mainFolder;

    private List<Handler> handlerCache;

    public static Logger logger = Logger.getLogger(StravaLauncher.class.getName());

    public Launcher() {
        this.mainFolder = new File(System.getProperty("user.dir"));

        this.handlerCache = new CopyOnWriteArrayList<>();
    }

    public void init(StravaLauncher strava) {
        // Enable Handlers
        this.enableHandlers();
        Console.info(String.format("Initialising Handlers: %s", Ansi.GREEN + " CHECK"), false);
    }

    public void registerHandler(Handler handler) {
        this.handlerCache.add(handler);
    }

    private void enableHandlers() {
        for (Handler handler : this.handlerCache) {
            handler.enable(this);
        }
    }

    private void disableHandlers() {
        for (Handler handler : this.handlerCache) {
            handler.disable(this);
        }
    }

    public List<Handler> getHandlerCache() {
        return handlerCache;
    }

    public File getFolder() {
        return mainFolder;
    }

}
