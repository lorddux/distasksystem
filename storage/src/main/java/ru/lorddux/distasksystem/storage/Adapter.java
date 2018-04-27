package ru.lorddux.distasksystem.storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lorddux.distasksystem.storage.config.Configuration;
import ru.lorddux.distasksystem.storage.data.WorkerTaskResult;
import ru.lorddux.distasksystem.storage.receiver.TCPReceiver;
import ru.lorddux.distasksystem.storage.receiver.processors.JsonSentenceProcessor;
import ru.lorddux.distasksystem.storage.sql.ConnectionWorker;
import ru.lorddux.distasksystem.storage.sql.Connector;
import ru.lorddux.distasksystem.storage.sql.JDBCLoader;
import ru.lorddux.distasksystem.utils.DynamicQueuePool;
import ru.lorddux.distasksystem.utils.ListDynamicQueuePool;
import ru.lorddux.distasksystem.utils.download.Downloader;
import ru.lorddux.distasksystem.utils.download.DownloaderImpl;

import java.io.File;
import java.sql.Driver;

public class Adapter {
    private static final Logger log_ = LogManager.getLogger(Adapter.class);
    private static final String DRIVER_PATH = "var/driver.jar";
    private static final String SUBFOLDER = "var";
    private static final int BATCH_SIZE = 5000;
    private boolean runningFlag = false;
    private TCPReceiver receiver;
    private ConnectionWorker connectionWorker;
    private Thread receiverThread;
    private Thread connectionThread;

    public long getStat() {
        return connectionWorker.getStat();
    }

    public synchronized void start() {
        log_.info("Initialize services");
        try {
            init();
        } catch (Exception e) {
            log_.fatal("Can not init services", e);
            return;
        }
        receiverThread.start();
        connectionThread.start();
        runningFlag = true;
        log_.info("All services were successfully started");
    }

    public synchronized void stop() {
        receiver.stop();
        connectionWorker.stop();

        joinThread(receiverThread);
        joinThread(connectionThread);
        runningFlag = false;
        log_.info("All services were successfully stopped");
    }

    private void init() throws Exception {
        Configuration configuration = Configuration.getInstance();
        DynamicQueuePool<WorkerTaskResult> pool = new ListDynamicQueuePool<>();
        receiver = new TCPReceiver(configuration.getListenPort(), new JsonSentenceProcessor(), pool);

        File f = new File(SUBFOLDER);
        if (! f.exists()) {
            f.mkdirs();
        }
        Downloader downloader = new DownloaderImpl();
        downloader.download(configuration.getDriverAddress(), DRIVER_PATH);
        Driver driver = JDBCLoader.loadDriver(DRIVER_PATH, configuration.getDriverClass());

        Connector connector = Connector.createInstance(driver);
        connector.connect(
                configuration.getConnectionUrl(),
                configuration.getConnectionUserName(),
                configuration.getConnectionPassword()
        );
        connector.prepareStatement(configuration.getSqlStatement());
        connectionWorker = new ConnectionWorker(pool, BATCH_SIZE, connector);

        receiverThread = new Thread(receiver);
        connectionThread = new Thread(connectionWorker);
    }

    public boolean isRunning() {
        return runningFlag;
    }

    private void joinThread(Thread thread) {
        try {
            thread.interrupt();
            thread.join(2000);
        } catch (InterruptedException e) {

        }
    }
}
