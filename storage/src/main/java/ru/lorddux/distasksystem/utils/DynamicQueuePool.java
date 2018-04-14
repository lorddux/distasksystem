package ru.lorddux.distasksystem.utils;

import java.util.Queue;

public interface DynamicQueuePool<T> extends QueuePool<T>{
    boolean addQueue(Queue<T> queue);
    boolean removeQueue(Queue<T> queue);
}
