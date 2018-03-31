package ru.hse.lorddux.exception;

public class BaseException extends Exception{

    private Integer errorCode;
    private String errorMessage;

    public BaseException(Integer errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
