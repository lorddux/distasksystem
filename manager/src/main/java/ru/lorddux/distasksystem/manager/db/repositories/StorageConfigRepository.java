package ru.lorddux.distasksystem.manager.db.repositories;

import ru.lorddux.distasksystem.manager.db.entity.StorageConfig;
import org.springframework.data.repository.CrudRepository;

public interface StorageConfigRepository extends CrudRepository<StorageConfig, Long> {
}
