package ru.hse.lorddux.executor;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.hse.lorddux.utils.PathManager;
import ru.hse.lorddux.config.Configuration;
import ru.hse.lorddux.exception.ExecutorException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@AllArgsConstructor
@RequiredArgsConstructor
public class PythonExecutor implements Executor {
    private static final Logger log_ = LogManager.getLogger(PythonExecutor.class);
    private static final String DEFAULT_PYTHON_COMMAND = "python";
    private static final String DEFAULT_ERROR_OUTPUT_PATH = PathManager.getDefaultClassFilePath(
            PythonExecutor.class, "error.out"
    );
    private static final int SLEEP_TIME = 1000;

    @NonNull private String codeAddress;
    @NonNull private BlockingQueue<String> tasksQueue;
    private String pythonCommand = DEFAULT_PYTHON_COMMAND;
    private static volatile boolean stopFlag = false;

    public boolean dispatch(String task) {
        return tasksQueue.offer(task);
    }

    //TODO
    public void run() {
        Configuration config = Configuration.getInstance();
        String result;
        try {
            while (!stopFlag) {
                if (tasksQueue.isEmpty()) {
                    Thread.sleep(SLEEP_TIME);
                    continue;
                }
                try {
                    result = processTask(tasksQueue.poll());

                } catch (ExecutorException e) {

                }
            }
        } catch (InterruptedException e) {
            log_.warn(String.format("Thread %s was interrupted!", Thread.currentThread().getName()));
        }
    }

    private String processTask(String task) throws ExecutorException {

        return null;
    }

    private Process buildProcess(String codePath, List<String> arguments) throws IOException{
        List<String> pbArguments = Arrays.asList(pythonCommand, codePath);
        pbArguments.addAll(arguments);
        log_.info(String.format("Run command: %s", pbArguments.stream().reduce("", (s, s2) -> String.format("%s %s", s, s2))));
        ProcessBuilder p = new ProcessBuilder(pythonCommand);
        return p.start();
    }

    private Process buildProcess(String codePath) throws IOException{
        return buildProcess(codePath, Collections.emptyList());
    }

    private static void redirectError() {

    }
}
