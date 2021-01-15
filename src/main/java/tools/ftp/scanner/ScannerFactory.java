package tools.ftp.scanner;

public class ScannerFactory {
    public static FTPScanner createAnonymousScanner(int timeout) {
        return new FTPScanner("anonymous", "@nonymous", timeout);
    }

    public static FTPScanner createScanner(String user, String password) {
        return new FTPScanner(user, password, 1000);
    }
}
