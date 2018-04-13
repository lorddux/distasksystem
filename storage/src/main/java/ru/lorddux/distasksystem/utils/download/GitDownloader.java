package ru.lorddux.distasksystem.utils.download;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import ru.lorddux.distasksystem.exception.DownloadException;

import java.io.File;

public class GitDownloader implements Downloader {

    @Override
    public void download(String url, String dstFilePath) throws DownloadException {
        CloneCommand command = new CloneCommand();
        command = command.setDirectory(new File(dstFilePath));
        command = command.setURI(url);
        try {
            command.call();
        } catch (GitAPIException e) {
            throw new DownloadException(e);
        }
    }
}
