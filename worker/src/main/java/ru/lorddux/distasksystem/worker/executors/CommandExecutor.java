package ru.lorddux.distasksystem.worker.executors;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class CommandExecutor extends Executor {
    private static final Logger log_ = LogManager.getLogger(CommandExecutor.class);

    @Getter
    private String command;

    public CommandExecutor(String command, Integer queueSize) {
        super(queueSize);
        this.command = command;
    }

    protected Process buildProcess(String task) throws IOException {
        String finalCommand = String.format(command, task);
        log_.debug(String.format("Run command: %s", finalCommand));
        ProcessBuilder p = new ProcessBuilder(finalCommand.split(" "));
        return p.start();
    }
}
