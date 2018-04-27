package ru.lorddux.distasksystem.manager.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.lorddux.distasksystem.manager.db.State;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "storage_layers")
@NoArgsConstructor
@AllArgsConstructor
public class StorageLayer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String address;

    private Integer port;

    private String authorization;

    @ManyToOne
    @JoinColumn(name = "config")
    private StorageConfig config;

    @Enumerated(EnumType.STRING)
    private State state;

    private Integer totalStat = 0;
}
