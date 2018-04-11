package ru.hse.lorddux.executors;

import com.google.gson.Gson;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import ru.hse.lorddux.data.TaskResult;
import ru.hse.lorddux.exception.ExecutorException;

public class JsonPostprocessor implements Postprocessor {
    @Override
    public String giveThisMethodName(String result, CloudQueueMessage task) throws ExecutorException {
        if (! checkResult(result)) throw new ExecutorException(String.format("Bad return format: %s", result));
        TaskResult taskResult = new TaskResult(result.trim(), task.getId(), System.currentTimeMillis());
        return new Gson().toJson(taskResult);
    }

    private boolean checkResult(String result) {
        return result.length() > 0 && result.charAt(0) == '{';
    }
}