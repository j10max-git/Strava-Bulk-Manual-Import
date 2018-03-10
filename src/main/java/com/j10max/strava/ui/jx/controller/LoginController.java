package com.j10max.strava.ui.jx.controller;


import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.j10max.strava.StravaLauncher;
import com.j10max.strava.ui.jx.MainApp;
import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class LoginController {

    /**
     * Reference to the main application
     */
    private MainApp mainJX;

    public LoginController() {
    }

    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Button loginBtn;
    @FXML
    public CheckBox rememberCheckbox;
    @FXML
    public Button closeBtn;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        this.mainJX.getPrimaryStage().close();
    }

    @FXML
    private void handleLoginAction(ActionEvent event) {
        boolean failed = false;
        { /* Field Validations */
            // Email field
            if (StringUtils.isBlank(loginField.getText()) || !loginField.getText().contains("@")) {
                failed = true;
                loginField.setStyle("-fx-border-color: red;");
            }
            // Password field
            if (StringUtils.isBlank(passwordField.getText())) {
                failed = true;
                passwordField.setStyle("-fx-border-color: red;");
            }
        }
        if (!failed) {
            try {
                HtmlPage result = StravaLauncher.instance.http().loginToStrava(loginField.getText(), passwordField.getText());
                // Check redirection success
                if (!result.getUrl().toString().contains("login")){
                    // success

                    loginBtn.setStyle("-fx-background-color: #72e301;");
                } else {
                    // failed
                    loginBtn.setStyle("-fx-background-color: #e30101;");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

        }
        /*
         FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Manual Entries CSV File");
                File file = fileChooser.showOpenDialog(stage);

                if (file.exists()) {
                    long start = System.currentTimeMillis();
                    List<EntryResult> entries = null;
                    try {
                        entries = StravaLauncher.instance.file().parseCSVFile(file);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    long timeTaken = System.currentTimeMillis() - start;

                    if (entries != null) {
                        StravaLauncher.instance.entries = entries;

                        String label = "";
                        label += String.format("File found:  %s", file.getPath()) + "\n";
                        label += String.format("Time taken to import manual entries:  %sms", timeTaken) + "\n";
                        label += String.format("Manual entries found:  %s", entries.size()) + "\n";

                        detailLabel.setText(
                                label
                        );
                        panel.loginTab.setDisable(false);
                    }
                }
         */
    }

    public void setMainJX(MainApp mainJX) {
        this.mainJX = mainJX;
    }


}
