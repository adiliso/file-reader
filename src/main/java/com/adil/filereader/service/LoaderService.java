package com.adil.filereader.service;

import com.adil.filereader.model.StockDataModel;
import com.adil.filereader.util.AppUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Consumer;

import static com.adil.filereader.util.AppUtils.getFileExtension;

public interface LoaderService {

    void load(File file, Consumer<StockDataModel> processor);

    default Optional<LoaderService> getLoaderService(File file) {
        String fileExtension = getFileExtension(file.getPath());
        return switch (fileExtension) {
            case "txt" -> Optional.of(new TxtLoaderService());
            case "csv" -> Optional.of(new CsvLoaderService());
            case "xml" -> Optional.of(new XmlLoaderService());
            default -> Optional.empty();
        };
    }
}
