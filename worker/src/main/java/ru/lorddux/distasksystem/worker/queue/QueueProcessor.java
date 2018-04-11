package ru.lorddux.distasksystem.worker.queue;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueueMessage;

public interface QueueProcessor {
    /**
     *
     * @return task from queue
     */
    CloudQueueMessage getNextTask(int visibilityTimeout) throws StorageException;

    /**
     *
     * @return
     */
    CloudQueueMessage getNextTask() throws StorageException;

    /**
     *
     * @param batchSize size of the batch
     * @param visibilityTimeout visibility timeout
     * @return batch of tasks from queue of some custom size@return
     */
    Iterable<CloudQueueMessage> getNextBatch(int batchSize, int visibilityTimeout) throws StorageException;

    /**
     *
     * @param batchSize size of the batch
     * @return batch of tasks from queue of some custom size
     */
    Iterable<CloudQueueMessage> getNextBatch(int batchSize) throws StorageException;

    /**
     * Delete task from remote queue
     * @param message - message to delete from queue
     */
    void deleteTask(CloudQueueMessage message) throws StorageException;

}
