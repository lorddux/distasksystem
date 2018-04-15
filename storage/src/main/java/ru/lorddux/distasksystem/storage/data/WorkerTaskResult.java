package ru.lorddux.distasksystem.storage.data;

import lombok.Data;

@Data
public class WorkerTaskResult {
    private String taskId;
    private String taskSentence;
    private int resultNumber;
    private int timestamp;
    private String result;
}
