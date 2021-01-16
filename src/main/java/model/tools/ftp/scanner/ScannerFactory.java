package model.tools.ftp.scanner;

public class ScannerFactory {
    public static FTPScanner createAnonymousScanner(int timeout) {
        return new FTPScanner("anonymous", "@nonymous", timeout);
    }

    public static FTPScanner createScanner(String user, String password, int timeout) {
        return new FTPScanner(user, password, timeout);
    }
}
