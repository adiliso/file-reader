package com.adil.filereader.controller;

import com.adil.filereader.logger.TextAreaLogger;
import com.adil.filereader.model.StockDataModel;
import com.adil.filereader.service.MonitoringService;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import static com.adil.filereader.util.AppUtils.getCheckingInterval;
import static com.adil.filereader.util.AppUtils.getValidInterval;
import static com.adil.filereader.util.AppUtils.setCheckingInterval;

public class ViewController {

    private static final MonitoringService monitoringService = new MonitoringService();

    @FXML
    private TextField path;

    private String previousPath;

    @FXML
    private TextArea info;

    @FXML
    private TextField checkingInterval;

    @FXML
    private TableView<StockDataModel> tableView;

    @FXML
    private TableColumn<StockDataModel, String> dateColumn;

    @FXML
    private TableColumn<StockDataModel, Double> openColumn;

    @FXML
    private TableColumn<StockDataModel, Double> highColumn;

    @FXML
    private TableColumn<StockDataModel, Double> lowColumn;

    @FXML
    private TableColumn<StockDataModel, Double> closeColumn;

    @FXML
    private TableColumn<StockDataModel, Long> volumeColumn;

    @FXML
    public void initialize() {
        TextAreaLogger.getInstance().setTextArea(info);
        path.appendText("");
        previousPath = "";
        checkingInterval.appendText("5");

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        openColumn.setCellValueFactory(new PropertyValueFactory<>("open"));
        highColumn.setCellValueFactory(new PropertyValueFactory<>("high"));
        lowColumn.setCellValueFactory(new PropertyValueFactory<>("low"));
        closeColumn.setCellValueFactory(new PropertyValueFactory<>("close"));
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));
    }

    @FXML
    protected void onMonitorButtonClick() {
        onClearTableButtonClick();
        boolean intervalChanged = intervalChanged();
        if (intervalChanged) setCheckingInterval(getValidInterval(checkingInterval));

        if (intervalChanged || pathChanged()) {
            monitoringService.restartMonitoring(path.getText(), tableView);
            return;
        }
        monitoringService.monitorDirectory(path.getText(), tableView);
    }

    public void onClearTableButtonClick() {
        tableView.getItems().clear();
    }

    public static void stopMonitoring() {
        monitoringService.stopMonitoring();
    }

    private boolean pathChanged() {
        boolean pathChanged = !previousPath.equals(path.getText());
        previousPath = path.getText();
        return monitoringService.isMonitoringActive() && pathChanged;
    }

    private boolean intervalChanged() {
        int interval = getValidInterval(checkingInterval);
        return interval != -1 && getCheckingInterval() != interval;
    }
}
