package ru.lorddux.distasksystem.storage.data;

import lombok.Data;

@Data
public class WorkerTaskResult {
    private String id;
    private String result;
    private long timestamp;
}
