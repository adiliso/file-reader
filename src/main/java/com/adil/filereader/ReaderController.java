package com.adil.filereader;

import com.adil.filereader.model.StockDataModel;
import com.adil.filereader.service.LoaderService;
import javafx.application.Platform;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.adil.filereader.util.AppUtils.getCheckingInterval;
import static com.adil.filereader.util.AppUtils.getLoaderService;
import static com.adil.filereader.util.AppUtils.isValidDirectory;

public class ReaderController {
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private volatile boolean monitoringActive = false;

    public void monitorDirectory(String directoryPath,
                                 TextArea info,
                                 TableView<StockDataModel> tableView) {
        if (!isValidDirectory(directoryPath, info)) {
            return;
        }

        if (monitoringActive) {
            info.appendText("Directory monitoring is already active.\n");
            return;
        }

        monitoringActive = true;
        Path pathToWatch = Paths.get(directoryPath);

        executorService.submit(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                pathToWatch.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

                info.appendText("Monitoring directory: " + directoryPath + "\n");
                info.appendText("Checking interval: " + getCheckingInterval() + " s\n");

                while (monitoringActive) {
                    WatchKey key = watchService.take(); // Blocks until an event occurs

                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            Path eventPath = pathToWatch.resolve((Path) event.context());
                            info.appendText("New file detected: " + eventPath + "\n");

                            File file = eventPath.toFile();
                            Optional<LoaderService> optionalLoaderService = getLoaderService(file);
                            optionalLoaderService.ifPresent(loaderService ->
                                    loaderService.load(file, data ->
                                            Platform.runLater(() -> tableView.getItems().add(data))
                                    )
                            );
                        }
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        info.appendText("WatchKey no longer valid. Exiting.\n");
                        break;
                    }

                    TimeUnit.SECONDS.sleep(getCheckingInterval());
                }
            } catch (IOException e) {
                Platform.runLater(() -> info.appendText("Failed to initialize WatchService: " + e.getMessage() + "\n"));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                monitoringActive = false;
            }
        });
    }

    public void stopMonitoring() {
        monitoringActive = false;
        executorService.shutdownNow();
    }

    public void restartMonitoring(String directoryPath, TextArea info, TableView<StockDataModel> tableView) {
        stopMonitoring();
        executorService = Executors.newSingleThreadExecutor();
        monitorDirectory(directoryPath, info, tableView);
    }

    public boolean isMonitoringActive() {
        return monitoringActive;
    }
}
