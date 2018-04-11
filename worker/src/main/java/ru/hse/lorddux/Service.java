package ru.hse.lorddux;

public interface Service {

    void start() throws Exception;
    void stop();
    boolean isRunning();
}
