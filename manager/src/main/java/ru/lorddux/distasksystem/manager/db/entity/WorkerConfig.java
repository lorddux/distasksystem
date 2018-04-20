package ru.lorddux.distasksystem.manager.db.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "worker_configs")
@NoArgsConstructor
@AllArgsConstructor
public class WorkerConfig implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "config", cascade = CascadeType.ALL)
    private final Set<Worker> workers = new HashSet<>();

    public void addWorker(Worker worker) {
        workers.add(worker);
    }
}
