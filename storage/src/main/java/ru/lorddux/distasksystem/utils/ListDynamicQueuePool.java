package ru.lorddux.distasksystem.utils;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ListDynamicQueuePool<T> implements DynamicQueuePool<T> {

    private List<BlockingQueue<T>> queuePool;
    private AtomicInteger size;

    public ListDynamicQueuePool() {
        queuePool = new LinkedList<>();
        size = new AtomicInteger();
    }

    @Override
    public T poll(long sleepTimeMillis) {
        T res = null;
        int i = 0;
        while (size.get() > 0 && (res = queuePool.get(i % size.get()).poll()) == null) {
            i++;
            if (i >= size.get()) {
                break;
            }
        }
        return res;
    }

    @Override
    public int drainNextTo(Collection<T> collection) {
        return 0;
    }

    @Override
    public void add(T item, long sleepTimeMillis) {

    }

    @Override
    public void offerAll(Iterable<T> items, long sleepTimeMillis) {

    }

    @Override
    public int drainTo(Collection<T> collection, int size) {
        if (this.size.get() == 0) return 0;
        final int step = size / this.size.get();
        int num = 0;
        int i = 0;
        boolean hadElements = false;
        while (this.size.get() > 0 && num < size) {
            i++;
            int locSize = queuePool.get(i % this.size.get()).drainTo(collection, Math.min(step,size - num));
            if (locSize > 0) {
                hadElements = true;
                num += locSize;
            }
            if (i >= this.size.get()) {
                if (! hadElements) break;
                i = 0;
                hadElements = false;
            }
        }
        return num;
    }

    @Override
    public boolean addQueue(Queue<T> queue) {
        if (! queuePool.contains(queue) && queuePool.add((BlockingQueue<T>) queue)) {
            size.incrementAndGet();
            return true;
        }
        return false;
    }

    //##note: blocks current thread until the queue is empty
    @Override
    public boolean removeQueue(Queue<T> queue) {
        if (! queuePool.contains(queue)) return false;
        while (! queue.isEmpty()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                return false;
            }
        }
        if (queuePool.remove(queue)) {
            size.decrementAndGet();
            return true;
        }
        return false;
    }
}
