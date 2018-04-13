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

public class PythonExecutor extends ExecutorImpl {
    private static final Logger log_ = LogManager.getLogger(PythonExecutor.class);

    @Setter
    @Getter
    private String codePath;

    @Getter
    private String command;

    public PythonExecutor(String pythonCommand, String mainFile, Integer queueSize) {
        super(queueSize);
        command = pythonCommand;
        codePath = mainFile;
    }

    protected Process buildProcess(String task) throws IOException{
        List<String> pbArguments = new ArrayList<>();
        pbArguments.add(command);
        pbArguments.add(codePath);
        pbArguments.add(task);
        log_.debug(String.format("Run command: %s", pbArguments.stream().reduce("", (s, s2) -> String.format("%s %s", s, s2))));
        ProcessBuilder p = new ProcessBuilder(pbArguments);
        return p.start();
    }
}
