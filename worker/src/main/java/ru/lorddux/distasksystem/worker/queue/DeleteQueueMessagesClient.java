package ru.lorddux.distasksystem.worker.queue;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lorddux.distasksystem.worker.executors.ExecutorImpl;
import ru.lorddux.distasksystem.worker.utils.ExecutorQueuePool;

import java.util.Collection;

public class DeleteQueueMessagesClient implements Runnable {
    private static Logger log_ = LogManager.getLogger(DeleteQueueMessagesClient.class);

    private ExecutorQueuePool<CloudQueueMessage> queuePool;
    private QueueProcessor queueProcessor;
    private volatile boolean stopFlag = false;

    public DeleteQueueMessagesClient(Collection<ExecutorImpl> executors, QueueProcessor queueProcessor) {
        queuePool = new ExecutorQueuePool<>(executors, ExecutorImpl::getCompletedTaskIDQueue);
        this.queueProcessor = queueProcessor;
    }

    public void stop() {
        stopFlag = true;
    }

    @Override
    public void run() {
        CloudQueueMessage messageToDelete;
        while (!stopFlag) {
            if ((messageToDelete = queuePool.poll(1000L)) == null) {
                return;
            }
            try {
                log_.debug(String.format("Delete message %s", messageToDelete.getId()));
                queueProcessor.deleteTask(messageToDelete);
            } catch (StorageException e) {
                log_.warn(String.format("An error was occurred while deleting message %s", messageToDelete.getId()), e);
            }
        }
    }
}
