package ru.hse.lorddux.config;

import lombok.Data;

import java.util.List;

@Data
public class Configuration {
    private static volatile Configuration instance;

    private WorkerType type;
    private List<String> JVMParameters;
    private String queueAddress;
    private String queueAuthorization;
    private String queueAccount;
    private String queueName;
    private Integer queueTaskVisibilityTimeout;
    private String storageAddress;
    private String codeAddress;

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
}