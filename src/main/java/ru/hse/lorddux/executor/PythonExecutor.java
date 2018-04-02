package ru.hse.lorddux.executor;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.hse.lorddux.structures.TaskItem;
import ru.hse.lorddux.utils.PathManager;
import ru.hse.lorddux.exception.ExecutorException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@AllArgsConstructor
@RequiredArgsConstructor
public class PythonExecutor extends Thread {
    private static final Logger log_ = LogManager.getLogger(PythonExecutor.class);
    private static final String DEFAULT_PYTHON_COMMAND = "python";
    private static final int SLEEP_TIME = 1000;

    @NonNull
    private BlockingQueue<TaskItem> tasksQueue;

    @NonNull
    private BlockingQueue<String> completedTaskIDQueue;

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

    private static volatile boolean stopFlag = false;

    public PythonExecutor(String commandPath, String codePath, List<String> args, Integer queueSize) {
        this(commandPath, codePath, queueSize);
        this.args = args;
    }

    public PythonExecutor(String commandPath, String codePath, Integer queueSize) {
        this.pythonCommand = commandPath;
        this.codePath = codePath;
        tasksQueue = new ArrayBlockingQueue<>(queueSize);
        completedTaskIDQueue = new LinkedBlockingQueue<>();
        resultQueue = new LinkedBlockingQueue<>();
    }

    public void stopThread() {
        stopFlag = false;
    }

    public boolean dispatch(TaskItem task) {
        return tasksQueue.offer(task);
    }

    public int takeCompleteID(Collection<String> collection) {
        return completedTaskIDQueue.drainTo(collection);
    }

    public int takeResults(Collection<String> collection) {
        return resultQueue.drainTo(collection);
    }

    //TODO
    public void run() {
        String result;
        TaskItem task = new TaskItem();
        try {
            while (!stopFlag) {
                if (tasksQueue.isEmpty()) {
                    Thread.sleep(SLEEP_TIME);
                    continue;
                }
                try {
                    task = tasksQueue.poll();
                    result = processTask(task.getMessageText());
                    completedTaskIDQueue.add(task.getMessageId());
                    resultQueue.add(result);
                } catch (ExecutorException e) {
                    log_.error(String.format("Error while executing task %s", task.getMessageId()), e);
                }
            }
        } catch (InterruptedException e) {
            log_.warn(String.format("Thread %s was interrupted!", Thread.currentThread().getName()));
        }
    }

    public String processTask(String taskMsg) throws ExecutorException {
        try {
            Process p = buildProcess(taskMsg);
            return new String(p.getInputStream().readAllBytes());

        } catch (IOException e) {
            throw new ExecutorException(e);
        }
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
