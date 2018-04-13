package ru.lorddux.distasksystem.storage.sql;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class DriverShim implements Driver {

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
class test {
    public void will_not_work() throws Exception {
        URL u = new URL("jar:file:/path/to/pgjdbc2.jar!/");
        String classname = "org.postgresql.Driver";
        URLClassLoader ucl = new URLClassLoader(new URL[]{u});
        Class.forName(classname, true, ucl);
        DriverManager.getConnection("jdbc:postgresql://host/db", "user", "pw");
        // That will throw SQLException: No suitable driver
    }

    public void will_work() throws Exception {
        URL u = new URL("jar:file:var/mysql-connector-java-5.1.46.jar!/");
        String classname = "com.mysql.jdbc.Driver";
        URLClassLoader ucl = new URLClassLoader(new URL[]{u});
        Driver d = (Driver) Class.forName(classname, true, ucl).newInstance();
        DriverManager.registerDriver(new DriverShim(d));
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test?", "root", "");
        PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO test_table (messageId, timestamp, result) VALUES (?, ?, ?)");
        for (int i = 8; i < 11; i++) {
            preparedStatement.setString(1, "riodd"+String.valueOf(i));
            preparedStatement.setInt(2, (int) (System.currentTimeMillis() / 1000));
            preparedStatement.setString(3, "result" + String.valueOf(i));
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
        for (int i = 8; i < 11; i++) {
            preparedStatement.setString(1, "riyd"+String.valueOf(i));
            preparedStatement.setInt(2, (int) (System.currentTimeMillis() / 1000));
            preparedStatement.setString(3, "result" + String.valueOf(i));
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
//        conn.commit();
//         Success!
    }

    public static void main(String[] args) throws Exception {
        test t = new test();
        t.will_work();
    }
}