package com.j10max.strava.ui;

import com.j10max.strava.ui.jx.MainApp;
import com.j10max.strava.launcher.Handler;
import com.j10max.strava.launcher.Launcher;

public class UIHandler extends Handler {

    public MainApp app;

    @Override
    public void onEnable(Launcher launcher) {
        app = new MainApp();
        app.init(new String[]{""});
    }

    @Override
    public void onDisable(Launcher launcher) {

    }

}
