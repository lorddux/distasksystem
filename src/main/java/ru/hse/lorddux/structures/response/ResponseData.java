package ru.hse.lorddux.structures.response;

public class ResponseData<Result> {
    private Boolean successFeature = null;
    private Integer errorCode = null;
    private String description = null;
    private Result result = null;

    public void setResult(Result result) {
        this.result = result;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public void setSuccessFeature(Boolean successFeature) {
        this.successFeature = successFeature;
    }

    public Result getResult() {
        return result;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getSuccessFeature() {
        return successFeature;
    }
}
