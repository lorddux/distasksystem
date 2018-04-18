package ru.lorddux.distasksystem.manager.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "nodes")
@NoArgsConstructor
@AllArgsConstructor
public class NodeState {
    @Id
    private String address;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Integer totalStat;

    @Enumerated(EnumType.STRING)
    private State state;

    public enum State {
        RUN,
        STOP,
        DEAD
    }

    public enum Type {
        WORKER,
        STORAGE
    }
}
