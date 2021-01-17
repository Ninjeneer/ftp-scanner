package model.tools.ftp.scanner;

import model.tools.ScannerObservable;
import model.tools.ftp.FTPClient;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class FTPScanner implements ScannerObservable {
    private final FTPClient ftpClient;
    private final List<FTPScannerObserver> listeners;

    FTPScanner(String user, String password, int timeout) {
        this.ftpClient = new FTPClient(user, password, timeout);
        this.listeners = new ArrayList<>();
    }

    public void scanUniqueIp(String host, int port) {
        this.scanRangeIp(host, host, port);
    }

    public void scanRangeIp(String hostRangeStart, String hostRangeStop, int port) {
        new Thread(() -> {
            triggerListenersForStart();
            Map<String, Boolean> results = new HashMap<>();
            // Transform String IP Address to BigIntegers
            BigInteger intStart = null;
            BigInteger intStop = null;
            try {
                 intStart = new BigInteger(1, InetAddress.getByName(hostRangeStart).getAddress());
                 intStop = new BigInteger(1, InetAddress.getByName(hostRangeStop).getAddress());
            } catch (UnknownHostException e) {
                this.triggerListenersWithError("Unknown IP", e);
            }
            assert intStart != null;
            assert intStop != null;

            BigInteger nbIpToScan = intStop.subtract(intStart);
            System.out.println("Starting scan of " + (hostRangeStart.equals(hostRangeStop) ? "1" : nbIpToScan.toString()) + " target" + (nbIpToScan.compareTo(BigInteger.valueOf(1)) > 0 ? "s" : ""));

            for (BigInteger i = intStart; i.compareTo(intStop) <= 0; i = i.add(BigInteger.valueOf(1))) {
                int[] ipBytes = bigIntegerToBytes(i);
                String host = String.format("%d.%d.%d.%d", ipBytes[0], ipBytes[1], ipBytes[2], ipBytes[3]);
                System.out.printf("Opening connexion to %-22s%5s", host + ":" + port + "...", "");
                boolean result = ftpClient.testConnexion(host, port);
                results.put(host, result);
                System.out.println(result ? "SUCCESS" : "FAILED");
                triggerListenersForData(results);
            }
            triggerListenersForStop();
        }).start();
    }

    public void addListener(FTPScannerObserver s) {
        this.listeners.add(s);
    }

    @Override
    public void triggerListenersForStart() {
        for (FTPScannerObserver s : this.listeners) {
            s.onScannerStart();
        }
    }

    @Override
    public void triggerListenersForStop() {
        for (FTPScannerObserver s : this.listeners) {
            s.onScannerStop();
        }
    }

    @Override
    public void triggerListenersWithError(String message, Exception e) {

    }

    private void triggerListenersForData(Map<String, Boolean> data) {
        for (FTPScannerObserver s : this.listeners) {
            s.onScannerResult(data);
        }
    }

    private String addZeroPadding(String ip) {
        StringBuilder ipBuilder = new StringBuilder(ip);
        while (ipBuilder.length() < 32) {
            ipBuilder.insert(0, "0");
        }
        return ipBuilder.toString();
    }

    private int[] bigIntegerToBytes(BigInteger b) {
        String[] bytes = new String[4];
        String integerToBinaryString = this.addZeroPadding(b.toString(2));
        for (int i = 0; i < integerToBinaryString.length(); i += 8) {
            bytes[i / 8] = integerToBinaryString.substring(i, i+8);
        }

        int[] result = new int[4];
        for (int i = 0; i < 4; i++)
            result[i] = Integer.parseInt(bytes[i], 2);
        return result;
    }
}