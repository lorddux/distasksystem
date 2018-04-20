package ru.lorddux.distasksystem.worker.data;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResult {
    private String taskId;
    private String taskSentence;
    private int resultNumber;
    private long timestamp;
    private String result;
}
