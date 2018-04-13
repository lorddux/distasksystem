package ru.lorddux.distasksystem.exception;

public class BaseException extends Exception{

    private Integer errorCode;
    private String errorMessage;

    public BaseException(Integer errorCode, String errorMessage, Throwable e) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        setStackTrace(e.getStackTrace());
    }

    public BaseException(Integer errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BaseException(Throwable e) {
        this.errorMessage = e.getMessage();
        setStackTrace(e.getStackTrace());
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

}
