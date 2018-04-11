package ru.hse.lorddux.exception;

public class ConnectionException extends BaseException {
    private static final int ERROR_CODE = 64;

    public ConnectionException(String errorMsg, Throwable e) {
        super(ERROR_CODE, errorMsg, e);
    }

    public ConnectionException(Throwable e) {
        super(e);
    }
}
