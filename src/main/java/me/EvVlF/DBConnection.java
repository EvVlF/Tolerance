package me.EvVlF;

import org.h2.tools.RunScript;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class DBConnection {
    private final String H2DB_Path = "jdbc:h2:mem:tolerance;TRACE_LEVEL_FILE=0;TRACE_LEVEL_SYSTEM_OUT=0";
    private Connection connection;

    private static final class DBconnectionSingletonHolder {
        private static final DBConnection DB_CONNECTION_INSTANCE = new DBConnection();
    }

    private DBConnection() {
    }

    static DBConnection getInstance() {
        return DBconnectionSingletonHolder.DB_CONNECTION_INSTANCE;
    }

    void connect() {
        try {
            connection = DriverManager.getConnection(H2DB_Path);
            System.out.println("Соединение с базой данных установлено");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Connection getConnection() {
        return connection;
    }

    void readDBinMem() throws SQLException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("tolerance.sql");
        Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        RunScript.execute(connection, reader);
    }

    void closeConnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

}
