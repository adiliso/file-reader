package com.adil.filereader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MonitoringApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MonitoringApp.class.getResource("reader-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 650, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        stage.setTitle("Monitoring App");
        stage.setScene(scene);
        stage.setOnCloseRequest(windowEvent -> ViewController.stopMonitoring());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}