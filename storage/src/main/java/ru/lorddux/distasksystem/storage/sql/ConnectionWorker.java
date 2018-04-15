package ru.lorddux.distasksystem.storage.sql;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lorddux.distasksystem.Stopable;
import ru.lorddux.distasksystem.storage.data.WorkerTaskResult;
import ru.lorddux.distasksystem.utils.CacheManager;
import ru.lorddux.distasksystem.utils.QueuePool;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class ConnectionWorker implements Stopable {
    private static final Logger log_ = LogManager.getLogger(ConnectionWorker.class);

    @NonNull
    private QueuePool<WorkerTaskResult> queuePool;

    @NonNull
    Integer batchSize;

    @NonNull
    private Connector connector;

    private CacheManager<WorkerTaskResult> cache;
    private volatile boolean stopFlag = false;

    @Override
    public void stop() {
        stopFlag = true;
        try {
            connector.close();
        } catch (SQLException e) {
            log_.warn("An error was occurred while closing connection: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        log_.info("run()");
        processCache();
        List<WorkerTaskResult> results = new ArrayList<>();
        while (! stopFlag) {
            int actual = queuePool.drainTo(results, 5000);
            if (actual == 0) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    log_.info("Thread was interrupted. Exiting");
                    stop();
                }
                continue;
            }
            if (connector.isClosed()) {
                log_.warn("Lost db connection");
                while (connector.isClosed() && !stopFlag) {
                    try {
                        connector.reconnect();
                    } catch (SQLException e) {
                        try {
                            Thread.sleep(5000L);
                        } catch (InterruptedException ie) {
                            stop();
                        }
                    }
                }
            }
            addBatch(results);
            results = new ArrayList<>();
        }
    }

    private void processCache() {
//        Collection<WorkerTaskResult> cacheData = cache.loadNext(5000);
//        while (cacheData.size() > 0 && ! stopFlag) {
//            addBatch(cacheData);
//            cacheData = cache.loadNext(5000);
//        }
    }

    private void addBatch(Collection<WorkerTaskResult> data) {
        for (WorkerTaskResult item :
                data) {
            try {
                connector.setParameters(item);
            } catch (SQLException e) {
                log_.error("Can not prepare statement with values " + item.toString());
            }
        }

        try {
            connector.executeBatch();
            connector.commit();
        } catch (SQLException e) {
            log_.warn("An error was occurred while batch execution: " + e.getMessage());
        }
    }
}
