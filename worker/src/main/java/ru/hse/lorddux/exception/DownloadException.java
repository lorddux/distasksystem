package ru.hse.lorddux.exception;

public class DownloadException extends BaseException {
    private static final int ERROR_CODE = 52;

    public DownloadException(String errorMsg, Throwable e) {
        super(ERROR_CODE, errorMsg, e);
    }

    public DownloadException(Throwable e) {
        super(e);
    }
}
