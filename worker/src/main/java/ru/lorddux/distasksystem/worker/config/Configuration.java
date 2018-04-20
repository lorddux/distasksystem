package ru.lorddux.distasksystem.worker.config;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {
    private static volatile Configuration instance;

    private String codeAddress;
    private String codeMainFile;
    private String codeCommand;
    private String storageAddress;
    private Integer storagePort;
    private String queueConnectionString;
    private String queueName;
    private WorkerType workerType;
    private String authorization;
    private Integer workerCapacity;

    public enum WorkerType {
        EXECUTOR,
        GENERATOR
    }

    public static Configuration getInstance() {
        Configuration localInstance = instance;
        if (localInstance == null) {
            synchronized (Configuration.class) {
                localInstance = instance;
            }
        }
        return localInstance;
    }

    public static void setInstance(Configuration newInstance) {
        synchronized (Configuration.class) {
            instance = newInstance;
        }
    }
}