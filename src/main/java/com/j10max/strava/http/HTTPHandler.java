package com.j10max.strava.http;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.j10max.strava.StravaLauncher;
import com.j10max.strava.file.entry.Entry;
import com.j10max.strava.file.entry.EntryResult;
import com.j10max.strava.file.entry.EntryType;
import com.j10max.strava.launcher.Handler;
import com.j10max.strava.launcher.Launcher;
import com.j10max.strava.settings.Settings;
import com.j10max.strava.util.Ansi;
import com.j10max.strava.util.Console;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HTTPHandler extends Handler {

    private WebClient webClient;


    @Override
    public void onEnable(Launcher launcher) {
        Console.info(String.format("Initialising Web Client: %s", Ansi.GREEN + " CHECK"), false);
        webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
        webClient.getCookieManager().setCookiesEnabled(true);
    }

    @Override
    public void onDisable(Launcher launcher) {

    }

    public void startUpload() {
        new Thread() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();

                List<Entry> entryList = new ArrayList<>();

                /*  */
                Console.info("Removing Failed manual entries");
                for (int i = 0; i < StravaLauncher.instance.entries.size(); i++) {
                    EntryResult result = StravaLauncher.instance.entries.get(i);
                    if (result.result()) {
                        entryList.add(result.entry());
                    }
                }

                Console.info("Beginning Upload process to Strava.net");

                long averageTime = 0;
                for (int i = 0; i < entryList.size(); i++) {
                    long entryTime = System.currentTimeMillis();

                    Entry entry = entryList.get(i);

                    /* Start Singular Entry Upload */
                    try {
                        // Retrieve Manual entry page
                        HtmlPage page = webClient.getPage("https://strava.com/upload/manual");
                        webClient.waitForBackgroundJavaScript(5000);

                        // Find submit form
                        HtmlForm form = (HtmlForm) page.getElementById("new_activity");

                        // Entry Distance
                        form.getInputByName("activity[distance]").setValueAttribute(String.format("%s", Math.round(entry.distance)));
                        form.getInputByName("activity[distance]").setAttribute("class", "valid");
                        form.getInputByName("activity[distance_unit]").setValueAttribute((String) Settings.DISTANCE_UNIT.value()); // Force Distance Unit

                        // Entry Duration
                        if (entry.duration == null) {
                            System.out.println(entry.title());
                        }
                        String[] duration = entry.duration.split(":");
                        form.getInputByName("activity[elapsed_time_hours]").setValueAttribute(duration[0]);
                        form.getInputByName("activity[elapsed_time_minutes]").setValueAttribute(duration[1]);
                        form.getInputByName("activity[elapsed_time_seconds]").setValueAttribute(duration[2]);

                        // Entry Elevation
                        form.getInputByName("activity[elev_gain]").setValueAttribute(String.format("%s", Math.round(entry.elevation))); // Ensure whole number
                        form.getInputByName("activity[elev_gain]").setAttribute("class", "valid");
                        form.getInputByName("activity[elevation_unit]").setValueAttribute((String) Settings.ELEVATION_UNIT.value()); // Force Feet (UK)

                        // Entry Descriptive
                        form.getInputByName("activity[name]").setValueAttribute(entry.title());
                        form.getTextAreaByName("activity[description]").setText(entry.description());

                        // Entry Type
                        form.getInputByName("activity[type]").setValueAttribute(entry.type().value());

                        // Entry Timestamp
                        form.getInputByName("activity[start_date]").setValueAttribute(entry.date);
                        form.getInputByName("activity[start_time_of_day]").setValueAttribute(entry.time);

                        // Entry Timestamp (What POST actually checks)
                        try {
                            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(entry.date + " " + entry.time);
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                            form.getInputByName("activity[start_date_iso_8601]").setValueAttribute(df.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        // Entry Is Private?
                        for (HtmlInput input : form.getInputsByName("activity[private]")) { // Hidden input aswell
                            input.setValueAttribute(entry.privated ? "1" : "0");
                        }

                        // TODO - Bike / Shoe ID
                        if (entry.type() == EntryType.Run) {
                            form.getInputByName("activity[athlete_gear_id]").setValueAttribute(String.valueOf(entry.shoeID));
                        } else  if (entry.type() == EntryType.Ride) {
                            form.getInputByName("activity[bike_id]").setValueAttribute(String.valueOf(entry.bikeID));
                        }

                        /* Simulate Form Submit click */
                        HtmlPage page2 = form.getInputByValue("Create").click();

                        long entryTimeTaken = (System.currentTimeMillis() - entryTime);
                        averageTime = (averageTime + entryTimeTaken) / 2;  // Estimate Average Time across all entries

                        /* Inform Entry Performance */
                        Console.printBox(
                                "Entry Upload Complete",
                                "Time Taken: " + getDurationBreakdown(entryTimeTaken),
                                "Title: " + entry.title(),
                                "Type: " + entry.type().value(),
                                "Date: " + entry.date,
                                "Duration: " + entry.duration,
                                "Distance: " + entry.distance,
                                "Hidden: " + (entry.privated ? "yes" : "no")
                        );
                        Console.info("Estimated time left: " + getDurationBreakdown(averageTime * (entryList.size() - i)), false);
                        Console.info("Elapsed time: " + getDurationBreakdown(System.currentTimeMillis() - start), true);
                        Console.info("Remaining (" + (entryList.size() - i) + "/" + entryList.size() + ")");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Console.printBox(
                        "Manual Entries Upload Complete",
                        "Entries: " + entryList.size(),
                        "Time Taken: " + getDurationBreakdown(System.currentTimeMillis() - start),
                        "Average time taken per entry: " + getDurationBreakdown(averageTime)
                );

            }
        }.start();
    }

    /**
     * Convert a millisecond duration to a string format
     *
     * @param millis A duration to convert to a string form
     * @return A string of the form "X Days Y Hours Z Minutes A Seconds".
     */
    public static String getDurationBreakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append(" Days ");
        sb.append(hours);
        sb.append(" Hours ");
        sb.append(minutes);
        sb.append(" Minutes ");
        sb.append(seconds);
        sb.append(" Seconds");

        return (sb.toString());
    }

    public HtmlPage loginToStrava(String email, String password) throws IOException {
        Console.info(String.format("Logging into Strava.net using: %s,%s", email, password), false);

        HtmlPage page = webClient.getPage("https://strava.com/login");

        HtmlForm form = page.getHtmlElementById("login_form");

        // Input Fields
        form.getInputByName("email").setValueAttribute(email);
        form.getInputByName("password").setValueAttribute(password);


        HtmlButton submitButton = (HtmlButton) page.getElementById("login-button");
        submitButton.click();

        return submitButton.click();
    }

    private List<HtmlInput> getInputs(DomNode node) {
        Iterable<DomNode> children = node.getChildren();
        List<HtmlInput> inputList = new ArrayList<HtmlInput>();
        for (DomNode child : children) {
            if (child instanceof HtmlInput) {
                inputList.add((HtmlInput) child);
            }
            if (child.hasChildNodes()) {
                List<HtmlInput> returnValue = getInputs(child);
                for (DomNode pass : returnValue) {
                    inputList.add((HtmlInput) pass);
                }
            }
        }
        return inputList;
    }

}
