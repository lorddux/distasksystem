package ru.hse.lorddux.exception;

public class DownloadException extends BaseException {

    public DownloadException(String errorMsg) {
        super(12, errorMsg);
    }
}
