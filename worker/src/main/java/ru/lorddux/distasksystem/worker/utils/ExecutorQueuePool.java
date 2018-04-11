package ru.lorddux.distasksystem.worker.utils;

import com.google.common.collect.Iterators;
import ru.lorddux.distasksystem.worker.executors.PythonExecutor;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExecutorQueuePool<T> implements QueuePool<T>{

    private Iterator<BlockingQueue<T>> queuePool;
    private int queueNum;

    public ExecutorQueuePool(Collection<PythonExecutor> queueHolders, Function<PythonExecutor, BlockingQueue<T>> getQueueFunction) {
        queueNum = queueHolders.size();
        queuePool = Iterators.cycle(
                queueHolders.stream().map(getQueueFunction::apply).collect(Collectors.toList())
        );
    }

    @Override
    public T poll(long sleepTimeMillis) {
        T res;
        int count = 0;
        while ((res = queuePool.next().poll()) == null) {
            count++;
            if (count == queueNum) {
                count = 0;
                try {
                    Thread.sleep(sleepTimeMillis);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        return res;
    }

    @Override
    public void add(T item, long sleepTimeMillis) {
        int count = 0;
        while (!queuePool.next().offer(item)) {
            count++;
            if (count == queueNum) {
                count = 0;
                try {
                    Thread.sleep(sleepTimeMillis);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    @Override
    public int drainNextTo(Collection<T> collection) {
        int res;
        while ((res = queuePool.next().drainTo(collection)) == 0);
        return res;
    }

    @Override
    public void offerAll(Iterable<T> items, long sleepTimeMillis) {
        Iterator<T> it = items.iterator();
        while (it.hasNext()) {
            add(it.next(), sleepTimeMillis);
        }
    }
}
