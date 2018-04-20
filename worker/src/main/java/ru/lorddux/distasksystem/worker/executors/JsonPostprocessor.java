package ru.lorddux.distasksystem.worker.executors;

import com.google.gson.Gson;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import ru.lorddux.distasksystem.worker.data.TaskResult;
import ru.lorddux.distasksystem.worker.exception.ExecutorException;

public class JsonPostprocessor implements Postprocessor {
    @Override
    public String giveThisMethodName(String result, CloudQueueMessage task, int id) throws ExecutorException {
        if (! checkResult(result)) throw new ExecutorException(String.format("Bad return format: %s", result));
        try {
            TaskResult taskResult = new TaskResult(task.getId(), task.getMessageContentAsString(), id,  (int) System.currentTimeMillis() / 1000, result);
            return new Gson().toJson(taskResult);
        } catch (StorageException e) {
            throw new ExecutorException(e);
        }
    }

    private boolean checkResult(String result) {
        return result.length() > 0 && result.charAt(0) == '{';
    }
}
