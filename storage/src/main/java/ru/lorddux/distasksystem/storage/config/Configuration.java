package ru.lorddux.distasksystem.storage.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {
    private static volatile Configuration instance;

    private StorageLayerConfig storageLayerConfig;
    private Integer listenPort;
    private DriverConfig driverConfig;
    private DBConfig queueConfig;
    private List<String> jvmParameters;
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
    public static class DriverConfig {
        private String driverAddress;
        private String driverClass;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DBConfig {
        private String connectionUrl;
        private String connectionPassword;
        private String tableName;
        private String userName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StorageLayerConfig {
        private String address;
        private Integer port;
    }

}