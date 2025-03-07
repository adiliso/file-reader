package com.adil.filereader;

import com.adil.filereader.model.StockDataModel;
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

    private static final ReaderController readerController = new ReaderController();

    @FXML
    private TextField path;

    @FXML
    private TextField checkingInterval;

    @FXML
    public TextArea info;

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
        info.setEditable(false);
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
        int interval = getValidInterval(checkingInterval);
        if (interval != -1 && interval != getCheckingInterval()) {
            setCheckingInterval(interval);
            readerController.restartMonitoring(path.getText(), info, tableView);
            return;
        }
        onClearTableButtonClick();
        readerController.monitorDirectory(path.getText(), info, tableView);
    }

    public void onClearTableButtonClick() {
        tableView.getItems().clear();
    }

    public static void stopMonitoring(){
        readerController.stopMonitoring();
    }
}
