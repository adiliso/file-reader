package com.adil.filereader;

import com.adil.filereader.controller.ViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MonitoringApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MonitoringApp.class.getResource("reader-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 650, 800);
        stage.setTitle("Monitoring App");
        stage.setScene(scene);
        stage.setOnCloseRequest(windowEvent -> ViewController.stopMonitoring());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}