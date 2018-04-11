package ru.lorddux.distasksystem.worker.config;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {
    private static volatile Configuration instance;

    private StorageLayerConfig storageLayerConfig;
    private CodeConfig codeConfig;
    private QueueConfig queueConfig;
    private List<String> jvmParameters;
    private WorkerType type;
    private Integer workerCapacity;
    private String authorization;

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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CodeConfig {
        private String address;
        private String mainFile;
        private String command;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueConfig {
        private String storageConnectionString;
        private String queueName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StorageLayerConfig {
        private String address;
        private Integer port;
    }

    public enum WorkerType {
        EXECUTOR,
        GENERATOR
    }
}