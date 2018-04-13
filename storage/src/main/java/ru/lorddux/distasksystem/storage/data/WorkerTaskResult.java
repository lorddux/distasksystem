package ru.lorddux.distasksystem.storage.data;

import lombok.Data;

@Data
public class WorkerTaskResult {
    private String taskId;
    private String taskSentence;
    private int resultNumber;
    private String result;
    private long timestamp;
}
