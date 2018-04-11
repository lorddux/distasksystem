package ru.hse.lorddux.executors;

import com.microsoft.azure.storage.queue.CloudQueueMessage;
import ru.hse.lorddux.exception.ExecutorException;

public interface Postprocessor {
    String giveThisMethodName(String result, CloudQueueMessage task) throws ExecutorException;
}
