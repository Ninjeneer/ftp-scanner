import model.tools.ftp.scanner.FTPScanner;
import model.tools.ftp.scanner.ScannerFactory;
import view.GUI;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws UnknownHostException {
        if (args.length == 0) {
            GUI g = new GUI();
            g.start();
        } else {
            FTPScanner s = ScannerFactory.createAnonymousScanner(Integer.parseInt(args[3]));
            s.scanRangeIp(args[0], args[1], Integer.parseInt(args[2]));
        }
//        ScannerFactory.createAnonymousScanner(1000).scanMonkey(200, 21);
    }
}
