package ru.lorddux.distasksystem.storage.sql;

import ru.lorddux.distasksystem.storage.data.WorkerTaskResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatementProcessor {

    private Connection connection;
    private PreparedStatement statement;
    private String sqlQuery;

    public void prepareStatement() throws SQLException {
        statement = connection.prepareStatement(sqlQuery);
        statement.execute();
        connection.commit();
    }

    public void setParameters(WorkerTaskResult parameters) {
        
    }
}
