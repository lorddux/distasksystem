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
    private DriverConfig driverConfig;
    private String authorization;
    private DBConfig dbConfig;
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
        private String userName;
        private String connectionPassword;
    }

    public static void main(String[] args) {
        Configuration c = new Configuration(1515, new DriverConfig("https://github.com/lorddux/testDriver/raw/master/mysql-connector-java-5.1.46.jar", "com.mysql.jdbc.Driver"), "kek", new DBConfig("jdbc:mysql://localhost/test", "root", ""), "INSERT into test (id, taskId, sentence, resNum, timestamp, result) VALUES\" + \"(?,?,?,?,?,?)");
        System.out.println(new Gson().toJson(c));
    }
}