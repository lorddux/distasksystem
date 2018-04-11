package ru.hse.lorddux;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatThread implements Runnable {
    private Logger log_;
    private Long stat;
    private long interval;
    private volatile boolean stop = false;
    private String statMessage;

    public StatThread(final Class clazz, final String statMessageFormat, final Long statVariable, final long statInterval) {
        log_ = LogManager.getLogger(clazz);
        stat = statVariable;
        interval = statInterval;
        statMessage = statMessageFormat;
    }

    void stop() {
        Thread.currentThread().interrupt();
        stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                Thread.sleep(interval);
                log_.info(String.format(statMessage, stat));
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
