package ru.lorddux.distasksystem.manager.db.repositories;

import ru.lorddux.distasksystem.manager.db.entity.NodeState;
import org.springframework.data.repository.CrudRepository;

public interface NodeStateRepository extends CrudRepository<NodeState, String> {
}
