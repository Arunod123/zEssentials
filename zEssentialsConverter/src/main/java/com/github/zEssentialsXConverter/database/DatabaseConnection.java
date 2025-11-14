package com.github.zEssentialsXConverter.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public DatabaseConnection(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        String jdbcUrl = "jdbc:mariadb://" + this.host + ":" + this.port + "/" + this.database;
        return DriverManager.getConnection(jdbcUrl, this.username, this.password);
    }
}
