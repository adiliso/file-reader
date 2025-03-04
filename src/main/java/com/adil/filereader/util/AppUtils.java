package com.adil.filereader.util;

import com.adil.filereader.service.LoaderService;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppUtils {

    private AppUtils() {
    }

    public static int CHECKING_INTERVAL = 5;

    public static int getValidInterval(TextField textField) {
        String text = textField.getText();
        int interval;
        try {
            interval = Integer.parseInt(text);
            if (interval < 1 || interval > 3600) {
                showErrorAlert("Please enter number in (1 - 3600) interval");
                return -1;
            }
        } catch (NumberFormatException e) {
            showErrorAlert("Please enter a valid number");
            return -1;
        }

        return interval;
    }

    public static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static String getFileExtension(String filePath) {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        int lastIndex = fileName.lastIndexOf(".");
        return (lastIndex == -1) ? "" : fileName.substring(lastIndex + 1);
    }
}
