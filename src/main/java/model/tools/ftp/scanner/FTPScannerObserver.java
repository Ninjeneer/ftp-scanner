package model.tools.ftp.scanner;
import java.util.Map;

public interface FTPScannerObserver {
    void onScannerStart();
    void onScannerResult(Map<String, Boolean> data);
    void onScannerStop();
    void onScannerError(String message, Exception e);
}
