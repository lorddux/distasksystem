package ru.lorddux.distasksystem.worker.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class PipInstaller {
    private static final Logger log_ = LogManager.getLogger(PipInstaller.class);

    public static void installRequirements(String requirementsPath) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("pip", "install", "-r", requirementsPath);
        processBuilder.start();
        Process p = processBuilder.start();
        log_.info(new String(p.getInputStream().readAllBytes()));
        log_.error(new String(p.getErrorStream().readAllBytes()));
    }
}
