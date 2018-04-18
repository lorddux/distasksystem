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
@Table(name="storage_configs")
@NoArgsConstructor
@AllArgsConstructor
public class StorageConfig {
    @Id
    private Long id;

    @NotNull
    private String driverAddress;

    @NotNull
    private String driverClass;

    @NotNull
    private String connectionUrl;

    private String connectionUserName;

    private String connectionPassword;

    @NotNull
    private String sqlStatement;
}
