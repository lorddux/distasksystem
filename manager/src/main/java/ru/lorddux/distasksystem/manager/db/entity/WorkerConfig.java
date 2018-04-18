package ru.lorddux.distasksystem.manager.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "worker_configs")
@NoArgsConstructor
@AllArgsConstructor
public class WorkerConfig {
    @Id
    private Long id;

    @NotNull
    private String codeAddress;

    private String codeMainFile;

    @NotNull
    private String codeCommand;

    @NotNull
    private String storageAddress;

    @NotNull
    private Integer storagePort;

    @NotNull
    private String queueConnectionString;

    @NotNull
    private String queueName;
}
