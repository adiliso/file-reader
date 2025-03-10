package com.adil.filereader.util;

import com.adil.filereader.logger.TextAreaLogger;
import com.adil.filereader.service.LoaderService;
import com.adil.filereader.service.impl.CsvLoaderService;
import com.adil.filereader.service.impl.TxtLoaderService;
import com.adil.filereader.service.impl.XmlLoaderService;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public final class AppUtils {

    private AppUtils() {
    }

    private static int checkingInterval = 5;

    public static int getCheckingInterval() {
        return checkingInterval;
    }

    public static void setCheckingInterval(int checkingInterval) {
        AppUtils.checkingInterval = checkingInterval;
    }

    public static int getValidInterval(TextField textField) {
        String text = textField.getText();
        try {
            int interval = Integer.parseInt(text);
            if (interval < 1 || interval > 3600) {
                showErrorAlert("Please enter number in (1 - 3600) interval");
                return -1;
            }
            return interval;
        } catch (NumberFormatException e) {
            showErrorAlert("Please enter a valid number");
            return -1;
        }
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

    public static Optional<LoaderService> getLoaderService(File file) {
        String fileExtension = getFileExtension(file.getPath());
        return switch (fileExtension) {
            case "txt" -> Optional.of(new TxtLoaderService());
            case "csv" -> Optional.of(new CsvLoaderService());
            case "xml" -> Optional.of(new XmlLoaderService());
            default -> Optional.empty();
        };
    }

    public static boolean isValidDirectory(String path) {
        File file = new File(path);
        if (path.isEmpty()) {
            log("Enter a director!\n");
            return false;
        }
        if (!file.exists()) {
            log("Directory does not exist: " + path);
            return false;
        }
        if (!file.isDirectory()) {
            log(path + " is not a directory");
            return false;
        }
        if (!file.canRead()) {
            log(path + " is not readable");
        }
        return true;
    }

    public static void log(String text) {
        TextAreaLogger.getInstance().log(text);
    }
}
