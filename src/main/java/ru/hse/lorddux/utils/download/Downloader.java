package ru.hse.lorddux.utils.download;

import ru.hse.lorddux.exception.DownloadException;

import java.net.URL;

public interface Downloader {
    void download(URL url, String dstFilePath) throws DownloadException;
}
