package tools.ftp.scanner;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class FTPClient {
    private final String user;
    private final String password;
    private final org.apache.commons.net.ftp.FTPClient client;
    private final Collection<FTPFile> files;

    public FTPClient(String user, String password) {
        this(user, password, 1000);
    }

    public FTPClient(String user, String password, int timeout) {
        this.user = user;
        this.password = password;
        this.files = new ArrayList<>();
        this.client = new org.apache.commons.net.ftp.FTPClient();
        this.client.setConnectTimeout(timeout);
    }

    public boolean testConnexion(String host, int port) {
        try {
            this.client.connect(host, port);
            int reply = this.client.getReplyCode();
            this.close();
            return FTPReply.isPositiveCompletion(reply);
        } catch (Exception e) {
            return false;
        }
    }

    public void open(String host, int port) throws IOException {
        if (this.testConnexion(host, port)) {
            System.out.println("FTP client connected. Logging in...");
            this.client.login(user, password);
            System.out.println("Successfully logged !");
        }
    }

    public void close() throws IOException {
        this.client.disconnect();
    }

    public Collection<FTPFile> listAllFiles(String path) throws IOException {
        Collection<FTPFile> collection = new ArrayList<>();
        FTPFile[] files = this.client.listFiles(path);
        this.files.addAll(Arrays.asList(files));
        for (FTPFile f : files) {
            if (f.isDirectory()) {
                collection.addAll(this.listAllFiles(path + "/" + f.getName()));
            } else {
                collection.add(f);
            }
            System.out.println(path + "/" + f.getName());
        }
        return collection;
    }

    public Collection<FTPFile> listFiles(String path) throws IOException {
        return new ArrayList<>(Arrays.asList(this.client.listFiles(path)));
    }

    public Collection<FTPFile> getFiles() {
        return files;
    }
}
