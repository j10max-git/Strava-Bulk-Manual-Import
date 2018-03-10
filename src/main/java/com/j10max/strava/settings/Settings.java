package com.j10max.strava.settings;

public enum Settings {

    DISTANCE_UNIT("mi"),
    ELEVATION_UNIT("ft"),
    DEFAULT_TIME("10:00"),
    THREAD_COUNT(2);


    private Object value;

    Settings(Object value) {
        this.value = value;
    }

    public Object value() {
        return value;
    }

    public void value(String value) {
        this.value = value;
    }

}
