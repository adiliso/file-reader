package com.adil.filereader.service.impl;

import com.adil.filereader.model.StockDataModel;
import com.adil.filereader.service.LoaderService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;

import static com.adil.filereader.util.AppUtils.log;

public class TxtLoaderService implements LoaderService {

    @Override
    public void load(File file, Consumer<StockDataModel> processor) {
        try (FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
             BufferedReader reader = new BufferedReader(Channels.newReader(channel, StandardCharsets.UTF_8))) {
            reader.lines()
                    .skip(1)
                    .map(this::parseLine)
                    .forEach(processor);
        } catch (IOException e) {
            log(e.getMessage());
        }
    }

    private StockDataModel parseLine(String line) {
        String[] fields = line.trim().split(";");
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
