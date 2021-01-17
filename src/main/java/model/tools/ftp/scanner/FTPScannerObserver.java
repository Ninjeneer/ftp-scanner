package model.tools.ftp.scanner;
import java.util.Map;

public interface FTPScannerObserver {
    void onScannerStart();
    void onScannerResult(String host, boolean result);
    void onScannerStop();
    void onScannerError(String message, Exception e);
}
