package ru.lorddux.distasksystem.storage.sql;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lorddux.distasksystem.Stopable;
import ru.lorddux.distasksystem.storage.data.WorkerTaskResult;
import ru.lorddux.distasksystem.utils.QueuePool;

import java.sql.SQLException;

@RequiredArgsConstructor
public class ConnectionWorker implements Stopable {
    private static final Logger log_ = LogManager.getLogger(ConnectionWorker.class);

    @NonNull
    private QueuePool<WorkerTaskResult> queuePool;

    @NonNull
    Integer batchSize;

    @NonNull
    private Connector connector;

    private volatile boolean stopFlag = false;

    @Override
    public void stop() {
        stopFlag = true;
    }

    @Override
    public void run() {
        log_.info("run()");
        Integer currentBatchSize = 0;
        while (! stopFlag) {
            WorkerTaskResult result = queuePool.poll(100L);
            boolean sent = false;
            while (! sent && ! stopFlag) {
                try {
                    connector.setParameters(result);
                    sent = true;
                    currentBatchSize++;
                } catch (SQLException e) {
                    log_.warn("Can not set parameters to prepared statement");
                }
            }
            if (currentBatchSize >= batchSize) {
                try {
                    connector.executeBatch();
                    connector.commit();
                } catch (SQLException e) {
                    log_.error("Can not execute batch");
                    while (connector.isClosed() && ! stopFlag) {
                        try {
                            connector.reconnect();
                        } catch (SQLException ex) {
                            log_.warn("Cant not reconnect to db: " + ex.getMessage());
                        }
                    }
                }
            }
        }
    }

//    private void setConnector() throws SQLException {
//        connector = Connector.createInstance(driver);
//        connector.connect(
//                config.getDbConfig().getConnectionUrl(), config.getDbConfig().getUserName(), config.getDbConfig().getConnectionPassword()
//        );
//        connector.prepareStatement(config.getSqlStatement());
//    }
}
