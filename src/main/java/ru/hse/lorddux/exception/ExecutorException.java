package ru.hse.lorddux.exception;

public class ExecutorException extends BaseException {

    private static final int ERROR_CODE = 100;

    public ExecutorException(String errorMsg, Throwable e) {
        super(ERROR_CODE, errorMsg, e);
    }

    public ExecutorException(Throwable e) {
        super(e);
    }
}
