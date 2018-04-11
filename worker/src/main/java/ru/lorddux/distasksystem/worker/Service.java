package ru.lorddux.distasksystem.worker;

public interface Service {

    void start() throws Exception;
    void stop();
    boolean isRunning();
}
