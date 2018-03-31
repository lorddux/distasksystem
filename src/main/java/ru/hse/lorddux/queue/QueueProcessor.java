package ru.hse.lorddux.queue;

import java.util.List;

public interface QueueProcessor {
    /**
     *
     * @return task from queue as a string
     */
    String getNextTask();

    /**
     *
     * @return batch of tasks from queue of some fixed size
     */
    List<String> getNextBatch();

    /**
     *
     * @param batchSize
     * @return batch of tasks from queue of some custom size
     */
    List<String> getNextBatch(int batchSize);
}
