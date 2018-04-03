package ru.hse.lorddux.config;

import lombok.Data;

import java.util.List;

@Data
public class Configuration {
    private static volatile Configuration instance;

    private WorkerType type;
    private List<String> jvmParameters;
    private String storageAddress;
    private CodeConfig codeConfig;
    private QueueConfig queueConfig;

    public static Configuration getInstance() {
        Configuration localInstance = instance;
        if (localInstance == null) {
            synchronized (Configuration.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Configuration();
                }
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
    public static class CodeConfig {
        private String address;
        private String mainFile;
    }

    @Data
    public static class QueueConfig {
        private String storageConnectionString;
        private String queueName;
    }

    public enum WorkerType {
        EXECUTOR,
        GENERATOR
    }
}