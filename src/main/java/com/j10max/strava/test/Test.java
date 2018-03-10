package com.j10max.strava.test;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        try {
            new Test().init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    WebClient webClient;

    private void init() throws IOException {
        webClient = new WebClient(BrowserVersion.CHROME);

        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getCookieManager().setCookiesEnabled(true);

        login();

        entry();
    }

    private void entry() throws IOException{
        long start = System.currentTimeMillis();

        HtmlPage page = webClient.getPage("https://strava.com/upload/manual");
        HtmlForm form = page.getHtmlElementById("new_activity");

        // KiloMetres



        form.getInputByName("activity[distance]").setAttribute("step", "any");
        form.getInputByName("activity[distance]").setValueAttribute("4");

        form.getInputByName("activity[elapsed_time_hours]").setValueAttribute("01");
        form.getInputByName("activity[elapsed_time_minutes]").setValueAttribute("50");
        form.getInputByName("activity[elapsed_time_seconds]").setValueAttribute("32");

        form.getInputByName("activity[elev_gain]").setValueAttribute("0");

        form.getInputByName("activity[name]").setValueAttribute("Hello world");
        form.getTextAreaByName("activity[description]").setText("Random morning run");

        form.getInputByName("activity[type]").setValueAttribute("Run");

        form.getInputByName("activity[start_date]").setValueAttribute("02/03/2015");
        form.getInputByName("activity[start_time_of_day]").setValueAttribute("5:30 PM");

        form.getInputByName("workout_type_run").setValueAttribute("1");
        form.getInputByName("activity[private]").setValueAttribute("1");

        HtmlPage page2 = form.getInputByValue("Create").click();
        System.out.println(page2.asText());
        System.out.println("duration: " + (System.currentTimeMillis() - start));
    }

    private void login() throws IOException {
        HtmlPage page = webClient.getPage("https://strava.com/login");

        HtmlForm form = page.getHtmlElementById("login_form");

        // Input Fields
        form.getInputByName("email").setValueAttribute("cubedxyz@gmail.com");
        form.getInputByName("password").setValueAttribute("c10max");

        for (HtmlInput input : getInputs(form)) {
            System.out.println(input.getNameAttribute() + ":" + input.getValueAttribute());
        }

        HtmlButton submitButton = (HtmlButton) page.getElementById("login-button");
        HtmlPage page2 = submitButton.click();
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
