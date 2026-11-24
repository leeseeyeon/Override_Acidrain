package server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private static final String URL =
            "jdbc:mysql://localhost:3306/wordstheme1";

    private static final String USER = "root";

    private static final String PASS =
            System.getenv("DB_PASSWORD");

    public Connection getConnection() throws SQLException {

        return DriverManager.getConnection(URL, USER, PASS);
    }
}
