package ru.lorddux.distasksystem.worker.executors;

import com.microsoft.azure.storage.queue.CloudQueueMessage;
import ru.lorddux.distasksystem.worker.exception.ExecutorException;

public interface Postprocessor {
    String giveThisMethodName(String result, CloudQueueMessage task, int id) throws ExecutorException;
}
