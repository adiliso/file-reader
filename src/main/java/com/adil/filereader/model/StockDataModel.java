package com.adil.filereader.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StockDataModel {
    private final StringProperty date;
    private final DoubleProperty open;
    private final DoubleProperty high;
    private final DoubleProperty low;
    private final DoubleProperty close;
    private final LongProperty volume;

    public StockDataModel(String date, double open, double high, double low, double close, long volume) {
        this.date = new SimpleStringProperty(date);
        this.open = new SimpleDoubleProperty(open);
        this.high = new SimpleDoubleProperty(high);
        this.low = new SimpleDoubleProperty(low);
        this.close = new SimpleDoubleProperty(close);
        this.volume = new SimpleLongProperty(volume);
    }

    public String getDate() {
        return date.get();
    }

    public Double getOpen() {
        return open.get();
    }

    public Double getHigh() {
        return high.get();
    }

    public Double getLow() {
        return low.get();
    }

    public Double getClose() {
        return close.get();
    }

    public Long getVolume() {
        return volume.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public DoubleProperty openProperty() {
        return open;
    }

    public DoubleProperty highProperty() {
        return high;
    }

    public DoubleProperty lowProperty() {
        return low;
    }

    public DoubleProperty closeProperty() {
        return close;
    }

    public LongProperty volumeProperty() {
        return volume;
    }
}