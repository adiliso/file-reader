package com.adil.filereader;

import com.adil.filereader.model.StockDataModel;
import com.adil.filereader.service.CsvLoaderService;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.adil.filereader.util.AppUtils.CHECKING_INTERVAL;

public class ReaderController {
    private ExecutorService executorService = Executors.newSingleThreadExecutor(); // Background thread
    private final LoaderService loaderService = new CsvLoaderService();
    private volatile boolean monitoringActive = false;

    public void monitorDirectory(String directoryPath,
                                 TextArea info,
                                 TableView<StockDataModel> tableView) {
        if (monitoringActive) {
            info.appendText("Directory monitoring is already active.\n");
            return;
        }

        info.appendText("Checking interval: " + CHECKING_INTERVAL + "\n");

        monitoringActive = true;
        Path pathToWatch = Paths.get(directoryPath);

        executorService.submit(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                pathToWatch.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

                info.appendText("Monitoring directory: " + directoryPath + "\n");

                while (monitoringActive) {
                    try {
                        WatchKey key = watchService.poll(CHECKING_INTERVAL, TimeUnit.SECONDS);

                        if (key == null) {
                            continue; // No events, continue monitoring
                        }

                        for (WatchEvent<?> event : key.pollEvents()) {
                            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                                Path eventPath = pathToWatch.resolve((Path) event.context());

                                info.appendText("New file detected: " + eventPath + "\n");

                                // Wait a bit to ensure the file is fully written
                                Thread.sleep(500);

                                File file = eventPath.toFile();

                                // Read and display the file content
                                loaderService.load(file, data -> Platform.runLater(() -> tableView.getItems().add(data)));
                            }
                        }

                        boolean valid = key.reset();
                        if (!valid) {
                            info.appendText("WatchKey no longer valid. Exiting.\n");
                            break;
                        }

                    } catch (Exception e) {
                        info.appendText("Error: " + e.getMessage() + "\n");
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
        executorService.shutdownNow(); // Force shutdown
    }

    public void restartMonitoring(String directoryPath, TextArea info, TableView<StockDataModel> tableView) {
        if (!executorService.isShutdown()) {
            executorService.shutdownNow(); // Stop existing tasks
        }
        executorService = Executors.newSingleThreadExecutor(); // Reinitialize
        monitorDirectory(directoryPath, info, tableView); // Restart monitoring
    }

}
