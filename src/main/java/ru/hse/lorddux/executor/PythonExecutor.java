package ru.hse.lorddux.executor;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.hse.lorddux.exception.ExecutorException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@AllArgsConstructor
@RequiredArgsConstructor
public class PythonExecutor extends Thread {
    private static final Logger log_ = LogManager.getLogger(PythonExecutor.class);
    private static final String DEFAULT_PYTHON_COMMAND = "python";
    private static final int SLEEP_TIME = 1000;
    public static int DEFAULT_QUEUE_SIZE = 100;

    @Getter
    @NonNull
    private BlockingQueue<CloudQueueMessage> tasksQueue;

    @Getter
    @NonNull
    private BlockingQueue<CloudQueueMessage> completedTaskIDQueue;

    @Getter
    @NonNull
    private BlockingQueue<String> resultQueue;

    @Setter
    @Getter
    @NonNull
    private String codePath;

    @Setter
    @Getter
    private List<String> args = Collections.emptyList();

    @Setter
    @Getter
    private String pythonCommand = DEFAULT_PYTHON_COMMAND;

    private volatile boolean stopFlag = false;

    public PythonExecutor(String commandPath, String codePath, List<String> args, Integer queueSize) {
        this(commandPath, codePath, queueSize);
        this.args = args;
    }

    public PythonExecutor(String commandPath, String codePath, Integer queueSize) {
        this.pythonCommand = commandPath;
        this.codePath = codePath;
        this.tasksQueue = new ArrayBlockingQueue<>(queueSize);
        this.completedTaskIDQueue = new ArrayBlockingQueue<>(queueSize);
        this.resultQueue = new ArrayBlockingQueue<>(queueSize);
    }

    public void stopThread() {
        stopFlag = false;
    }

    //TODO
    public void run() {
        String result;
        CloudQueueMessage task;
        String taskId = "-1";
        try {
            while (!stopFlag) {
                if (tasksQueue.isEmpty()) {
                    Thread.sleep(SLEEP_TIME);
                    continue;
                }
                try {
                    task = tasksQueue.poll();
                    taskId = task.getMessageId();
                    result = processTask(task.getMessageContentAsString());
                    log_.trace(String.format("Task %s executed. Result: %s", taskId, result));
                    while ((! resultQueue.offer(result, 1, TimeUnit.SECONDS))           && (! stopFlag));
                    while ((! completedTaskIDQueue.offer(task, 1, TimeUnit.SECONDS))    && (! stopFlag));
                } catch (IOException e) {
                    log_.error(String.format("Error while executing task %s", taskId), e);
                }
            }
        } catch (InterruptedException e) {
            log_.warn(String.format("Thread %s was interrupted!", Thread.currentThread().getName()));
        } catch (StorageException e) {
            log_.warn("Can not process task", e);
        }
    }

    public String processTask(String taskMsg) throws IOException {
        Process p = buildProcess(taskMsg);
        return new String(p.getInputStream().readAllBytes());
    }

    private Process buildProcess(String task) throws IOException{
        List<String> pbArguments = new ArrayList<>();
        pbArguments.add(pythonCommand);
        pbArguments.add(codePath);
        pbArguments.add(task);
        pbArguments.addAll(args);
        log_.debug(String.format("Run command: %s", pbArguments.stream().reduce("", (s, s2) -> String.format("%s %s", s, s2))));
        ProcessBuilder p = new ProcessBuilder(pbArguments);
        return p.start();
    }
}
