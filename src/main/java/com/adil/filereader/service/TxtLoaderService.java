package com.adil.filereader.service;

import com.adil.filereader.model.StockDataModel;

import java.io.File;
import java.util.function.Consumer;

public class TxtLoaderService implements LoaderService{

    @Override
    public void load(File file, Consumer<StockDataModel> processor) {

    }
}
