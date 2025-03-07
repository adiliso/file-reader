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

public class ReaderController {
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private LoaderService loaderService;
    private volatile boolean monitoringActive = false;

    public void monitorDirectory(String directoryPath,
                                 TextArea info,
                                 TableView<StockDataModel> tableView) {
        if (monitoringActive) {
            info.appendText("Directory monitoring is already active.\n");
            return;
        }

        info.appendText("Checking interval: " + getCheckingInterval() + "\n");

        monitoringActive = true;
        Path pathToWatch = Paths.get(directoryPath);

        executorService.submit(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                pathToWatch.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

                info.appendText("Monitoring directory: " + directoryPath + "\n");

                while (monitoringActive) {
                    try {
                        WatchKey key = watchService.poll(getCheckingInterval(), TimeUnit.SECONDS);

                        if (key == null) {
                            continue; // No events, continue monitoring
                        }

                        for (WatchEvent<?> event : key.pollEvents()) {
                            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                                Path eventPath = pathToWatch.resolve((Path) event.context());

                                info.appendText("New file detected: " + eventPath + "\n");

                                File file = eventPath.toFile();

                                Optional<LoaderService> optionalLoaderService = getLoaderService(file);
                                if (optionalLoaderService.isEmpty()) {
                                    continue;
                                }
                                loaderService = optionalLoaderService.get();

                                // Read and display the file content
                                loaderService.load(file, data -> Platform.runLater(() -> tableView.getItems().add(data)));
                            }
                        }

                        boolean valid = key.reset();
                        if (!valid) {
                            info.appendText("WatchKey no longer valid. Exiting.\n");
                            break;
                        }

                    } catch (InterruptedException e) {
                        info.appendText("Error A: " + e.getMessage() + "\n");
                    }
                }

            } catch (IOException e) {
                info.appendText("Failed to initialize WatchService: " + e.getMessage() + "\n");
            } finally {
                monitoringActive = false; // Reset the flag when done
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
}
