package ru.hse.lorddux.utils.download;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import ru.hse.lorddux.exception.DownloadException;

import java.io.File;
import java.net.URL;

public class GitDownloader implements Downloader {

    @Override
    public void download(URL url, String dstFilePath) throws DownloadException {
        CloneCommand command = new CloneCommand();
        command = command.setDirectory(new File("var"));
        command = command.setURI("https://github.com/lorddux/oop-1.git");
        try {
            command.call();
        } catch (GitAPIException e) {
            throw new DownloadException(e);
        }
    }
}
