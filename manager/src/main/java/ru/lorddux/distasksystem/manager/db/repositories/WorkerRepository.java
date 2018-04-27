package ru.lorddux.distasksystem.manager.db.repositories;

import ru.lorddux.distasksystem.manager.db.entity.Worker;
import org.springframework.data.repository.CrudRepository;

public interface WorkerRepository extends CrudRepository<Worker, Long> {
    Worker findByAddress(String address);
}
