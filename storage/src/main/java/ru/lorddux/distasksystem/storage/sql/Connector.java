package ru.lorddux.distasksystem.storage.sql;

import org.apache.logging.log4j.LogManager;
import ru.lorddux.distasksystem.storage.data.WorkerTaskResult;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class Connector {
    private static final org.apache.logging.log4j.Logger log_ = LogManager.getLogger(Connector.class);
    private static volatile Connector instance;

    private Connection connection;
    private PreparedStatement preparedStatement;

    public static Connector getInstance() {
        Connector localInstance = instance;
        if (localInstance == null) {
            synchronized (Connector.class) {
                localInstance = instance;
            }
        }
        return localInstance;
    }

    public synchronized static Connector createInstance(Driver driver) throws SQLException {
        instance = new Connector(driver);
        return instance;
    }

    private Connector(Driver driver) throws SQLException {
        log_.info("Register jdbc driver");
        DriverManager.registerDriver(new DriverShim(driver));
    }

    public void connect(String url, String user, String password) throws SQLException {
        log_.info("Connecting to " + url);
        connection = DriverManager.getConnection(url, user, password);
    }

    public void prepareStatement(String sqlStatement) throws SQLException {
        preparedStatement = connection.prepareStatement(sqlStatement);
    }

    public void setParameters(WorkerTaskResult result) throws SQLException {
        log_.debug(String.format("Set a new raw with parameters %s", result.toString()));
        preparedStatement.setString(1, result.getTaskId());
    }

    public void executeBatch() throws SQLException {
        log_.debug("Execute batch");
        preparedStatement.executeBatch();
    }

    public void commit() throws SQLException {
        if (! connection.getAutoCommit()) {
            connection.commit();
        }
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }

    private static class DriverShim implements Driver {

        private Driver driver;

        DriverShim(Driver d) {
            this.driver = d;
        }

        @Override
        public boolean acceptsURL(String u) throws SQLException {
            return this.driver.acceptsURL(u);
        }

        @Override
        public Connection connect(String u, Properties p) throws SQLException {
            return this.driver.connect(u, p);
        }

        @Override
        public int getMajorVersion() {
            return this.driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return this.driver.getMinorVersion();
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
            return this.driver.getPropertyInfo(u, p);
        }

        @Override
        public boolean jdbcCompliant() {
            return this.driver.jdbcCompliant();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return this.driver.getParentLogger();
        }
    }
}
