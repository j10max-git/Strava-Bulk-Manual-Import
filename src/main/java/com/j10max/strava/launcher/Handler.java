package com.j10max.strava.launcher;

public abstract class Handler {

    public Handler() {

    }

    public abstract void onEnable(Launcher launcher);
    public abstract void onDisable(Launcher launcher);

    public void enable(Launcher launcher) {
        this.onEnable(launcher);
    }

    public void disable(Launcher launcher) {
        this.onDisable(launcher);
    }

}
