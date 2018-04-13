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

@AllArgsConstructor
@RequiredArgsConstructor
public class ExecutorImpl extends Thread {
    private static final Logger log_ = LogManager.getLogger(ExecutorImpl.class);
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
    private String command = DEFAULT_PYTHON_COMMAND;

    private volatile boolean stopFlag = false;
    private Postprocessor postprocessor;

    public ExecutorImpl(String commandPath, String codePath, List<String> args, Integer queueSize) {
        this(commandPath, codePath, queueSize);
        this.args = args;
    }

    public ExecutorImpl(String commandPath, String codePath, Integer queueSize) {
        this.command = commandPath;
        this.codePath = codePath;
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

    private Process buildProcess(String task) throws IOException{
        List<String> pbArguments = new ArrayList<>();
        pbArguments.add(command);
        pbArguments.add(codePath);
        pbArguments.add(task);
        pbArguments.addAll(args);
        log_.debug(String.format("Run command: %s", pbArguments.stream().reduce("", (s, s2) -> String.format("%s %s", s, s2))));
        ProcessBuilder p = new ProcessBuilder(pbArguments);
        return p.start();
    }
}
