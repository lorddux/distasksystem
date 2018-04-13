package ru.lorddux.distasksystem.worker.queue;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lorddux.distasksystem.worker.executors.ExecutorImpl;
import ru.lorddux.distasksystem.worker.utils.ExecutorQueuePool;

import java.util.Collection;

public class GetQueueMessagesClient implements Runnable {
    private static Logger log_ = LogManager.getLogger(GetQueueMessagesClient.class);
    private static long SLEEP_TIME = 1000L;

    private ExecutorQueuePool<CloudQueueMessage> executorsQueuePool;
    private QueueProcessor queueProcessor;
    private volatile boolean stopFlag = false;

    public GetQueueMessagesClient(Collection<ExecutorImpl> executors, QueueProcessor queueProcessor) {
        this.executorsQueuePool = new ExecutorQueuePool<>(executors, ExecutorImpl::getTasksQueue);
        this.queueProcessor = queueProcessor;
    }

    public void stop() {
        stopFlag = true;
    }

    @Override
    public void run() {
        log_.info("run()");
        while (!stopFlag) {
            try {
                Iterable<CloudQueueMessage> messages = queueProcessor.getNextBatch(10, 60);
                executorsQueuePool.offerAll(messages, 100L);
            } catch (StorageException e) {
                log_.error("Can non get a task", e);
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException ex) {
                    log_.info("Exiting");
                    break;
                }
            }
        }
    }
}
