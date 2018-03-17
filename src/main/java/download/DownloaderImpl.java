package download;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloaderImpl implements Downloader {

    final static Logger logger = LogManager.getLogger(Downloader.class);

    public void download(URL url, String dstFilePath) throws Exception {
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        Map<String, List<String>> header = http.getHeaderFields();
        while (isRedirected(header)) {
            url = new URL(
                    header.get("Location").get(0)
            );
            http = (HttpURLConnection) url.openConnection();
            header = http.getHeaderFields();
        }
        InputStream input = http.getInputStream();

        OutputStream output = new FileOutputStream(new File(dstFilePath));
        byte[] buffer = new byte[4096];
        int readSize;
        while ((readSize = input.read(buffer)) != -1) {
            output.write(buffer, 0, readSize);
        }
        output.close();
    }

    private boolean isRedirected(Map<String, List<String>> header ) {
        for (String hv : header.get( null )) {
            if (hv.contains(" 301 ") || hv.contains(" 302 ")) return true;
        }
        return false;
    }

}
