package ru.lorddux.distasksystem.exception;

public class ExecutorException extends BaseException {

    private static final int ERROR_CODE = 100;

    public ExecutorException(String msg, Throwable e) {
        super(ERROR_CODE, msg, e);
    }

    public ExecutorException(Throwable e) {
        super(e);
    }

    public ExecutorException(String msg) {
        super(ERROR_CODE, msg);
    }
}
