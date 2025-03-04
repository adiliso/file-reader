package com.adil.filereader.service;

import com.adil.filereader.model.StockDataModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CsvLoaderService implements LoaderService {

    @Override
    public void load(File file, Consumer<StockDataModel> processor) {
        try (Stream<String> lines = Files.lines(file.toPath())) {
            lines
                    .map(this::parseLine)
                    .forEach(processor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StockDataModel parseLine(String line) {
        String[] fields = line.split(",");
        return new StockDataModel(
                fields[0],
                Double.parseDouble(fields[1]),
                Double.parseDouble(fields[2]),
                Double.parseDouble(fields[3]),
                Double.parseDouble(fields[4]),
                Long.parseLong(fields[5])
        );
    }
}
