import tools.ftp.scanner.FTPScanner;
import tools.ftp.scanner.ScannerFactory;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        FTPScanner s = ScannerFactory.createAnonymousScanner(Integer.parseInt(args[3]));
        Map<String, Boolean> m = s.scanRangeIp(args[0], args[1], Integer.parseInt(args[2]));
        for (Map.Entry<String, Boolean> e : m.entrySet()) {
            if (e.getValue()) {
                System.out.println(e.getKey() + " ==> SUCCESS");
            }
        }
    }
}
