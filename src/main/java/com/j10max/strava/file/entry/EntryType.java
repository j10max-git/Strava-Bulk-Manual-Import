package com.j10max.strava.file.entry;

public enum EntryType {

    Ride("Ride"), Run("Run"), Swim("Swim"), Hike("Hike"), Walk("Walk"), ASKI("Alpine Ski"),
    BSKI("Backcountry Ski"), Canoeing("Canoeing"), Crossfit("Crossfit"), EBIKERIDE("E-Bike Ride"), Elliptical("Elliptical"), Handcycle("Handcycle");


    private String value;

    EntryType(String value) {
        this.value = value;
    }

    public static EntryType parseEntryType(String entryValue) {
        for (EntryType type : EntryType.values()) {
            if (type.value().equalsIgnoreCase(entryValue)){
                return type;
            }
        }
        return null;
    }

    public String value(){
        return this.value;
    }

}
