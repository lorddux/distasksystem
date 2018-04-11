package ru.lorddux.distasksystem.worker.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResult {
    private String resultString;
    private String taskId;
    private long timestamp;
}
