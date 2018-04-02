package ru.hse.lorddux.queue;

import ru.hse.lorddux.structures.TaskItem;

import java.util.List;

public interface QueueProcessor {
    /**
     *
     * @return task from queue as a string
     */
    TaskItem getNextTask(int visibilityTimeout);

    /**
     *
     * @return batch of tasks from queue of some fixed size
     */
    List<TaskItem> getNextBatch(int visibilityTimeout);

    /**
     *
     * @param batchSize
     * @return batch of tasks from queue of some custom size
     */
    List<TaskItem> getNextBatch(int batchSize, int visibilityTimeout);

    /**
     * Delete task from remote queue
     * @param popReceipt
     */
    void deleteTask(String popReceipt);

}
