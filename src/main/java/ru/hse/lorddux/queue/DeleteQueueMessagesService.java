package ru.hse.lorddux.queue;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.hse.lorddux.executor.PythonExecutor;
import ru.hse.lorddux.utils.ExecutorQueuePool;

import java.util.Collection;

public class DeleteQueueMessagesService implements Runnable {
    private static Logger log_ = LogManager.getLogger(DeleteQueueMessagesService.class);

    private ExecutorQueuePool<CloudQueueMessage> queuePool;
    private QueueProcessor queueProcessor;
    private volatile boolean stopFlag = false;

    public DeleteQueueMessagesService(Collection<PythonExecutor> executors, QueueProcessor queueProcessor) {
        queuePool = new ExecutorQueuePool<>(executors, PythonExecutor::getCompletedTaskIDQueue);
        this.queueProcessor = queueProcessor;
    }

    public void stop() {
        stopFlag = true;
    }

    @Override
    public void run() {
        while (!stopFlag) {
            CloudQueueMessage messageToDelete = queuePool.poll(1000L);
            try {
                log_.debug(String.format("Delete message %s", messageToDelete.getId()));
                queueProcessor.deleteTask(messageToDelete);
            } catch (StorageException e) {
                log_.warn(String.format("An error was occurred while deleting message %s", messageToDelete.getId()), e);
            }
        }
    }
}
