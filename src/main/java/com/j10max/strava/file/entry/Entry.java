package com.j10max.strava.file.entry;

/**
 * Manual Entry
 */
public class Entry {

    // Descriptive
    private String title;
    private EntryType type;
    private String description;

    // Date & Time stamp
    public String date;
    public String time;

    // Run data
    public String duration;
    public float distance;
    public float elevation;

    // Additional
    public int bikeID;
    public int shoeID;

    public boolean privated;

    public Entry(String title, EntryType type, String description) {
        this.title = title;
        this.type = type;
        this.description = description;
    }

    public String title() {
        return title;
    }

    public EntryType type() {
        return type;
    }

    public String description() {
        return description;
    }

}





