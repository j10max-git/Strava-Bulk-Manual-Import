package com.j10max.strava.file;

import com.j10max.strava.file.entry.Entry;
import com.j10max.strava.file.entry.EntryResult;
import com.j10max.strava.file.entry.EntryType;
import com.j10max.strava.launcher.Handler;
import com.j10max.strava.launcher.Launcher;
import com.j10max.strava.settings.Settings;
import com.j10max.strava.util.*;
import javafx.beans.property.SimpleStringProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler extends Handler {

    @Override
    public void onEnable(Launcher launcher) {
        com.j10max.strava.util.Console.info(String.format("Import Utility: %s", Ansi.GREEN + " CHECK"), false);
    }

    @Override
    public void onDisable(Launcher launcher) {
    }


    public List<EntryResult> parseCSVFile(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException(file.getPath() + " does not exist");
        }

        // List of Manual Entry Results
        List<EntryResult> entryResults = new ArrayList<>();

        /*
            Read Entries CSV file format
         */
        BufferedReader br;
        String line;
        br = new BufferedReader(new FileReader(file));
        int index = 0;
        while ((line = br.readLine()) != null) {
            // Ignore first CSV line
            if (index == 0) {
                index++;
                continue;
            }

            // Split by (CSV)
            String[] split = line.split(",");

            if (split.length < 10) {
                EntryResult result = new EntryResult(false, "Entry not in the correct format");
                entryResults.add(result);
                continue;
            }

            // Prepare Entry initial requirements
            String title = split[3];
            if (StringUtils.isBlank(title)) {
                EntryResult result = new EntryResult(false, "Title field is blank");
                entryResults.add(result);
                continue;
            }

            // Description
            String description = split[8];
            // Description can be blank

            // Type
            if (StringUtils.isBlank(split[5])) {
                EntryResult result = new EntryResult(false, "Type field is blank");
                entryResults.add(result);
                continue;
            }
            EntryType type = EntryType.parseEntryType(split[5]);
            if (type == null) {
                EntryResult result = new EntryResult(false, String.format("Type %s invalid", split[5]));
                entryResults.add(result);
                continue;
            }

            /*
                Create Entry
             */
            Entry entry = new Entry(title, type, description);
            {
                // Date
                if (StringUtils.isBlank(split[0])) {
                    EntryResult result = new EntryResult(entry, false, "Date field is blank");
                    entryResults.add(result);
                    continue;
                }
                entry.date = split[0].trim();

                // Timestamp
                if (!split[1].equals("") && !split[1].isEmpty()) {
                    entry.time = split[1];
                } else {
                    entry.time = (String) Settings.DEFAULT_TIME.value();
                }

                // Duration
                // i.e. 00:35:15 - hh:mm:ss
                String duration = split[2];
                if (StringUtils.isBlank(duration) || !duration.contains(":") || duration.split(":").length < 2) {
                    EntryResult result = new EntryResult(entry, false, "Duration field incorrect format");
                    entryResults.add(result);
                    continue;
                }
                entry.duration = duration;

                // Distance
                if (StringUtils.isBlank(split[4])) {
                    EntryResult result = new EntryResult(entry, false, "Distance field is blank");
                    entryResults.add(result);
                    continue;
                }

                try {
                    entry.distance = Float.parseFloat(split[4].trim());
                } catch (NumberFormatException exp) {
                    EntryResult result = new EntryResult(entry, false, exp.getMessage());
                    entryResults.add(result);
                    continue;
                }
                if (entry.distance <= 0) {
                    EntryResult result = new EntryResult(entry, false, "Distance must be greater than 0");
                    entryResults.add(result);
                    continue;
                }

                // Type

                // Elevation
                if (split[6].isEmpty()) {
                    entry.elevation = 0;
                } else {
                    try {
                        entry.elevation = Float.parseFloat(split[6].trim());
                    } catch (NumberFormatException exp) {
                        EntryResult result = new EntryResult(entry, false, exp.getMessage());
                        entryResults.add(result);
                        continue;
                    }
                }

                // Bike or Shoes (ID)
                if (!StringUtils.isBlank(split[7])) {
                    if (NumberUtils.isNumber(split[7])) {
                        if (entry.type() == EntryType.Run) {
                            entry.bikeID = Integer.parseInt(split[7]);
                        } else if (entry.type() == EntryType.Ride) {
                            entry.shoeID = Integer.parseInt(split[7]);
                        }
                    } else {
                        EntryResult result = new EntryResult(entry, false, "Bike/Shoe ID field is not a number");
                        entryResults.add(result);
                        continue;
                    }
                }
                // Private
                if (!StringUtils.isBlank(split[9])) {
                    entry.privated = split[9].trim().equals("1") || split[9].trim().equalsIgnoreCase("yes")  || split[9].trim().equalsIgnoreCase("true");
                } else {
                    EntryResult result = new EntryResult(entry, false, "Private field is blank");
                    entryResults.add(result);
                    continue;
                }
            }
            entryResults.add(new EntryResult(entry, true));
        }
        return entryResults;
    }

}
