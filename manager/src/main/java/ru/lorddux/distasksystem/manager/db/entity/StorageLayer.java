package ru.lorddux.distasksystem.manager.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "storage_layers")
@NoArgsConstructor
@AllArgsConstructor
public class StorageLayer {
    @Id
    private String address;
    private String authorization;
    private Integer port;
    private Integer configId;
}
