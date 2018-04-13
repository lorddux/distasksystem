package ru.lorddux.distasksystem.worker.executors;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lorddux.distasksystem.worker.exception.ExecutorException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class ExecutorImpl extends Thread {
    private static final Logger log_ = LogManager.getLogger(ExecutorImpl.class);
    private static final int SLEEP_TIME = 1000;
    public static int DEFAULT_QUEUE_SIZE = 100;

    @Getter
    private BlockingQueue<CloudQueueMessage> tasksQueue;

    @Getter
    private BlockingQueue<CloudQueueMessage> completedTaskIDQueue;

    @Getter
    private BlockingQueue<String> resultQueue;

    private volatile boolean stopFlag = false;
    private Postprocessor postprocessor;

    public ExecutorImpl(Integer queueSize) {
        this.tasksQueue = new ArrayBlockingQueue<>(queueSize);
        this.completedTaskIDQueue = new ArrayBlockingQueue<>(queueSize);
        this.resultQueue = new ArrayBlockingQueue<>(queueSize);
        this.postprocessor = new JsonPostprocessor();
    }


    public void stopThread() {
        stopFlag = false;
    }

    //TODO
    public void run() {
        log_.info("run()");
        String taskId = "-1";
        try {
            while (!stopFlag) {
                if (tasksQueue.isEmpty()) {
                    Thread.sleep(SLEEP_TIME);
                    continue;
                }
                try {
                    CloudQueueMessage task = tasksQueue.poll();
                    taskId = task.getMessageId();
                    processTask(task);
                    while ((! completedTaskIDQueue.offer(task, 1, TimeUnit.SECONDS))    && (! stopFlag));
                } catch (IOException e) {
                    log_.error(String.format("Error while executing task %s", taskId), e);
                }  catch (StorageException e) {
                    log_.warn(String.format("Can not process task %s", taskId), e);
                }
            }
        } catch (InterruptedException e) {
            log_.info("Exiting");
        } catch (ExecutorException e) {
            log_.fatal("Exiting", e);
        }
    }

    public void processTask(CloudQueueMessage task)
            throws IOException, ExecutorException, InterruptedException, StorageException {
        Process p = buildProcess(task.getMessageContentAsString());
        Scanner scanner = new Scanner(p.getInputStream());
        int id = 0;
        while (scanner.hasNextLine()) {
            String result = scanner.nextLine();
            log_.trace(String.format("Task %s executed. Result: %s", task.getId(), result));
            String finalResult = postprocessor.giveThisMethodName(result, task, id);
            while ((! resultQueue.offer(finalResult, 1, TimeUnit.SECONDS)) && (! stopFlag));
            id++;
        }
        String errorMessage = new String(p.getErrorStream().readAllBytes());
        if (errorMessage.length() > 0) {
            throw new ExecutorException(errorMessage);
        }
    }

    protected abstract Process buildProcess(String task) throws IOException;
}
