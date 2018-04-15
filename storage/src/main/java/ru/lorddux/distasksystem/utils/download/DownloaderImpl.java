package ru.lorddux.distasksystem.utils.download;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lorddux.distasksystem.exception.DownloadException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class DownloaderImpl implements Downloader {
    private final static Logger log_ = LogManager.getLogger(Downloader.class);

    public void download(String urlString, String dstFilePath) throws DownloadException {
        try (OutputStream output = new FileOutputStream(new File(dstFilePath))) {
            URL url = new URL(urlString);
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

            byte[] buffer = new byte[4096];
            int readSize;
            while ((readSize = input.read(buffer)) != -1) {
                output.write(buffer, 0, readSize);
            }
        } catch (Exception e) {
            throw new DownloadException(e);
        }
    }

    private boolean isRedirected(Map<String, List<String>> header ) {
        for (String hv : header.get( null )) {
            if (hv.contains(" 301 ") || hv.contains(" 302 ")) return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        Downloader d = new DownloaderImpl();
        d.download("https://github.com/lorddux/testDriver/raw/master/mysql-connector-java-5.1.46.jar", "driver.jar");
    }
}
