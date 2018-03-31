package ru.hse.lorddux.exception;

public class ExecutorException extends BaseException {

    private static final int ERROR_CODE = 100;

    public ExecutorException(String msg) {
        super(ERROR_CODE, msg);
    }
}
