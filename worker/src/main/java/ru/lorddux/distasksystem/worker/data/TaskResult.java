package ru.lorddux.distasksystem.worker.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResult {
    private String taskId;
    private String taskSentence;
    private int resultId;
    private String result;
    private long timestamp;
}
