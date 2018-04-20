package ru.lorddux.distasksystem.storage.config;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {
    private static volatile Configuration instance;

    private Integer listenPort;
    private String driverAddress;
    private String driverClass;
    private String authorization;
    private String connectionUrl;
    private String connectionUserName;
    private String connectionPassword;
    private String sqlStatement;

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