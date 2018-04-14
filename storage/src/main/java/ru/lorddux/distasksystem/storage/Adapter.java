package ru.lorddux.distasksystem.storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Adapter implements Service {
    private static final Logger log_ = LogManager.getLogger(Adapter.class);

    private volatile boolean runningFlag = false;

    @Override
    public synchronized void start() {

    }

    @Override
    public synchronized void stop() {

    }

    private void init() throws Exception {

    }

    @Override
    public boolean isRunning() {
        return runningFlag;
    }
}
