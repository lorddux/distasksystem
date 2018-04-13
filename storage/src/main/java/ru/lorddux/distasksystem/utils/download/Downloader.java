package ru.lorddux.distasksystem.utils.download;


import ru.lorddux.distasksystem.exception.DownloadException;

public interface Downloader {
    void download(String url, String dstFilePath) throws DownloadException;
}
