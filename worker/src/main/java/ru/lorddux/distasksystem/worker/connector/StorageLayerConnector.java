package ru.lorddux.distasksystem.worker.connector;

public interface StorageLayerConnector extends Runnable {
    void stop();
    long getStat();
}
