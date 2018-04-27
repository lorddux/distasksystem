package ru.lorddux.distasksystem.manager.db.repositories;

import ru.lorddux.distasksystem.manager.db.entity.WorkerConfig;
import org.springframework.data.repository.CrudRepository;

public interface WorkerConfigRepository extends CrudRepository<WorkerConfig, Long> {
}
