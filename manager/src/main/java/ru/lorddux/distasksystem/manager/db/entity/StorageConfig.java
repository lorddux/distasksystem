package ru.lorddux.distasksystem.manager.db.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name="storage_configs")
@NoArgsConstructor
@AllArgsConstructor
public class StorageConfig implements Serializable {
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

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "config", cascade = CascadeType.ALL)
    private final Set<StorageLayer> storageLayers = new HashSet<>();

    public void addStorage(StorageLayer storageLayer) {
        storageLayers.add(storageLayer);
    }
}
