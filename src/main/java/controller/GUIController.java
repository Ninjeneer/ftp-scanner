package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import model.tools.ftp.scanner.ScannerFactory;
import org.apache.commons.net.ftp.FTPFile;
import model.tools.ftp.scanner.FTPScanner;

import java.net.URL;
import java.util.ResourceBundle;

public class GUIController implements Initializable {
    // FXML controls
    @FXML
    private TreeView<FTPFile> fileTree;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Label scannerStatus;

    // FTP Scanner
    private FTPScanner scanner;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.scannerStatus.setText("Scanner not ready");
        this.scanner = ScannerFactory.createAnonymousScanner(1000);
        this.scannerStatus.setText("Scanner ready");
    }

    public void startScanner() {
        System.out.println("start");
        this.stopButton.setDisable(false);
        this.startButton.setDisable(true);
        this.scannerStatus.setText("Scanner started !");
    }

    public void stopScanner() {
        System.out.println("stop");
        this.stopButton.setDisable(true);
        this.startButton.setDisable(false);
        this.scannerStatus.setText("Scanner ready");
    }
}
