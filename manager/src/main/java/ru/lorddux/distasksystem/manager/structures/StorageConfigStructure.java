package ru.lorddux.distasksystem.manager.structures;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageConfigStructure implements Serializable {
    private Integer listenPort;
    private String driverAddress;
    private String driverClass;
    private String authorization;
    private String connectionUrl;
    private String connectionUserName;
    private String connectionPassword;
    private String sqlStatement;
}
