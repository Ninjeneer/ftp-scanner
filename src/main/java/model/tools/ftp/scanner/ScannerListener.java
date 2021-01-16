package model.tools.ftp.scanner;
import java.util.Map;

public interface ScannerListener {
    void onScannerResult(Map<String, Boolean> data);
}
