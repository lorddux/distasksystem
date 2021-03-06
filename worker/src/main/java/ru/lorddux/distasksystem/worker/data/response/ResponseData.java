package ru.lorddux.distasksystem.worker.data.response;

import lombok.Data;

@Data
public class ResponseData<Result> {
    private Boolean successFeature = null;
    private Integer errorCode = null;
    private String description = null;
    private Result result = null;
}
