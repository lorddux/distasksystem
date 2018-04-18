package ru.lorddux.distasksystem.manager.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "workers")
@NoArgsConstructor
@AllArgsConstructor
public class Worker {
    @Id
    private String workerAddress;
    private String authorization;
    private Integer configId;
    private Integer workerCapacity;
}
