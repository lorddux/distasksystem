package ru.lorddux.distasksystem.manager.structures;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerConfigStructure implements Serializable {
    private String codeAddress;
    private String codeMainFile;
    private String codeCommand;
    private String storageAddress;
    private Integer storagePort;
    private String queueConnectionString;
    private String queueName;
    private String authorization;
    private Integer workerCapacity;
}
