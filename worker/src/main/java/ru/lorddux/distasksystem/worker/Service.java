package ru.lorddux.distasksystem.worker;

public interface Service {

    void start();
    void stop();
    boolean isRunning();
}
