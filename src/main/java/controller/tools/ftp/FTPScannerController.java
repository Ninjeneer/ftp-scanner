package controller.tools.ftp;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.tools.ftp.scanner.FTPScanner;
import model.tools.ftp.scanner.FTPScannerObserver;
import model.tools.ftp.scanner.ScanMode;
import model.tools.ftp.scanner.ScannerFactory;
import view.tools.ftp.scanner.ScannerResult;

import java.net.URL;
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
    private RadioButton scanModeMonkey;
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
    private TextField numberOfIp;
    @FXML
    private TableView<ScannerResult> scannerResult;

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
        TableColumn<ScannerResult, String> column1 = new TableColumn<>("IP");
        column1.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().host));

        TableColumn<ScannerResult, String> column2 = new TableColumn<>("Result");
        column2.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().result ? "SUCCESS" : "FAILED"));
        this.scannerResult.getColumns().setAll(column1, column2);
    }

    public void startScanner() {
        System.out.println("start");
        this.scannerResult.getItems().removeAll(this.scannerResult.getItems());
        if (scanMode == ScanMode.SINGLE) {
            this.scanner.scanUniqueIp(this.singleIp.getText(), Integer.parseInt(this.port.getText()));
        } else if (scanMode == ScanMode.RANGE) {
            this.scanner.scanRangeIp(this.lowerIp.getText(), this.upperIp.getText(), Integer.parseInt(this.port.getText()));
        } else if (scanMode == ScanMode.MONKEY) {
            this.scanner.scanMonkey(Integer.parseInt(this.numberOfIp.getText()), Integer.parseInt(this.port.getText()));
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
            this.numberOfIp.setDisable(true);
        } else if (e.getSource() == this.scanModeRange) {
            System.out.println("Switched to range scan mode");
            this.scanMode = ScanMode.RANGE;
            this.singleIp.setDisable(true);
            this.lowerIp.setDisable(false);
            this.upperIp.setDisable(false);
            this.excludeBounds.setDisable(false);
            this.numberOfIp.setDisable(true);
        } else if (e.getSource() == this.scanModeMonkey) {
            System.out.println("Switched to monkey scan mode");
            this.scanMode = ScanMode.MONKEY;
            this.singleIp.setDisable(true);
            this.lowerIp.setDisable(true);
            this.upperIp.setDisable(true);
            this.excludeBounds.setDisable(true);
            this.numberOfIp.setDisable(false);
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
    public void onScannerResult(String host, boolean result) {
        Platform.runLater(() -> {
            ScannerResult sr = new ScannerResult(host, result);
            this.scannerResult.getItems().add(sr);
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
