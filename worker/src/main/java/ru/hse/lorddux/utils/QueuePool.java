package ru.hse.lorddux.utils;

import java.util.Collection;

public interface QueuePool<T> {

    /**
     * gets and removes item from queue queuePool
     * @param sleepTimeMillis - sleep time if all queues are empty
     * @return item polled from queue queuePool
     */
    T poll(long sleepTimeMillis);

    /**
     * drains items from one of queue of this queuePool
     * @param collection destination
     * @return number of elements drained to collection
     */
    int drainNextTo(Collection<T> collection);

    /**
     * adds an item to one of queue of this queuePool
     * @param item - item to add
     */
    void add(T item, long sleepTimeMillis);

    void offerAll(Iterable<T> items, long sleepTimeMillis);
}
