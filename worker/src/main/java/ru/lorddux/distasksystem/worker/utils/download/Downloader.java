package ru.lorddux.distasksystem.worker.utils.download;

import ru.lorddux.distasksystem.worker.exception.DownloadException;

public interface Downloader {
    void download(String url, String dstFilePath) throws DownloadException;
}
