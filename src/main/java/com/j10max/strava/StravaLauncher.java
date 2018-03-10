package com.j10max.strava;


import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.j10max.strava.file.FileHandler;
import com.j10max.strava.file.entry.EntryResult;
import com.j10max.strava.http.HTTPHandler;
import com.j10max.strava.launcher.Launcher;
import com.j10max.strava.settings.Settings;
import com.j10max.strava.util.Console;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class StravaLauncher {

    public static final String NAME = "Strava Bulk Upload";
    public static final String VERSION = "1.0";

    public static StravaLauncher instance;

    private Launcher launcher;

    private HTTPHandler http;
    private FileHandler file;

    public List<EntryResult> entries;

    public StravaLauncher() {
        instance = this;
        this.launcher = new Launcher();
    }

    public static void main(String[] args) {
        new StravaLauncher().launch(args);
    }

    public void launch(String[] args) {
        System.out.println("   ______                       ____                    __ ");
        System.out.println("  / __/ /________ __  _____ _  /  _/_ _  ___  ___  ____/ /_");
        System.out.println(" _\\ \\/ __/ __/ _ `/ |/ / _ `/ _/ //  ' \\/ _ \\/ _ \\/ __/ __/");
        System.out.println("/___/\\__/_/  \\_,_/|___/\\_,_/ /___/_/_/_/ .__/\\___/_/  \\__/ ");
        System.out.println("                                      /_/                  ");

        Console.info(String.format("[Starting %s]", NAME));
        Console.whitespace();

        Console.info("========= Startup Checks =========");

        this.launcher.registerHandler(this.http = new HTTPHandler());
        this.launcher.registerHandler(this.file = new FileHandler());

        this.launcher.init(this);

        Console.whitespace();

        Console.info(String.format("========= Starting %s =========", NAME));

        /* Scanner */
        Scanner scanner = new Scanner(System.in);

        /* Loop Login */
        boolean login = false;

        while (!login) {
            Console.action("Enter your email address: ", false);
            String emailAddress = scanner.next();

            Console.action("Enter your password: ", false);
            String password = scanner.next();

            Console.whitespace();

            try {
                HtmlPage page = this.http().loginToStrava(emailAddress, password);
                if (!page.getUrl().toString().contains("login")) {
                    Console.info("Logged in successfully to strava.com");
                    login = true;
                } else {
                    Console.info("Log in failed to strava.com");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Console.info("========= Import Manual Entries Settings =========");

        while (true) {
            Console.action("Select a default time of day (hh:mm): ", false);
            String value = scanner.next();


            Date date = null;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                date = sdf.parse(value);
                if (!value.equals(sdf.format(date))) {
                    date = null;
                }
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            if (date != null) {
                Settings.DEFAULT_TIME.value(value);
                break;
            }
        }

        Console.whitespace();

        /* Exists CSV File */
        boolean fileExists = false;
        File csvFile = null;

        while (true) {
            Console.action("Enter file location for CSV file: ", false);
            String fileLocation = scanner.next();

            csvFile = new File(fileLocation);
            if (csvFile.exists()) {
                break;
            }
        }

        Console.whitespace();

        /* Import CSV File */
        try {
            Console.info("Importing entries CSV: " + csvFile.toPath().toString(), false);
            this.entries = this.file.parseCSVFile(csvFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int entriesSuccess = 0, entriesFail = 0;
        for (EntryResult result : this.entries) {
            if (result.result()) {
                entriesSuccess++;
            } else {
                entriesFail++;
            }
        }
        Console.info(String.format("Manual entries: Success[%s]  Failed[%s]", entriesSuccess, entriesFail), true);

        if (entriesFail > 0) {
            Console.action("Do you wish to see the errors that occurred (Y/N): ");
            String proceed = scanner.next();
            if (proceed.equalsIgnoreCase("Y")) {
                Console.whitespace();
                int index = 1;
                for (EntryResult result : this.entries) {
                    if (!result.result()) {
                        if (result.message() != null) {
                            Console.info(String.format("Item %s: %s", index, result.message()), false);
                        }
                    }
                    index++;
                }
                Console.whitespace();
            }
        }

        Console.info("========= Upload to Strava Settings =========");

        while (true) {
            Console.action("Choose your distance field unit (miles/meters/kilometers/yards): ", false);
            String distanceUnit = scanner.next();
            if (distanceUnit.equalsIgnoreCase("kilometers")) {
                Settings.DISTANCE_UNIT.value("km");
                break;
            } else if (distanceUnit.equalsIgnoreCase("meters")) {
                Settings.DISTANCE_UNIT.value("m");
                break;
            } else if (distanceUnit.equalsIgnoreCase("miles")) {
                Settings.DISTANCE_UNIT.value("mi");
                break;
            } else if (distanceUnit.equalsIgnoreCase("yards")) {
                Settings.DISTANCE_UNIT.value("yd");
                break;
            }
        }

        while (true) {
            Console.action("Choose your elevation field unit (feet/meters): ", false);
            String elevationUnit = scanner.next();
            if (elevationUnit.equalsIgnoreCase("meters")) {
                Settings.ELEVATION_UNIT.value("m");
                break;
            } else if (elevationUnit.equalsIgnoreCase("feet")) {
                Settings.ELEVATION_UNIT.value("ft");
                break;
            }
        }

        Console.whitespace();

        Console.action("Start Upload process (Y): ");
        scanner.next();

        Console.whitespace();

        this.http.startUpload();

    }

    public HTTPHandler http() {
        return http;
    }

    public FileHandler file() {
        return file;
    }

}
