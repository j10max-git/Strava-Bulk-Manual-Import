package com.j10max.strava.ui.jx;

import com.j10max.strava.ui.jx.controller.LoginController;
import com.j10max.strava.ui.jx.controller.MainController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.IOException;

public class MainApp extends Application {

    private Stage primaryStage;
    private AnchorPane rootLayout;

    @FXML
    public Button closeButton;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("StravaLauncher Bulk Import");

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.resizableProperty().setValue(Boolean.FALSE);

        initLayout();

        showLoginOverview();
    }

    private void initLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/RootLayout.fxml"));
            this.rootLayout = (AnchorPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(this.rootLayout);
            primaryStage.setScene(scene);

            initDraggable(this.rootLayout);

            // Reference to main application
            MainController controller = new MainController();
            controller.setMainJX(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLoginOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/Login.fxml"));
            AnchorPane loginOverview = (AnchorPane) loader.load();

            Scene scene = new Scene(loginOverview);

            this.primaryStage.setScene(scene);

            initDraggable(loginOverview);

            // Reference to main application
            LoginController controller = new LoginController();
            controller.setMainJX(this);


            this.primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double xOffset = 0;
    private static double yOffset = 0;


    private void initDraggable(AnchorPane pane) {
        pane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = primaryStage.getX() - event.getScreenX();
                yOffset = primaryStage.getY() - event.getScreenY();
            }
        });
        pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() + xOffset);
                primaryStage.setY(event.getScreenY() + yOffset);
            }
        });
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void init(String[] args) {
        launch(args);
    }

}
