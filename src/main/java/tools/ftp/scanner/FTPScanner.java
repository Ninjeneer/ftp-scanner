package tools.ftp.scanner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FTPScanner {
    private final FTPClient ftpClient;

    FTPScanner(String user, String password, int timeout) {
        this.ftpClient = new FTPClient(user, password, timeout);
    }

    public boolean scanUniqueIp(String host, int port) {
        System.out.printf("Opening connexion to %-22s%5s", host + ":" + port + "...", "");
        boolean result = this.ftpClient.testConnexion(host, port);
        System.out.println(result ? "SUCCESS" : "FAILED");
        return result;
    }

    public Map<String, Boolean> scanRangeIp(String hostRangeStart, String hostRangeStop, int port) {
        Map<String, Boolean> results = new HashMap<>();
        int[] bytesStart = Arrays.stream(hostRangeStart.split("\\.")).mapToInt(Integer::parseInt).toArray();
        int[] bytesStop = Arrays.stream(hostRangeStop.split("\\.")).mapToInt(Integer::parseInt).toArray();

        for (int byte1 = bytesStart[0]; byte1 <= bytesStop[0]; byte1++) {
            for (int byte2 = bytesStart[1]; byte2 <= bytesStop[1]; byte2++) {
                for (int byte3 = bytesStart[2]; byte3 <= bytesStop[2]; byte3++) {
                    for (int byte4 = bytesStart[3]; byte4 <= bytesStop[3]; byte4++) {
                        String host = String.format("%d.%d.%d.%d", byte1, byte2, byte3, byte4);
                        results.put(host, this.scanUniqueIp(host, port));
                    }
                }
            }
        }
        return results;
    }
}
