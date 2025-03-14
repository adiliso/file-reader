package com.adil.filereader.service;

import com.adil.filereader.model.StockDataModel;
import javafx.application.Platform;
import javafx.scene.control.TableView;

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
import static com.adil.filereader.util.AppUtils.log;

public class MonitoringService {
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private volatile boolean monitoringActive = false;

    public void monitorDirectory(String directoryPath,
                                 TableView<StockDataModel> tableView) {
        if (!isValidDirectory(directoryPath)) {
            return;
        }

        if (monitoringActive) {
            log("Directory monitoring is already active.");
            return;
        }

        monitoringActive = true;
        Path pathToWatch = Paths.get(directoryPath);

        executorService.submit(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                pathToWatch.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

                log("Monitoring directory: " + directoryPath);
                log("Checking interval: " + getCheckingInterval() + " s");

                while (monitoringActive) {
                    TimeUnit.SECONDS.sleep(getCheckingInterval());

                    WatchKey key = watchService.take();

                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            Path eventPath = pathToWatch.resolve((Path) event.context());
                            log("New file detected: " + eventPath);

                            File file = eventPath.toFile();
                            Optional<LoaderService> optionalLoaderService = getLoaderService(file);
                            TimeUnit.MILLISECONDS.sleep(10);
                            optionalLoaderService.ifPresent(loaderService ->
                                    loaderService.load(file, data ->
                                            Platform.runLater(() -> tableView.getItems().add(data))
                                    )
                            );
                        }
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        log("WatchKey no longer valid. Exiting.");
                        break;
                    }
                }
            } catch (IOException e) {
                log("Failed to initialize WatchService: " + e.getMessage());
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

    public void restartMonitoring(String directoryPath, TableView<StockDataModel> tableView) {
        stopMonitoring();
        executorService = Executors.newSingleThreadExecutor();
        monitorDirectory(directoryPath, tableView);
    }

    public boolean isMonitoringActive() {
        return monitoringActive;
    }
}
