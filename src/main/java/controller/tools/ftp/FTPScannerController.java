package controller.tools.ftp;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.tools.ftp.scanner.FTPScanner;
import model.tools.ftp.scanner.ScanMode;
import model.tools.ftp.scanner.ScannerFactory;
import model.tools.ftp.scanner.FTPScannerObserver;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class FTPScannerController implements Initializable, FTPScannerObserver {
    // FXML controls
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Label scannerStatus;
    @FXML
    private RadioButton scanModeSingle;
    @FXML
    private RadioButton scanModeRange;
    @FXML
    private TextField port;
    @FXML
    private TextField singleIp;
    @FXML
    private TextField lowerIp;
    @FXML
    private TextField upperIp;
    @FXML
    private CheckBox excludeBounds;
    @FXML
    private TableView<Map.Entry<String, Boolean>> scannerResult;

    private ScanMode scanMode;
    // FTP Scanner
    private FTPScanner scanner;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.scannerStatus.setText("Scanner not ready");
        this.scanner = ScannerFactory.createAnonymousScanner(1000);
        this.scanner.addListener(this);
        this.scanMode = ScanMode.SINGLE;
        this.scannerStatus.setText("Scanner ready");

        // Initialize table view
        TableColumn<Map.Entry<String, Boolean>, String> column1 = new TableColumn<>("IP");
        column1.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getKey()));

        TableColumn<Map.Entry<String, Boolean>, String> column2 = new TableColumn<>("Result");
        column2.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getValue() ? "SUCCESS" : "FAILED"));
        this.scannerResult.getColumns().setAll(column1, column2);
    }

    public void startScanner() {
        System.out.println("start");
        this.scannerResult.getItems().removeAll(this.scannerResult.getItems());
        if (scanMode == ScanMode.SINGLE) {
            this.scanner.scanUniqueIp(this.singleIp.getText(), Integer.parseInt(this.port.getText()));
        } else if (scanMode == ScanMode.RANGE) {
            this.scanner.scanRangeIp(this.lowerIp.getText(), this.upperIp.getText(), Integer.parseInt(this.port.getText()));
        }
    }

    public void stopScanner() {
        System.out.println("stop");
    }

    public void changeScanMode(ActionEvent e) {
        if (e.getSource() == this.scanModeSingle) {
            System.out.println("Switched to single scan mode");
            this.scanMode = ScanMode.SINGLE;
            this.singleIp.setDisable(false);
            this.lowerIp.setDisable(true);
            this.upperIp.setDisable(true);
            this.excludeBounds.setDisable(true);
        } else if (e.getSource() == this.scanModeRange) {
            System.out.println("Switched to range scan mode");
            this.scanMode = ScanMode.RANGE;
            this.singleIp.setDisable(true);
            this.lowerIp.setDisable(false);
            this.upperIp.setDisable(false);
            this.excludeBounds.setDisable(false);
        }
    }

    @Override
    public void onScannerStart() {
        this.startButton.setDisable(true);
        this.stopButton.setDisable(false);
        Platform.runLater(() -> {
            this.scannerStatus.setText("Scanner started !");
        });
    }

    @Override
    public void onScannerResult(Map<String, Boolean> data) {
        System.out.println("trigger");
        System.out.println(data);
        Platform.runLater(() -> {
            ObservableList<Map.Entry<String, Boolean>> items = FXCollections.observableArrayList(data.entrySet());
            this.scannerResult.getItems().setAll(items);
        });
    }

    @Override
    public void onScannerStop() {
        this.stopButton.setDisable(true);
        this.startButton.setDisable(false);
        Platform.runLater(() -> {
            this.scannerStatus.setText("Scanner finished");
        });
    }

    @Override
    public void onScannerError(String message, Exception e) {
        Platform.runLater(() -> {
            this.showError("Error", message, e.getMessage());
        });
    }

    private void showError(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
