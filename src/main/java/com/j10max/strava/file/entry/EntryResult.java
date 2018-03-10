package com.j10max.strava.file.entry;

public class EntryResult {

    private Entry entry;
    private boolean result;
    private String message;

    public EntryResult(Entry entry, boolean result, String message) {
        this.entry = entry;
        this.result = result;
        this.message = message;
    }

    public EntryResult(Entry entry, boolean result) {
        this(entry, result, null);
    }

    public EntryResult(boolean result, String message) {
        this(null, result, message);
    }

    public Entry entry() {
        return entry;
    }

    public boolean result(){
        return result;
    }

    public String message(){
        return message;
    }

}
