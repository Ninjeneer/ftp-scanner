package view.tools.ftp.scanner;

public class ScannerResult {
    public final String host;
    public final Boolean result;

    public ScannerResult(String host, Boolean result) {
        this.host = host;
        this.result = result;
    }
}
