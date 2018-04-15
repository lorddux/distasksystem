package ru.lorddux.distasksystem.utils;

import java.util.Collection;

public interface CacheManager<T> {
    Collection<T> load();
    Collection<T> loadNext(int size);
    void save(Collection<T> data);
}
