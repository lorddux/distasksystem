package download;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public interface Downloader {
    void download(URL url, String dstFilePath) throws Exception;
}
