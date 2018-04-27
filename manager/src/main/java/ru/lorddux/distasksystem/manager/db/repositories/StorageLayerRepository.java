package ru.lorddux.distasksystem.manager.db.repositories;

import ru.lorddux.distasksystem.manager.db.entity.StorageLayer;
import org.springframework.data.repository.CrudRepository;

public interface StorageLayerRepository extends CrudRepository<StorageLayer, Long> {
    StorageLayer findByAddress(String address);
}
