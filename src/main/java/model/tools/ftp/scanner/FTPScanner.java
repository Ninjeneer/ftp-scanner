package model.tools.ftp.scanner;

import model.tools.ScannerObservable;
import model.tools.ftp.FTPClient;

import java.io.IOException;
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

    private boolean scan(String host, int port) {
        if (ftpClient.testConnexion(host, port)) {
            try {
                ftpClient.open(host, port);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public void scanUniqueIp(String host, int port) {
        this.triggerListenersForStart();
        this.scan(host, port);
        this.triggerListenersForStop();
    }

    public void scanRangeIp(String hostRangeStart, String hostRangeStop, int port) {
        new Thread(() -> {
            triggerListenersForStart();
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
                boolean result = this.scan(host, port);
                System.out.println(result ? "SUCCESS" : "FAILED");
                triggerListenersForData(host, result);
            }
            triggerListenersForStop();
        }).start();
    }

    public void scanMonkey(int nbIp, int port) {
        new Thread(() -> {
            triggerListenersForStart();
            try {
                Random r = new Random();
                String[] ipArray = new String[nbIp];
                for (int i = 0; i < nbIp; i++) {
                    String newIp;
                    do {
                        newIp = r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
                    } while (InetAddress.getByName(newIp).isSiteLocalAddress());
                    ipArray[i] = newIp;
                }

                for (String ip : ipArray) {
                    this.triggerListenersForData(ip, this.scan(ip, port));
                }
            } catch (Exception e) {
                this.triggerListenersWithError("Monkey Scan Error", e);
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
        for (FTPScannerObserver s : this.listeners) {
            s.onScannerError(message, e);
        }
    }

    private void triggerListenersForData(String host, boolean result) {
        for (FTPScannerObserver s : this.listeners) {
            s.onScannerResult(host, result);
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