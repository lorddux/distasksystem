package ru.hse.lorddux.download;

import java.net.URL;

public interface Downloader {
    void download(URL url, String dstFilePath) throws Exception;
}
